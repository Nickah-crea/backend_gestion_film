package com.example.films.service;

import com.example.films.dto.*;
import com.example.films.entity.*;
import com.example.films.repository.*;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RaccordService {
    
    private final RaccordRepository raccordRepository;
    private final TypeRaccordRepository typeRaccordRepository;
    private final StatutRaccordRepository statutRaccordRepository;
    private final RaccordImageRepository raccordImageRepository;
    private final SceneRepository sceneRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PersonnageRepository personnageRepository;
    private final ComedienRepository comedienRepository;
    private final SceneTournageRepository sceneTournageRepository;
    
    private final String uploadDir = "assets/raccords/";
    
   @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer le répertoire de stockage", e);
        }
    }
    
   
    @Transactional(readOnly = true)
    public List<RaccordDTO> getAllRaccords() {
        List<Raccord> raccords = raccordRepository.findAllWithDetails();
        return raccords.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    

    @Transactional(readOnly = true)
    public List<RaccordDTO> getRaccordsByScene(Long sceneId) {
        List<Raccord> raccords = raccordRepository.findBySceneId(sceneId);
        return raccords.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public RaccordDTO getRaccordById(Long id) {
        Raccord raccord = raccordRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Raccord non trouvé"));
        return convertToDTO(raccord);
    }
    
    @Transactional
    public RaccordDTO createRaccord(CreateRaccordDTO createRaccordDTO) {
         System.out.println("Création raccord - SceneSource: " + createRaccordDTO.getSceneSourceId());
        System.out.println("Création raccord - SceneCible: " + createRaccordDTO.getSceneCibleId());
        
        // Vérifier si le raccord existe déjà seulement si les scènes sont différentes
        if (!createRaccordDTO.getSceneSourceId().equals(createRaccordDTO.getSceneCibleId())) {
            if (raccordRepository.existsByScenesAndType(
                    createRaccordDTO.getSceneSourceId(),
                    createRaccordDTO.getSceneCibleId(),
                    createRaccordDTO.getTypeRaccordId())) {
                throw new RuntimeException("Un raccord de ce type existe déjà entre ces scènes");
            }
        } else {
            // Pour les raccords sur la même scène, on peut avoir plusieurs types différents
            // Mais on évite les doublons exacts
            if (raccordRepository.existsBySameSceneAndType(
                    createRaccordDTO.getSceneSourceId(),
                    createRaccordDTO.getTypeRaccordId())) {
                throw new RuntimeException("Un raccord de ce type existe déjà pour cette scène");
            }
        }
        
        // Récupérer les entités
        Scene sceneSource = sceneRepository.findById(createRaccordDTO.getSceneSourceId())
                .orElseThrow(() -> new RuntimeException("Scène source non trouvée"));
        Scene sceneCible = sceneRepository.findById(createRaccordDTO.getSceneCibleId())
                .orElseThrow(() -> new RuntimeException("Scène cible non trouvée"));
        TypeRaccord typeRaccord = typeRaccordRepository.findById(createRaccordDTO.getTypeRaccordId())
                .orElseThrow(() -> new RuntimeException("Type de raccord non trouvé"));
        StatutRaccord statutRaccord = statutRaccordRepository.findById(createRaccordDTO.getStatutRaccordId())
                .orElseThrow(() -> new RuntimeException("Statut de raccord non trouvé"));

        // Récupérer le personnage (peut être null)
        Personnage personnage = null;
        if (createRaccordDTO.getPersonnageId() != null) {
            personnage = personnageRepository.findById(createRaccordDTO.getPersonnageId())
                    .orElseThrow(() -> new RuntimeException("Personnage non trouvé"));
        }
        
        // Récupérer le comédien (peut être null)
        Comedien comedien = null;
        if (createRaccordDTO.getComedienId() != null) {
            comedien = comedienRepository.findById(createRaccordDTO.getComedienId())
                    .orElseThrow(() -> new RuntimeException("Comédien non trouvé"));
        }

        // Créer le raccord
        Raccord raccord = new Raccord();
        raccord.setSceneSource(sceneSource);
        raccord.setSceneCible(sceneCible);
        raccord.setTypeRaccord(typeRaccord);
        raccord.setDescription(createRaccordDTO.getDescription());
        raccord.setEstCritique(createRaccordDTO.getEstCritique());
        raccord.setStatutRaccord(statutRaccord);
        raccord.setPersonnage(personnage); 
        raccord.setComedien(comedien);
        
        Raccord savedRaccord = raccordRepository.save(raccord);
        
        // Sauvegarder les images
        if (createRaccordDTO.getImages() != null) {
            for (MultipartFile image : createRaccordDTO.getImages()) {
                if (!image.isEmpty()) {
                    try {
                        String imagePath = saveImage(image);
                        RaccordImage raccordImage = new RaccordImage();
                        raccordImage.setRaccord(savedRaccord);
                        raccordImage.setNomFichier(imagePath);                         raccordImage.setCheminFichier(imagePath);
                        raccordImage.setDescriptionImage("Image de référence pour le raccord");
                        raccordImage.setEstImageReference(true);
                        raccordImageRepository.save(raccordImage);
                    } catch (IOException e) {
                        throw new RuntimeException("Erreur lors de la sauvegarde de l'image", e);
                    }
                }
            }
        }
        
        return convertToDTO(savedRaccord);
    }
    
     // des méthodes de filtrage
    @Transactional(readOnly = true)
    public List<RaccordDTO> getRaccordsByPersonnage(Long personnageId) {
        List<Raccord> raccords = raccordRepository.findByPersonnageId(personnageId);
        return raccords.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RaccordDTO> getRaccordsByComedien(Long comedienId) {
        List<Raccord> raccords = raccordRepository.findByComedienId(comedienId);
        return raccords.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RaccordDTO> getRaccordsSansPersonnage() {
        List<Raccord> raccords = raccordRepository.findByPersonnageIsNull();
        return raccords.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public RaccordDTO updateRaccord(Long id, CreateRaccordDTO updateRaccordDTO) {
        Raccord raccord = raccordRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Raccord non trouvé"));
        
        // Mettre à jour tous les champs
        if (updateRaccordDTO.getSceneSourceId() != null) {
            Scene sceneSource = sceneRepository.findById(updateRaccordDTO.getSceneSourceId())
                    .orElseThrow(() -> new RuntimeException("Scène source non trouvée"));
            raccord.setSceneSource(sceneSource);
        }
        
        if (updateRaccordDTO.getSceneCibleId() != null) {
            Scene sceneCible = sceneRepository.findById(updateRaccordDTO.getSceneCibleId())
                    .orElseThrow(() -> new RuntimeException("Scène cible non trouvée"));
            raccord.setSceneCible(sceneCible);
        }
        
        if (updateRaccordDTO.getTypeRaccordId() != null) {
            TypeRaccord typeRaccord = typeRaccordRepository.findById(updateRaccordDTO.getTypeRaccordId())
                    .orElseThrow(() -> new RuntimeException("Type de raccord non trouvé"));
            raccord.setTypeRaccord(typeRaccord);
        }
        
        if (updateRaccordDTO.getDescription() != null) {
            raccord.setDescription(updateRaccordDTO.getDescription());
        }
        
        if (updateRaccordDTO.getEstCritique() != null) {
            raccord.setEstCritique(updateRaccordDTO.getEstCritique());
        }
        
        if (updateRaccordDTO.getStatutRaccordId() != null) {
            StatutRaccord statutRaccord = statutRaccordRepository.findById(updateRaccordDTO.getStatutRaccordId())
                    .orElseThrow(() -> new RuntimeException("Statut de raccord non trouvé"));
            raccord.setStatutRaccord(statutRaccord);
        }
        
        // Mettre à jour le personnage
        if (updateRaccordDTO.getPersonnageId() != null) {
            Personnage personnage = personnageRepository.findById(updateRaccordDTO.getPersonnageId())
                    .orElseThrow(() -> new RuntimeException("Personnage non trouvé"));
            raccord.setPersonnage(personnage);
        } else {
            raccord.setPersonnage(null);
        }
        
        // Mettre à jour le comédien
        if (updateRaccordDTO.getComedienId() != null) {
            Comedien comedien = comedienRepository.findById(updateRaccordDTO.getComedienId())
                    .orElseThrow(() -> new RuntimeException("Comédien non trouvé"));
            raccord.setComedien(comedien);
        } else {
            raccord.setComedien(null);
        }
        
        raccord.setModifieLe(LocalDateTime.now());
        
        Raccord updatedRaccord = raccordRepository.save(raccord);
        
        // Ajouter de nouvelles images
        if (updateRaccordDTO.getImages() != null && !updateRaccordDTO.getImages().isEmpty()) {
            for (MultipartFile image : updateRaccordDTO.getImages()) {
                if (!image.isEmpty()) {
                    try {
                        String imagePath = saveImage(image);
                        RaccordImage raccordImage = new RaccordImage();
                        raccordImage.setRaccord(updatedRaccord);
                        raccordImage.setNomFichier(imagePath);                         raccordImage.setCheminFichier(imagePath);
                        raccordImage.setDescriptionImage("Image ajoutée lors de la modification");
                        raccordImage.setEstImageReference(false);
                        raccordImageRepository.save(raccordImage);
                    } catch (IOException e) {
                        throw new RuntimeException("Erreur lors de la sauvegarde de l'image", e);
                    }
                }
            }
        }
        
        return convertToDTO(updatedRaccord);
    }

    @Transactional(readOnly = true)
    public Raccord findRaccordWithDetails(Long id) {
        return raccordRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Raccord non trouvé"));
    }
    
    @Transactional
    public void deleteRaccord(Long id) {
        Raccord raccord = raccordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Raccord non trouvé"));
        
        // Supprimer les images associées
        for (RaccordImage image : raccord.getImages()) {
            try {
                deleteImage(image.getCheminFichier());
            } catch (IOException e) {
                System.err.println("Erreur lors de la suppression de l'image: " + e.getMessage());
            }
        }
        
        raccordRepository.delete(raccord);
    }
    
    private String saveImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("Le fichier est vide");
        }
        
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        Path filePath = Paths.get(uploadDir + uniqueFilename);
        
        Files.copy(file.getInputStream(), filePath);
        return uniqueFilename;
    }
    
    private void deleteImage(String filename) throws IOException {
        if (filename != null) {
            Path filePath = Paths.get(uploadDir + filename);
            Files.deleteIfExists(filePath);
        }
    }
    
    public byte[] getImage(String filename) throws IOException {
        Path filePath = Paths.get(uploadDir + filename);
        return Files.readAllBytes(filePath);
    }
    
    private RaccordDTO convertToDTO(Raccord raccord) {
        RaccordDTO dto = new RaccordDTO();
        dto.setId(raccord.getId());
        dto.setSceneSourceId(raccord.getSceneSource().getId());
        dto.setSceneSourceTitre(raccord.getSceneSource().getTitre());
        dto.setSceneCibleId(raccord.getSceneCible().getId());
        dto.setSceneCibleTitre(raccord.getSceneCible().getTitre());
        dto.setTypeRaccordId(raccord.getTypeRaccord().getId());
        dto.setTypeRaccordNom(raccord.getTypeRaccord().getNomType());
        dto.setDescription(raccord.getDescription());
        dto.setEstCritique(raccord.getEstCritique());
        dto.setStatutRaccordId(raccord.getStatutRaccord().getId());
        dto.setStatutRaccordNom(raccord.getStatutRaccord().getNomStatut());
        dto.setCreeLe(raccord.getCreeLe());
        dto.setModifieLe(raccord.getModifieLe());
        
        if (raccord.getPersonnage() != null) {
            dto.setPersonnageId(raccord.getPersonnage().getId());
            dto.setPersonnageNom(raccord.getPersonnage().getNom());
        }

         if (raccord.getComedien() != null) {
            dto.setComedienId(raccord.getComedien().getId());
            dto.setComedienNom(raccord.getComedien().getNom());
        }

        // Convertir les images
        if (raccord.getImages() != null) {
            List<RaccordImageDTO> imagesDTO = raccord.getImages().stream()
                    .map(this::convertImageToDTO)
                    .collect(Collectors.toList());
            dto.setImages(imagesDTO);
        }
        
        return dto;
    }
    
    private RaccordImageDTO convertImageToDTO(RaccordImage image) {
        RaccordImageDTO dto = new RaccordImageDTO();
        dto.setId(image.getId());
        dto.setNomFichier(image.getNomFichier());
        dto.setCheminFichier(image.getCheminFichier());
        dto.setDescriptionImage(image.getDescriptionImage());
        dto.setEstImageReference(image.getEstImageReference());
        dto.setCreeLe(image.getCreeLe());
        return dto;
    }
    
    @Transactional(readOnly = true)
    public List<TypeRaccord> getAllTypesRaccord() {
        return typeRaccordRepository.findAllByOrderByNomType();
    }
    
    @Transactional(readOnly = true)
    public List<StatutRaccord> getAllStatutsRaccord() {
        return statutRaccordRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<RaccordDTO> getRaccordsAvecAlertes() {
        List<Raccord> raccords = raccordRepository.findAllWithDetails();
        return raccords.stream()
                .map(raccord -> {
                    RaccordDTO dto = convertToDTO(raccord);
                    // Ajouter les informations de tournage
                    Optional<SceneTournage> tournageSource = sceneTournageRepository.findBySceneId(raccord.getSceneSource().getId());
                    Optional<SceneTournage> tournageCible = sceneTournageRepository.findBySceneId(raccord.getSceneCible().getId());
                    
                    tournageSource.ifPresent(st -> {
                        dto.setDateTournageSource(st.getDateTournage());
                        dto.setStatutTournageSource(st.getStatutTournage());
                    });
                    
                    tournageCible.ifPresent(st -> {
                        dto.setDateTournageCible(st.getDateTournage());
                        dto.setStatutTournageCible(st.getStatutTournage());
                    });
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TypeRaccordDTO> getAllTypesRaccordDTO() {
        List<TypeRaccord> types = typeRaccordRepository.findAllByOrderByNomType();
        return types.stream()
                .map(type -> {
                    TypeRaccordDTO dto = new TypeRaccordDTO();
                    dto.setId(type.getId());
                    dto.setCode(type.getCode());
                    dto.setNomType(type.getNomType());
                    dto.setDescription(type.getDescription());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
}