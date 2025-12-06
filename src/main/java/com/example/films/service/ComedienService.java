package com.example.films.service;

import com.example.films.dto.ComedienDTO;
import com.example.films.dto.CreateComedienDTO;
import com.example.films.dto.DisponibiliteDTO;
import com.example.films.entity.Comedien;
import com.example.films.entity.DisponibiliteComedien;
import com.example.films.entity.Projet;
import com.example.films.exception.*;
import com.example.films.repository.ComedienRepository;
import com.example.films.repository.DisponibiliteComedienRepository;
import com.example.films.repository.ProjetRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map;

@Service
public class ComedienService {
    private final ComedienRepository comedienRepository;
    private final DisponibiliteComedienRepository disponibiliteRepository;
    private final ProjetRepository projetRepository; 
    private final String uploadDir = "assets/photos/";

    public ComedienService(ComedienRepository comedienRepository, 
                         DisponibiliteComedienRepository disponibiliteRepository,
                         ProjetRepository projetRepository) {
        this.comedienRepository = comedienRepository;
        this.disponibiliteRepository = disponibiliteRepository;
        this.projetRepository = projetRepository;
        
        // Créer le répertoire s'il n'existe pas
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new FileStorageException("Impossible de créer le répertoire de stockage des photos", e);
        }
    }

    @Transactional
    public ComedienDTO createComedien(CreateComedienDTO createComedienDTO) {
        // Vérifier si l'email existe déjà
        if (comedienRepository.findByEmail(createComedienDTO.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Comédien", "email", createComedienDTO.getEmail());
        }

        // Vérifier le projet
        Projet projet = projetRepository.findById(createComedienDTO.getProjetId())
                .orElseThrow(() -> new ResourceNotFoundException("Projet", "id", createComedienDTO.getProjetId()));

        // Validation des données
        validateComedienData(createComedienDTO);

        // Créer le comédien
        Comedien comedien = new Comedien();
        comedien.setProjet(projet);
        comedien.setNom(createComedienDTO.getNom());
        comedien.setAge(createComedienDTO.getAge());
        comedien.setEmail(createComedienDTO.getEmail());
        comedien.setPhotoPath(createComedienDTO.getPhotoPath());

        Comedien savedComedien = comedienRepository.save(comedien);

        // Créer les disponibilités multiples si fournies
        if (createComedienDTO.getDatesDisponibilite() != null && 
            createComedienDTO.getStatutsDisponibilite() != null &&
            !createComedienDTO.getDatesDisponibilite().isEmpty()) {
            
            validateDisponibilites(createComedienDTO.getDatesDisponibilite(), createComedienDTO.getStatutsDisponibilite());
            
            for (int i = 0; i < createComedienDTO.getDatesDisponibilite().size(); i++) {
                if (i < createComedienDTO.getStatutsDisponibilite().size()) {
                    DisponibiliteComedien disponibilite = new DisponibiliteComedien();
                    disponibilite.setComedien(savedComedien);
                    disponibilite.setDate(createComedienDTO.getDatesDisponibilite().get(i));
                    disponibilite.setStatut(createComedienDTO.getStatutsDisponibilite().get(i));
                    disponibiliteRepository.save(disponibilite);
                }
            }
        }

        return convertToDTO(savedComedien);
    }

    private void validateComedienData(CreateComedienDTO dto) {
        if (dto.getNom() == null || dto.getNom().trim().isEmpty()) {
            throw new BusinessValidationException("Le nom du comédien est obligatoire");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new BusinessValidationException("L'email du comédien est obligatoire");
        }
        if (dto.getAge() != null && dto.getAge() < 0) {
            throw new BusinessValidationException("L'âge ne peut pas être négatif");
        }
        if (dto.getAge() != null && dto.getAge() > 120) {
            throw new BusinessValidationException("L'âge n'est pas valide");
        }
    }

    private void validateDisponibilites(List<LocalDate> dates, List<String> statuts) {
        if (dates.size() != statuts.size()) {
            throw new BusinessValidationException("Le nombre de dates et de statuts de disponibilité doit être égal");
        }
        
        for (LocalDate date : dates) {
            if (date == null) {
                throw new BusinessValidationException("Une date de disponibilité est invalide");
            }
            if (date.isBefore(LocalDate.now())) {
                throw new BusinessValidationException("Les dates de disponibilité ne peuvent pas être dans le passé");
            }
        }
        
        for (String statut : statuts) {
            if (statut == null || statut.trim().isEmpty()) {
                throw new BusinessValidationException("Un statut de disponibilité est invalide");
            }
        }
    }

    public List<ComedienDTO> getComediensByProjet(Long projetId) {
        if (!projetRepository.existsById(projetId)) {
            throw new ResourceNotFoundException("Projet", "id", projetId);
        }
        
        List<Comedien> comediens = comedienRepository.findByProjetIdWithDisponibilites(projetId);
        return comediens.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public String savePhoto(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new BusinessValidationException("Le fichier photo est vide");
        }

        // Validation du type de fichier
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessValidationException("Le fichier doit être une image");
        }

        // Validation de la taille (max 5MB)
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new BusinessValidationException("La taille de l'image ne doit pas dépasser 5MB");
        }

        // Générer un nom de fichier unique
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // Chemin complet du fichier
        Path filePath = Paths.get(uploadDir + uniqueFilename);
        
        // Sauvegarder le fichier
        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException e) {
            throw new FileStorageException("Erreur lors de la sauvegarde de la photo", e);
        }

        return uniqueFilename;
    }

    public byte[] getPhoto(String filename) throws IOException {
        Path filePath = Paths.get(uploadDir + filename);
        if (!Files.exists(filePath)) {
            throw new ResourceNotFoundException("Photo", "filename", filename);
        }
        
        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new FileStorageException("Erreur lors de la lecture de la photo", e);
        }
    }

    public void deletePhoto(String filename) throws IOException {
        if (filename != null) {
            Path filePath = Paths.get(uploadDir + filename);
            if (Files.exists(filePath)) {
                try {
                    Files.delete(filePath);
                } catch (IOException e) {
                    throw new FileStorageException("Erreur lors de la suppression de la photo", e);
                }
            }
        }
    }

    public List<ComedienDTO> getAllComediens() {
        List<Comedien> comediens = comedienRepository.findAllWithDisponibilites();
        return comediens.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ComedienDTO getComedienById(Long id) {
        Comedien comedien = comedienRepository.findByIdWithDisponibilites(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comédien", "id", id));
        return convertToDTO(comedien);
    }

    @Transactional
    public ComedienDTO updateComedien(Long id, CreateComedienDTO updateComedienDTO) {
        Comedien comedien = comedienRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comédien", "id", id));

        // Vérifier si l'email est déjà utilisé par un autre comédien
        if (updateComedienDTO.getEmail() != null && 
            !comedien.getEmail().equals(updateComedienDTO.getEmail()) &&
            comedienRepository.findByEmail(updateComedienDTO.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Comédien", "email", updateComedienDTO.getEmail());
        }

        // Validation des données
        if (updateComedienDTO.getAge() != null && updateComedienDTO.getAge() < 0) {
            throw new BusinessValidationException("L'âge ne peut pas être négatif");
        }

        // Mettre à jour seulement les champs non-nulls
        if (updateComedienDTO.getNom() != null) {
            comedien.setNom(updateComedienDTO.getNom());
        }
        if (updateComedienDTO.getAge() != null) {
            comedien.setAge(updateComedienDTO.getAge());
        }
        if (updateComedienDTO.getEmail() != null) {
            comedien.setEmail(updateComedienDTO.getEmail());
        }
        if (updateComedienDTO.getPhotoPath() != null) {
            // Supprimer l'ancienne photo si elle existe
            if (comedien.getPhotoPath() != null) {
                try {
                    deletePhoto(comedien.getPhotoPath());
                } catch (IOException e) {
                    throw new FileStorageException("Erreur lors de la suppression de l'ancienne photo", e);
                }
            }
            comedien.setPhotoPath(updateComedienDTO.getPhotoPath());
        }

        // Sauvegarder le comédien d'abord
        Comedien updatedComedien = comedienRepository.save(comedien);
        
        // Gérer les disponibilités
        handleDisponibilitesUpdate(comedien, updateComedienDTO);

        return convertToDTO(updatedComedien);
    }

    @Transactional
    private void handleDisponibilitesUpdate(Comedien comedien, CreateComedienDTO updateComedienDTO) {
        // Supprimer TOUTES les disponibilités existantes de ce comédien
        disponibiliteRepository.deleteByComedienId(comedien.getId());
        
        // Recréer les disponibilités seulement si des nouvelles sont fournies
        if (updateComedienDTO.getDatesDisponibilite() != null && 
            updateComedienDTO.getStatutsDisponibilite() != null &&
            !updateComedienDTO.getDatesDisponibilite().isEmpty()) {
            
            validateDisponibilites(updateComedienDTO.getDatesDisponibilite(), updateComedienDTO.getStatutsDisponibilite());
            
            for (int i = 0; i < updateComedienDTO.getDatesDisponibilite().size(); i++) {
                if (i < updateComedienDTO.getStatutsDisponibilite().size()) {
                    LocalDate date = updateComedienDTO.getDatesDisponibilite().get(i);
                    String statut = updateComedienDTO.getStatutsDisponibilite().get(i);
                    
                    if (date != null && statut != null) {
                        DisponibiliteComedien newDisponibilite = new DisponibiliteComedien();
                        newDisponibilite.setComedien(comedien);
                        newDisponibilite.setDate(date);
                        newDisponibilite.setStatut(statut);
                        disponibiliteRepository.save(newDisponibilite);
                    }
                }
            }
        }
    }

    @Transactional
    public void deleteComedien(Long id) {
        Comedien comedien = comedienRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comédien", "id", id));
        
        // Supprimer la photo associée
        if (comedien.getPhotoPath() != null) {
            try {
                deletePhoto(comedien.getPhotoPath());
            } catch (IOException e) {
                throw new FileStorageException("Erreur lors de la suppression de la photo du comédien", e);
            }
        }
        
        comedienRepository.delete(comedien);
    }

    @Transactional
    public void addDisponibilite(Long comedienId, LocalDate date, String statut) {
        Comedien comedien = comedienRepository.findById(comedienId)
                .orElseThrow(() -> new ResourceNotFoundException("Comédien", "id", comedienId));

        // Validation
        if (date == null) {
            throw new BusinessValidationException("La date de disponibilité est obligatoire");
        }
        if (date.isBefore(LocalDate.now())) {
            throw new BusinessValidationException("La date de disponibilité ne peut pas être dans le passé");
        }
        if (statut == null || statut.trim().isEmpty()) {
            throw new BusinessValidationException("Le statut de disponibilité est obligatoire");
        }

        // Vérifier si une disponibilité existe déjà pour cette date
        Optional<DisponibiliteComedien> existing = disponibiliteRepository
                .findByComedienIdAndDate(comedienId, date);

        if (existing.isPresent()) {
            throw new DuplicateResourceException("Disponibilité", "date", date);
        }

        DisponibiliteComedien disponibilite = new DisponibiliteComedien(comedien, date, statut);
        disponibiliteRepository.save(disponibilite);
    }

    @Transactional
    public void updateDisponibilite(Long comedienId, Long disponibiliteId, LocalDate date, String statut) {
        Comedien comedien = comedienRepository.findById(comedienId)
            .orElseThrow(() -> new ResourceNotFoundException("Comédien", "id", comedienId));
        
        DisponibiliteComedien disponibilite = disponibiliteRepository.findById(disponibiliteId)
            .orElseThrow(() -> new ResourceNotFoundException("Disponibilité", "id", disponibiliteId));
        
        // Vérifier que la disponibilité appartient bien au comédien
        if (!disponibilite.getComedien().getId().equals(comedienId)) {
            throw new BusinessValidationException("Cette disponibilité n'appartient pas au comédien spécifié");
        }
        
        // Validation
        if (date != null && date.isBefore(LocalDate.now())) {
            throw new BusinessValidationException("La date de disponibilité ne peut pas être dans le passé");
        }
        
        disponibilite.setDate(date != null ? date : disponibilite.getDate());
        disponibilite.setStatut(statut != null ? statut : disponibilite.getStatut());
        
        disponibiliteRepository.save(disponibilite);
    }

    @Transactional
    public void deleteDisponibilite(Long comedienId, Long disponibiliteId) {
        Comedien comedien = comedienRepository.findById(comedienId)
            .orElseThrow(() -> new ResourceNotFoundException("Comédien", "id", comedienId));
        
        DisponibiliteComedien disponibilite = disponibiliteRepository.findById(disponibiliteId)
            .orElseThrow(() -> new ResourceNotFoundException("Disponibilité", "id", disponibiliteId));
        
        // Vérifier que la disponibilité appartient bien au comédien
        if (!disponibilite.getComedien().getId().equals(comedienId)) {
            throw new BusinessValidationException("Cette disponibilité n'appartient pas au comédien spécifié");
        }
        
        disponibiliteRepository.delete(disponibilite);
    }

    private ComedienDTO convertToDTO(Comedien comedien) {
        ComedienDTO dto = new ComedienDTO();
        dto.setId(comedien.getId());
        dto.setNom(comedien.getNom());
        dto.setAge(comedien.getAge());
        dto.setEmail(comedien.getEmail());
        dto.setPhotoPath(comedien.getPhotoPath());
        dto.setCreeLe(comedien.getCreeLe());
        dto.setModifieLe(comedien.getModifieLe());

        // Ajouter les informations du projet
        if (comedien.getProjet() != null) {
            dto.setProjetId(comedien.getProjet().getId());
            dto.setProjetTitre(comedien.getProjet().getTitre());
        }

        // Convertir les disponibilités
        if (comedien.getDisponibilites() != null) {
            List<DisponibiliteDTO> disponibilitesDTO = comedien.getDisponibilites().stream()
                    .map(this::convertDisponibiliteToDTO)
                    .collect(Collectors.toList());
            dto.setDisponibilites(disponibilitesDTO);
        }

        return dto;
    }

    private DisponibiliteDTO convertDisponibiliteToDTO(DisponibiliteComedien disponibilite) {
        DisponibiliteDTO dto = new DisponibiliteDTO();
        dto.setId(disponibilite.getId());
        dto.setDate(disponibilite.getDate());
        dto.setStatut(disponibilite.getStatut());
        return dto;
    }
}