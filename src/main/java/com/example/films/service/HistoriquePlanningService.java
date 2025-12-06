package com.example.films.service;

import com.example.films.dto.HistoriquePlanningDTO;
import com.example.films.entity.HistoriquePlanning;
import com.example.films.entity.Replanification;
import com.example.films.entity.SceneTournage;
import com.example.films.entity.PlanningTournage;
import com.example.films.repository.HistoriquePlanningRepository;
import com.example.films.repository.LieuRepository;
import com.example.films.repository.PlateauRepository;
import com.example.films.repository.SceneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoriquePlanningService {
    
    private final HistoriquePlanningRepository historiquePlanningRepository;
    private final SceneRepository sceneRepository;
    private final LieuRepository lieuRepository;
    private final PlateauRepository plateauRepository;
    
    /**
     * Récupérer l'historique complet d'une scène
     */
    @Transactional(readOnly = true)
    public List<HistoriquePlanningDTO> getHistoriqueBySceneId(Long sceneId) {
        List<HistoriquePlanning> historique = historiquePlanningRepository.findBySceneIdWithDetails(sceneId);
        return historique.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer l'historique par projet
     */
    @Transactional(readOnly = true)
    public List<HistoriquePlanningDTO> getHistoriqueByProjetId(Long projetId) {
        List<HistoriquePlanning> historique = historiquePlanningRepository.findByProjetId(projetId);
        return historique.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Sauvegarder l'historique d'un SceneTournage
     */
   @Transactional
    public void sauvegarderHistoriqueSceneTournage(SceneTournage tournage, Replanification replanification) {
        HistoriquePlanning historique = new HistoriquePlanning();
        
        historique.setSceneId(tournage.getScene().getId());
        historique.setTypePlanning("SCENE_TOURNAGE");
        historique.setAncienneDate(tournage.getDateTournage());
        
        // Convertir LocalTime en String pour l'historique
        if (tournage.getHeureDebut() != null) {
            historique.setAncienneHeureDebut(tournage.getHeureDebut().toString());
        }
        if (tournage.getHeureFin() != null) {
            historique.setAncienneHeureFin(tournage.getHeureFin().toString());
        }
        
        historique.setAncienStatut(tournage.getStatutTournage());
        historique.setRaisonReplanification(replanification.getRaison());
        historique.setReplanification(replanification);
        
        if (tournage.getLieu() != null) {
            historique.setAncienLieuId(tournage.getLieu().getId());
        }
        
        if (tournage.getPlateau() != null) {
            historique.setAncienPlateauId(tournage.getPlateau().getId());
        }
        
        historiquePlanningRepository.save(historique);
    }
    
    /**
     * Sauvegarder l'historique d'un PlanningTournage
     */
    @Transactional
    public void sauvegarderHistoriquePlanningTournage(PlanningTournage planning, Replanification replanification) {
        HistoriquePlanning historique = new HistoriquePlanning();
        
        historique.setSceneId(planning.getScene().getId());
        historique.setTypePlanning("PLANNING_TOURNAGE");
        historique.setAncienneDate(planning.getDateTournage());
        historique.setAncienneHeureDebut(planning.getHeureDebut());
        historique.setAncienneHeureFin(planning.getHeureFin());
        historique.setAncienStatut(planning.getStatut().getNomStatut());
        historique.setRaisonReplanification(replanification.getRaison());
        historique.setReplanification(replanification);
        
        if (planning.getLieu() != null) {
            historique.setAncienLieuId(planning.getLieu().getId());
        }
        
        if (planning.getPlateau() != null) {
            historique.setAncienPlateauId(planning.getPlateau().getId());
        }
        
        historiquePlanningRepository.save(historique);
    }
    
    /**
     * Sauvegarder un historique simple
     */
    @Transactional
    public void sauvegarderHistoriqueSimple(Long sceneId, String typePlanning, 
                                           String ancienStatut, String raison, 
                                           Replanification replanification) {
        HistoriquePlanning historique = new HistoriquePlanning();
        historique.setSceneId(sceneId);
        historique.setTypePlanning(typePlanning);
        historique.setAncienStatut(ancienStatut);
        historique.setRaisonReplanification(raison);
        historique.setReplanification(replanification);
        
        historiquePlanningRepository.save(historique);
    }
    
    /**
     * Convertir l'entité en DTO
     */
    private HistoriquePlanningDTO convertToDTO(HistoriquePlanning historique) {
        HistoriquePlanningDTO dto = new HistoriquePlanningDTO();
        
        dto.setId(historique.getId());
        dto.setSceneId(historique.getSceneId());
        dto.setTypePlanning(historique.getTypePlanning());
        dto.setAncienneDate(historique.getAncienneDate());
        dto.setAncienneHeureDebut(historique.getAncienneHeureDebut());
        dto.setAncienneHeureFin(historique.getAncienneHeureFin());
        dto.setAncienStatut(historique.getAncienStatut());
        dto.setAncienLieuId(historique.getAncienLieuId());
        dto.setAncienPlateauId(historique.getAncienPlateauId());
        dto.setRaisonReplanification(historique.getRaisonReplanification());
        dto.setDateReplanification(historique.getDateReplanification());
        
        // Informations sur la replanification
        if (historique.getReplanification() != null) {
            dto.setReplanificationId(historique.getReplanification().getId());
        }
        
        // Récupérer les noms du lieu et plateau si disponibles
        if (historique.getAncienLieuId() != null) {
            lieuRepository.findById(historique.getAncienLieuId()).ifPresent(lieu -> 
                dto.setAncienLieuNom(lieu.getNomLieu())
            );
        }
        
        if (historique.getAncienPlateauId() != null) {
            plateauRepository.findById(historique.getAncienPlateauId()).ifPresent(plateau -> 
                dto.setAncienPlateauNom(plateau.getNom())
            );
        }
        
        // Récupérer les informations de la scène et du projet
        sceneRepository.findById(historique.getSceneId()).ifPresent(scene -> {
            dto.setSceneTitre(scene.getTitre());
            if (scene.getSequence() != null && scene.getSequence().getEpisode() != null 
                && scene.getSequence().getEpisode().getProjet() != null) {
                dto.setProjetTitre(scene.getSequence().getEpisode().getProjet().getTitre());
            }
        });
        
        return dto;
    }
    
    /**
     * Supprimer l'historique d'une scène
     */
    @Transactional
    public void supprimerHistoriqueBySceneId(Long sceneId) {
        List<HistoriquePlanning> historique = historiquePlanningRepository.findBySceneIdOrderByDateReplanificationDesc(sceneId);
        historiquePlanningRepository.deleteAll(historique);
    }
    
    /**
     * Compter les replanifications par scène
     */
    @Transactional(readOnly = true)
    public Long compterReplanificationsBySceneId(Long sceneId) {
        return historiquePlanningRepository.countBySceneId(sceneId);
    }
}