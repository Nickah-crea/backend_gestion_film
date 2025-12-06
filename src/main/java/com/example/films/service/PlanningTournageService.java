package com.example.films.service;

import com.example.films.dto.*;
import com.example.films.entity.*;
import com.example.films.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanningTournageService {
    private final PlanningTournageRepository planningTournageRepository;
    private final SceneRepository sceneRepository;
    private final StatutPlanningRepository statutPlanningRepository;
    private final LieuRepository lieuRepository; // Added missing repository
    private final PlateauRepository plateauRepository; // Added missing repository

    public PlanningTournageService(PlanningTournageRepository planningTournageRepository,
                                 SceneRepository sceneRepository,
                                 StatutPlanningRepository statutPlanningRepository,
                                 LieuRepository lieuRepository, // Added in constructor
                                 PlateauRepository plateauRepository) { // Added in constructor
        this.planningTournageRepository = planningTournageRepository;
        this.sceneRepository = sceneRepository;
        this.statutPlanningRepository = statutPlanningRepository;
        this.lieuRepository = lieuRepository; 
        this.plateauRepository = plateauRepository; 
    }

    public List<PlanningTournageDTO> getPlanningByDateRange(LocalDate startDate, LocalDate endDate) {
        List<PlanningTournage> planning = planningTournageRepository.findByDateTournageBetween(startDate, endDate);
        return planning.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<PlanningTournageDTO> getPlanningByProjetId(Long projetId) {
        List<PlanningTournage> planning = planningTournageRepository.findByProjetId(projetId);
        return planning.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<PlanningTournageDTO> getPlanningBySceneId(Long sceneId) {
        List<PlanningTournage> planning = planningTournageRepository.findBySceneId(sceneId);
        return planning.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public PlanningTournageDTO getPlanningById(Long id) {
        PlanningTournage planning = planningTournageRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Planning de tournage non trouvé"));
        return convertToDTO(planning);
    }

    @Transactional
    public PlanningTournageDTO createPlanningTournage(CreatePlanningTournageDTO createDTO) {
        PlanningTournage planning = new PlanningTournage();
        
        // CORRECTION: Utiliser findByIdWithDetails au lieu de findByIdWithFullDetails
        Scene scene = sceneRepository.findByIdWithDetails(createDTO.getSceneId())
                .orElseThrow(() -> new RuntimeException("Scène non trouvée"));
        planning.setScene(scene);
        
        planning.setDateTournage(createDTO.getDateTournage());
        planning.setHeureDebut(createDTO.getHeureDebut());
        planning.setHeureFin(createDTO.getHeureFin());
        planning.setDescription(createDTO.getDescription());
        
        if (createDTO.getLieuId() != null) {
            Lieu lieu = lieuRepository.findById(createDTO.getLieuId())
                    .orElseThrow(() -> new RuntimeException("Lieu non trouvé"));
            planning.setLieu(lieu);
        }
        
        if (createDTO.getPlateauId() != null) {
            Plateau plateau = plateauRepository.findById(createDTO.getPlateauId())
                    .orElseThrow(() -> new RuntimeException("Plateau non trouvé"));
            planning.setPlateau(plateau);
        }
        
        StatutPlanning statut = statutPlanningRepository.findById(createDTO.getStatutId())
                .orElseThrow(() -> new RuntimeException("Statut non trouvé"));
        planning.setStatut(statut);
        
        PlanningTournage savedPlanning = planningTournageRepository.save(planning);
        return convertToDTO(savedPlanning);
    }

    @Transactional
    public PlanningTournageDTO updatePlanningTournage(Long id, CreatePlanningTournageDTO updateDTO) {
        PlanningTournage planning = planningTournageRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Planning de tournage non trouvé"));
        
        if (!planning.getScene().getId().equals(updateDTO.getSceneId())) {
            Scene scene = sceneRepository.findByIdWithFullDetails(updateDTO.getSceneId())
                    .orElseThrow(() -> new RuntimeException("Scène non trouvée"));
            planning.setScene(scene);
        }
        
        planning.setDateTournage(updateDTO.getDateTournage());
        planning.setHeureDebut(updateDTO.getHeureDebut());
        planning.setHeureFin(updateDTO.getHeureFin());
        planning.setDescription(updateDTO.getDescription());
        
        if (!planning.getStatut().getId().equals(updateDTO.getStatutId())) {
            StatutPlanning statut = statutPlanningRepository.findById(updateDTO.getStatutId())
                    .orElseThrow(() -> new RuntimeException("Statut non trouvé"));
            planning.setStatut(statut);
        }
        
        // Update lieu if provided
        if (updateDTO.getLieuId() != null && 
            (planning.getLieu() == null || !planning.getLieu().getId().equals(updateDTO.getLieuId()))) {
            Lieu lieu = lieuRepository.findById(updateDTO.getLieuId())
                    .orElseThrow(() -> new RuntimeException("Lieu non trouvé"));
            planning.setLieu(lieu);
        }
        
        // Update plateau if provided
        if (updateDTO.getPlateauId() != null && 
            (planning.getPlateau() == null || !planning.getPlateau().getId().equals(updateDTO.getPlateauId()))) {
            Plateau plateau = plateauRepository.findById(updateDTO.getPlateauId())
                    .orElseThrow(() -> new RuntimeException("Plateau non trouvé"));
            planning.setPlateau(plateau);
        }
        
        PlanningTournage updatedPlanning = planningTournageRepository.save(planning);
        return convertToDTO(updatedPlanning);
    }

    @Transactional
    public void deletePlanningTournage(Long id) {
        PlanningTournage planning = planningTournageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Planning de tournage non trouvé"));
        planningTournageRepository.delete(planning);
    }

    private PlanningTournageDTO convertToDTO(PlanningTournage planning) {
        PlanningTournageDTO dto = new PlanningTournageDTO();
        dto.setId(planning.getId());
        dto.setSceneId(planning.getScene().getId());
        dto.setSceneTitre(planning.getScene().getTitre());
        dto.setSequenceId(planning.getScene().getSequence().getId());
        dto.setSequenceTitre(planning.getScene().getSequence().getTitre());
        dto.setEpisodeId(planning.getScene().getSequence().getEpisode().getId());
        dto.setEpisodeTitre(planning.getScene().getSequence().getEpisode().getTitre());
        dto.setProjetId(planning.getScene().getSequence().getEpisode().getProjet().getId());
        dto.setProjetTitre(planning.getScene().getSequence().getEpisode().getProjet().getTitre());
        dto.setDateTournage(planning.getDateTournage());
        dto.setHeureDebut(planning.getHeureDebut());
        dto.setHeureFin(planning.getHeureFin());
        dto.setStatutId(planning.getStatut().getId());
        dto.setStatutNom(planning.getStatut().getNomStatut());
        dto.setStatutCode(planning.getStatut().getCode());
        dto.setDescription(planning.getDescription());
        dto.setCreeLe(planning.getCreeLe());
        dto.setModifieLe(planning.getModifieLe());

        if (planning.getLieu() != null) {
            dto.setLieuId(planning.getLieu().getId());
            dto.setLieuNom(planning.getLieu().getNomLieu());
            dto.setLieuTournage(planning.getLieu().getNomLieu()); // Pour compatibilité
        }
        
        if (planning.getPlateau() != null) {
            dto.setPlateauId(planning.getPlateau().getId());
            dto.setPlateauNom(planning.getPlateau().getNom());
            // Si on veut afficher le lieu + plateau
            if (planning.getLieu() != null) {
                dto.setLieuTournage(planning.getLieu().getNomLieu() + " - " + planning.getPlateau().getNom());
            }
        }
        
        return dto;
    }

    @Transactional
    public PlanningTournageDTO createPlanning(CreatePlanningTournageDTO createDTO) {
        PlanningTournage planning = new PlanningTournage();
        
        // Mapping des champs existants
        planning.setDateTournage(createDTO.getDateTournage());
        planning.setHeureDebut(createDTO.getHeureDebut());
        planning.setHeureFin(createDTO.getHeureFin());
        planning.setDescription(createDTO.getDescription());
        
        // Associer la scène
        Scene scene = sceneRepository.findById(createDTO.getSceneId())
                .orElseThrow(() -> new RuntimeException("Scène non trouvée"));
        planning.setScene(scene);
        
        // Associer le statut
        StatutPlanning statut = statutPlanningRepository.findById(createDTO.getStatutId())
                .orElseThrow(() -> new RuntimeException("Statut non trouvé"));
        planning.setStatut(statut);
        
        // Associer le lieu
        if (createDTO.getLieuId() != null) {
            Lieu lieu = lieuRepository.findById(createDTO.getLieuId())
                    .orElseThrow(() -> new RuntimeException("Lieu non trouvé"));
            planning.setLieu(lieu);
        }
        
        // Associer le plateau
        if (createDTO.getPlateauId() != null) {
            Plateau plateau = plateauRepository.findById(createDTO.getPlateauId())
                    .orElseThrow(() -> new RuntimeException("Plateau non trouvé"));
            planning.setPlateau(plateau);
        }
        
        PlanningTournage saved = planningTournageRepository.save(planning);
        return convertToDTO(saved);
    }
}
