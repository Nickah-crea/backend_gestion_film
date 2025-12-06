// RaccordExportService.java
package com.example.films.service;

import com.example.films.dto.RaccordExportDTO;
import com.example.films.dto.RaccordImageDTO;
import com.example.films.entity.*;
import com.example.films.repository.PlanningTournageRepository;
import com.example.films.repository.RaccordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList; // Ajouter cet import
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RaccordExportService {
    
    private final RaccordRepository raccordRepository;
    private final PlanningTournageRepository planningTournageRepository;
    
    @Transactional(readOnly = true)
    public List<RaccordExportDTO> getRaccordsForExportByComedien(Long comedienId) {
        List<Raccord> raccords = raccordRepository.findRaccordsAccessoiresByComedien(comedienId);
        return raccords.stream()
                .map(this::convertToExportDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<RaccordExportDTO> getRaccordsForExportByProjet(Long projetId) {
        List<Raccord> raccords = raccordRepository.findByProjetId(projetId);
        return raccords.stream()
                .map(this::convertToExportDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<RaccordExportDTO> getRaccordsForExportByType(String typeCode) {
        // Filtrer par type (accessoire, vetements, coiffure, etc.)
        List<Raccord> allRaccords = raccordRepository.findAllWithDetails();
        return allRaccords.stream()
                .filter(raccord -> typeCode.equals(raccord.getTypeRaccord().getCode()))
                .map(this::convertToExportDTO)
                .collect(Collectors.toList());
    }
    
    public RaccordExportDTO convertToExportDTO(Raccord raccord) {
        RaccordExportDTO dto = new RaccordExportDTO();
        dto.setId(raccord.getId());
        
        // DEBUG : Afficher la structure des images
        System.out.println("=== DEBUG RACCORD " + raccord.getId() + " ===");
        if (raccord.getImages() != null) {
            System.out.println("Nombre d'images: " + raccord.getImages().size());
            for (RaccordImage image : raccord.getImages()) {
                System.out.println("Image - ID: " + image.getId() + 
                                 ", NomFichier: " + image.getNomFichier() +
                                 ", Chemin: " + image.getCheminFichier());
            }
        } else {
            System.out.println("Aucune image");
        }
        
        // Informations de base
        if (raccord.getSceneSource() != null) {
            dto.setSceneSourceTitre(raccord.getSceneSource().getTitre());
            // Navigation vers le projet via la scène source
            if (raccord.getSceneSource().getSequence() != null) {
                dto.setSequenceTitre(raccord.getSceneSource().getSequence().getTitre());
                if (raccord.getSceneSource().getSequence().getEpisode() != null) {
                    dto.setEpisodeTitre(raccord.getSceneSource().getSequence().getEpisode().getTitre());
                    if (raccord.getSceneSource().getSequence().getEpisode().getProjet() != null) {
                        dto.setProjetTitre(raccord.getSceneSource().getSequence().getEpisode().getProjet().getTitre());
                    }
                }
            }
        }
        
        if (raccord.getSceneCible() != null) {
            dto.setSceneCibleTitre(raccord.getSceneCible().getTitre());
        }
        
        // Type de raccord
        if (raccord.getTypeRaccord() != null) {
            dto.setTypeRaccordNom(raccord.getTypeRaccord().getNomType());
        }
        
        dto.setDescription(raccord.getDescription());
        dto.setEstCritique(raccord.getEstCritique());
        
        // Statut
        if (raccord.getStatutRaccord() != null) {
            dto.setStatutRaccordNom(raccord.getStatutRaccord().getNomStatut());
        }
        
        // Personnage et comédien
        if (raccord.getPersonnage() != null) {
            dto.setPersonnageNom(raccord.getPersonnage().getNom());
        }
        
        if (raccord.getComedien() != null) {
            dto.setComedienNom(raccord.getComedien().getNom());
        }
        
        // Dates de tournage - IMPORTANT: utiliser PlanningTournage
        planningTournageRepository.findBySceneId(raccord.getSceneSource().getId())
            .stream().findFirst()
            .ifPresent(planning -> dto.setDateTournageSource(planning.getDateTournage()));
            
        planningTournageRepository.findBySceneId(raccord.getSceneCible().getId())
            .stream().findFirst()
            .ifPresent(planning -> dto.setDateTournageCible(planning.getDateTournage()));
        
        // CORRECTION DES IMAGES - Vérifier la structure
        if (raccord.getImages() != null && !raccord.getImages().isEmpty()) {
            List<RaccordImageDTO> imageDTOs = new ArrayList<>();
            
            for (RaccordImage image : raccord.getImages()) {
                RaccordImageDTO imageDTO = new RaccordImageDTO();
                imageDTO.setId(image.getId());
                
                // CORRECTION CRITIQUE : Vérifier et nettoyer le nom de fichier
                String nomFichier = image.getNomFichier();
                if (nomFichier != null && !nomFichier.contains("undefined")) {
                    imageDTO.setNomFichier(nomFichier);
                    imageDTO.setCheminFichier(image.getCheminFichier());
                    imageDTO.setDescriptionImage(image.getDescriptionImage());
                    imageDTO.setEstImageReference(image.getEstImageReference());
                    imageDTO.setCreeLe(image.getCreeLe());
                    
                    imageDTOs.add(imageDTO);
                } else {
                    System.out.println("Fichier ignoré - nom invalide: " + nomFichier);
                }
            }
            
            dto.setImages(imageDTOs);
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
        
        // Ajouter l'ID du type de raccord pour le filtrage
        if (image.getRaccord() != null && image.getRaccord().getTypeRaccord() != null) {
            dto.setTypeRaccordId(image.getRaccord().getTypeRaccord().getId());
        }
        
        return dto;
    }

    // Ajouter cette méthode dans RaccordExportService.java
    @Transactional(readOnly = true)
    public List<RaccordExportDTO> getRaccordsForExportByScene(Long sceneId) {
        List<Raccord> raccords = raccordRepository.findBySceneId(sceneId);
        return raccords.stream()
                .map(this::convertToExportDTO)
                .collect(Collectors.toList());
    }
}


