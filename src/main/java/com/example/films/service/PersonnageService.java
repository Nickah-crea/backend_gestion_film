package com.example.films.service;

import com.example.films.dto.CreatePersonnageDTO;
import com.example.films.dto.PersonnageDTO;
import com.example.films.entity.Comedien;
import com.example.films.entity.Personnage;
import com.example.films.entity.Projet;
import com.example.films.repository.ComedienRepository;
import com.example.films.repository.PersonnageRepository;
import com.example.films.repository.ProjetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonnageService {
    
    private final PersonnageRepository personnageRepository;
    private final ProjetRepository projetRepository;
    private final ComedienRepository comedienRepository;
    
    public PersonnageService(PersonnageRepository personnageRepository,
                           ProjetRepository projetRepository,
                           ComedienRepository comedienRepository) {
        this.personnageRepository = personnageRepository;
        this.projetRepository = projetRepository;
        this.comedienRepository = comedienRepository;
    }
    
    @Transactional
    public PersonnageDTO createPersonnage(CreatePersonnageDTO createPersonnageDTO) {
        // Vérifier si le personnage existe déjà dans ce projet
        if (personnageRepository.existsByNomAndProjetId(
            createPersonnageDTO.getNom(), 
            createPersonnageDTO.getProjetId())) {
            throw new RuntimeException("Un personnage avec ce nom existe déjà dans ce projet");
        }
        
        // Récupérer le projet
        Projet projet = projetRepository.findById(createPersonnageDTO.getProjetId())
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));
        
        // Récupérer le comédien (peut être null)
        Comedien comedien = null;
        if (createPersonnageDTO.getComedienId() != null) {
            comedien = comedienRepository.findById(createPersonnageDTO.getComedienId())
                    .orElseThrow(() -> new RuntimeException("Comédien non trouvé"));
        }
        
        // Créer le personnage
        Personnage personnage = new Personnage();
        personnage.setNom(createPersonnageDTO.getNom());
        personnage.setDescription(createPersonnageDTO.getDescription());
        personnage.setProjet(projet);
        personnage.setComedien(comedien); // Peut être null
        
        Personnage savedPersonnage = personnageRepository.save(personnage);
        return convertToDTO(savedPersonnage);
    }
    
    public List<PersonnageDTO> getAllPersonnages() {
        List<Personnage> personnages = personnageRepository.findAllWithDetails();
        return personnages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public PersonnageDTO getPersonnageById(Long id) {
        Personnage personnage = personnageRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Personnage non trouvé"));
        return convertToDTO(personnage);
    }
    
    @Transactional
        public PersonnageDTO updatePersonnage(Long id, CreatePersonnageDTO updatePersonnageDTO) {
        Personnage personnage = personnageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personnage non trouvé"));
        
        // Vérifier si le nom est déjà utilisé par un autre personnage dans le même projet
        if (!personnage.getNom().equals(updatePersonnageDTO.getNom()) &&
            personnageRepository.existsByNomAndProjetId(
                updatePersonnageDTO.getNom(), 
                updatePersonnageDTO.getProjetId())) {
            throw new RuntimeException("Un autre personnage utilise déjà ce nom dans ce projet");
        }
        
        // Récupérer le projet
        Projet projet = projetRepository.findById(updatePersonnageDTO.getProjetId())
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));
        
        // Récupérer le comédien (peut être null)
        Comedien comedien = null;
        if (updatePersonnageDTO.getComedienId() != null) {
            comedien = comedienRepository.findById(updatePersonnageDTO.getComedienId())
                    .orElseThrow(() -> new RuntimeException("Comédien non trouvé"));
        }
        
        personnage.setNom(updatePersonnageDTO.getNom());
        personnage.setDescription(updatePersonnageDTO.getDescription());
        personnage.setProjet(projet);
        personnage.setComedien(comedien); // Peut être null
        
        Personnage updatedPersonnage = personnageRepository.save(personnage);
        return convertToDTO(updatedPersonnage);
    }
    
    @Transactional
    public void deletePersonnage(Long id) {
        Personnage personnage = personnageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personnage non trouvé"));
        personnageRepository.delete(personnage);
    }
    
    public List<PersonnageDTO> getPersonnagesByProjet(Long projetId) {
        List<Personnage> personnages = personnageRepository.findByProjetId(projetId);
        return personnages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<PersonnageDTO> getPersonnagesByComedien(Long comedienId) {
        List<Personnage> personnages = personnageRepository.findByComedienId(comedienId);
        return personnages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PersonnageDTO> getPersonnagesSansComedien() {
        List<Personnage> personnages = personnageRepository.findByComedienIsNull();
        return personnages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private PersonnageDTO convertToDTO(Personnage personnage) {
        PersonnageDTO dto = new PersonnageDTO();
        dto.setId(personnage.getId());
        dto.setNom(personnage.getNom());
        dto.setDescription(personnage.getDescription());
        dto.setCreeLe(personnage.getCreeLe());
        dto.setModifieLe(personnage.getModifieLe());
        
        if (personnage.getProjet() != null) {
            dto.setProjetId(personnage.getProjet().getId());
            dto.setProjetTitre(personnage.getProjet().getTitre());
        }
        
        if (personnage.getComedien() != null) {
            dto.setComedienId(personnage.getComedien().getId());
            dto.setComedienNom(personnage.getComedien().getNom());
        } else {
            dto.setComedienId(null);
            dto.setComedienNom("Aucun comédien");
        }
        
        return dto;
    }
}
