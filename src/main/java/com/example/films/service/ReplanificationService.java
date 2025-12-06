package com.example.films.service;

import com.example.films.dto.CreateReplanificationDTO;
import com.example.films.dto.ReplanificationDTO;
import com.example.films.entity.*;
import com.example.films.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReplanificationService {
    
    private final ReplanificationRepository replanificationRepository;
    private final RaccordRepository raccordRepository;
    private final SceneRepository sceneRepository;
    private final SceneTournageRepository sceneTournageRepository;
    private final PlanningTournageRepository planningTournageRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); 
    private final HistoriquePlanningService historiquePlanningService;
    private final HistoriquePlanningRepository historiquePlanningRepository;
    
    @Transactional(readOnly = true)
    public List<ReplanificationDTO> getAllReplanifications() {
        List<Replanification> replanifications = replanificationRepository.findAllWithDetails();
        return replanifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ReplanificationDTO getReplanificationById(Long id) {
        Replanification replanification = replanificationRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Replanification non trouvée"));
        return convertToDTO(replanification);
    }
    
    @Transactional(readOnly = true)
    public List<ReplanificationDTO> getReplanificationsByScene(Long sceneId) {
        List<Replanification> replanifications = replanificationRepository.findBySceneId(sceneId);
        return replanifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean canCreateReplanification(Long sceneId) {
        // Vérifier si la scène existe et n'a pas déjà une replanification en cours
        return sceneRepository.existsById(sceneId) && 
               !replanificationRepository.existsBySceneIdAndStatut(sceneId, "PLANIFIEE");
    }
    
    @Transactional(readOnly = true)
    public List<ReplanificationDTO> getReplanificationsByProjet(Long projetId) {
        List<Replanification> replanifications = replanificationRepository.findByProjetId(projetId);
        return replanifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ReplanificationDTO> getReplanificationsByStatut(String statut) {
        List<Replanification> replanifications = replanificationRepository.findByStatut(statut);
        return replanifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ReplanificationDTO> getReplanificationsAValider() {
        LocalDate dateLimite = LocalDate.now().plusDays(7); // 7 jours à l'avance
        List<Replanification> replanifications = replanificationRepository.findReplanificationsAValider(dateLimite);
        return replanifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ReplanificationDTO createReplanification(CreateReplanificationDTO createDTO) {
        // Vérifier si une replanification existe déjà pour cette scène
        if (replanificationRepository.existsBySceneIdAndStatut(createDTO.getSceneId(), "PLANIFIEE")) {
            throw new RuntimeException("Une replanification est déjà en cours pour cette scène");
        }
        
        Scene scene = sceneRepository.findById(createDTO.getSceneId())
                .orElseThrow(() -> new RuntimeException("Scène non trouvée"));
        
        // Vérifier que la date de replanification est dans le futur
        if (createDTO.getNouvelleDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("La date de replanification doit être dans le futur");
        }
        
        // Récupérer tous les raccords de la scène
        List<Raccord> raccordsScene = raccordRepository.findBySceneId(createDTO.getSceneId());
        List<Long> raccordIds = raccordsScene.stream()
                .map(Raccord::getId)
                .collect(Collectors.toList());
        
        Replanification replanification = new Replanification();
        replanification.setScene(scene);
        replanification.setNouvelleDate(createDTO.getNouvelleDate());
        replanification.setNouvelleHeureDebut(createDTO.getNouvelleHeureDebut()); 
        replanification.setNouvelleHeureFin(createDTO.getNouvelleHeureFin()); 
        replanification.setRaison(createDTO.getRaison());
        replanification.setStatut("PLANIFIEE");
        replanification.setCreeLe(LocalDateTime.now());
        
        // Sauvegarder les IDs des raccords concernés
        try {
            replanification.setRaccordIds(objectMapper.writeValueAsString(raccordIds));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erreur lors de la sérialisation des raccords", e);
        }
        
        Replanification saved = replanificationRepository.save(replanification);
        
        return convertToDTO(saved);
    }
    
    @Transactional
    public ReplanificationDTO updateReplanificationStatut(Long id, String nouveauStatut) {
        Replanification replanification = replanificationRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Replanification non trouvée"));
        
        // Empêcher la modification d'une replanification terminée
        if ("TERMINEE".equals(replanification.getStatut()) && !"TERMINEE".equals(nouveauStatut)) {
            throw new RuntimeException("Impossible de modifier une replanification terminée");
        }
        
        replanification.setStatut(nouveauStatut);
        
        // Si la replanification est marquée comme terminée, mettre à jour le planning
        if ("TERMINEE".equals(nouveauStatut)) {
            updatePlanningAfterReplanification(replanification);
        }
        
        Replanification updated = replanificationRepository.save(replanification);
        return convertToDTO(updated);
    }
    
    @Transactional
    public ReplanificationDTO updateReplanification(Long id, CreateReplanificationDTO updateDTO) {
        Replanification replanification = replanificationRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Replanification non trouvée"));
        
        // Empêcher la modification d'une replanification terminée
        if ("TERMINEE".equals(replanification.getStatut())) {
            throw new RuntimeException("Impossible de modifier une replanification terminée");
        }
        
        // Mettre à jour les champs
        if (updateDTO.getNouvelleDate() != null) {
            if (updateDTO.getNouvelleDate().isBefore(LocalDate.now())) {
                throw new RuntimeException("La date de replanification doit être dans le futur");
            }
            replanification.setNouvelleDate(updateDTO.getNouvelleDate());
        }
        
        if (updateDTO.getRaison() != null) {
            replanification.setRaison(updateDTO.getRaison());
        }
        
        Replanification updated = replanificationRepository.save(replanification);
        return convertToDTO(updated);
    }
    
    @Transactional
    public void deleteReplanification(Long id) {
        Replanification replanification = replanificationRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Replanification non trouvée"));
        
        // Empêcher la suppression d'une replanification terminée
        if ("TERMINEE".equals(replanification.getStatut())) {
            throw new RuntimeException("Impossible de supprimer une replanification terminée");
        }
        
        replanificationRepository.delete(replanification);
    }

    @Transactional
    public ReplanificationDTO terminerReplanification(Long id) {
        Replanification replanification = replanificationRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Replanification non trouvée"));
        
        // Mettre à jour le statut
        replanification.setStatut("TERMINEE");
        
        // Mettre à jour le planning de tournage avec la nouvelle date
        updatePlanningAfterReplanification(replanification);
        
        Replanification updated = replanificationRepository.save(replanification);
        return convertToDTO(updated);
    }
    
   @Transactional
    public void updatePlanningAfterReplanification(Replanification replanification) {
        try {
            Scene scene = replanification.getScene();
            
            // Mettre à jour SceneTournage et sauvegarder l'historique
            Optional<SceneTournage> tournageOpt = sceneTournageRepository.findBySceneId(scene.getId());
            if (tournageOpt.isPresent()) {
                SceneTournage tournage = tournageOpt.get();
                
                // Sauvegarder l'historique avant modification
                sauvegarderHistoriqueSceneTournage(tournage, replanification);
                
                // Mettre à jour la date et les heures
                tournage.setDateTournage(replanification.getNouvelleDate());
                
                // Mettre à jour les heures seulement si elles sont fournies
                if (replanification.getNouvelleHeureDebut() != null) {
                    tournage.setHeureDebut(replanification.getNouvelleHeureDebut());
                }
                if (replanification.getNouvelleHeureFin() != null) {
                    tournage.setHeureFin(replanification.getNouvelleHeureFin());
                }
                
                sceneTournageRepository.save(tournage);
                
                System.out.println("Tournage mis à jour - Nouvelle date: " + replanification.getNouvelleDate() + 
                                ", Heure début: " + tournage.getHeureDebut() + 
                                ", Heure fin: " + tournage.getHeureFin());
            } else {
                // Créer un nouveau tournage si aucun n'existe
                SceneTournage nouveauTournage = new SceneTournage();
                nouveauTournage.setScene(scene);
                nouveauTournage.setDateTournage(replanification.getNouvelleDate());
                
                // Utiliser les heures fournies ou des valeurs par défaut
                nouveauTournage.setHeureDebut(
                    replanification.getNouvelleHeureDebut() != null ? 
                    replanification.getNouvelleHeureDebut() : LocalTime.of(9, 0)
                );
                nouveauTournage.setHeureFin(
                    replanification.getNouvelleHeureFin() != null ? 
                    replanification.getNouvelleHeureFin() : LocalTime.of(12, 0)
                );
                
                nouveauTournage.setStatutTournage("planifie");
                sceneTournageRepository.save(nouveauTournage);
                
                System.out.println("Nouveau tournage créé pour la scène: " + scene.getId());
            }
            
            // Mettre à jour PlanningTournage et sauvegarder l'historique
            List<PlanningTournage> plannings = planningTournageRepository.findBySceneId(scene.getId());
            if (!plannings.isEmpty()) {
                PlanningTournage planning = plannings.get(0);
                
                // Sauvegarder l'historique avant modification
                sauvegarderHistoriquePlanningTournage(planning, replanification);
                
                planning.setDateTournage(replanification.getNouvelleDate());
                
                // Mettre à jour les heures pour PlanningTournage aussi
                if (replanification.getNouvelleHeureDebut() != null) {
                    planning.setHeureDebut(replanification.getNouvelleHeureDebut().toString());
                }
                if (replanification.getNouvelleHeureFin() != null) {
                    planning.setHeureFin(replanification.getNouvelleHeureFin().toString());
                }
                
                planningTournageRepository.save(planning);
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du planning: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la mise à jour du planning: " + e.getMessage());
        }
    }

    private void sauvegarderHistoriqueSceneTournage(SceneTournage tournage, Replanification replanification) {
        HistoriquePlanning historique = new HistoriquePlanning();
        
        historique.setSceneId(tournage.getScene().getId());
        historique.setTypePlanning("SCENE_TOURNAGE");
        historique.setAncienneDate(tournage.getDateTournage());
        
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

    private void sauvegarderHistoriquePlanningTournage(PlanningTournage planning, Replanification replanification) {
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
   
    private ReplanificationDTO convertToDTO(Replanification replanification) {
        ReplanificationDTO dto = new ReplanificationDTO();
        dto.setId(replanification.getId());
        dto.setSceneId(replanification.getScene().getId());
        dto.setSceneTitre(replanification.getScene().getTitre());
        dto.setNouvelleDate(replanification.getNouvelleDate());
         dto.setNouvelleHeureDebut(replanification.getNouvelleHeureDebut()); 
        dto.setNouvelleHeureFin(replanification.getNouvelleHeureFin());
        dto.setRaison(replanification.getRaison());
        dto.setStatut(replanification.getStatut());
        dto.setCreeLe(replanification.getCreeLe());
        
        // Désérialiser les IDs des raccords
        try {
            if (replanification.getRaccordIds() != null) {
                List<Long> raccordIds = objectMapper.readValue(
                    replanification.getRaccordIds(), 
                    new TypeReference<List<Long>>() {}
                );
                dto.setRaccordIds(raccordIds);
                
                // Récupérer les types de raccords concernés
                List<String> types = new ArrayList<>();
                for (Long raccordId : raccordIds) {
                    Optional<Raccord> raccordOpt = raccordRepository.findById(raccordId);
                    raccordOpt.ifPresent(raccord -> 
                        types.add(raccord.getTypeRaccord().getNomType())
                    );
                }
                dto.setTypesRaccordsConcernes(types);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la désérialisation des raccords: " + e.getMessage());
        }
        
        return dto;
    }
}