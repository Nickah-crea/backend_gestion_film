package com.example.films.service;

import com.example.films.entity.Comedien;
import com.example.films.entity.DisponibiliteComedien;
import com.example.films.entity.Personnage;
import com.example.films.entity.SceneTournage;
import com.example.films.repository.ComedienRepository;
import com.example.films.repository.DisponibiliteComedienRepository;
import com.example.films.repository.PersonnageRepository;
import com.example.films.repository.SceneTournageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConflictVerificationService {
    
    private final SceneTournageRepository sceneTournageRepository;
    private final PersonnageRepository personnageRepository;
    private final ComedienRepository comedienRepository;
    private final DisponibiliteComedienRepository disponibiliteComedienRepository;
    
    public ConflictVerificationService(SceneTournageRepository sceneTournageRepository,
                                     PersonnageRepository personnageRepository,
                                     ComedienRepository comedienRepository,
                                     DisponibiliteComedienRepository disponibiliteComedienRepository) {
        this.sceneTournageRepository = sceneTournageRepository;
        this.personnageRepository = personnageRepository;
        this.comedienRepository = comedienRepository;
        this.disponibiliteComedienRepository = disponibiliteComedienRepository;
    }
    
    public ConflictVerificationResult verifierConflitsComediens(Long sceneId, LocalDate dateTournage, 
                                                              LocalTime heureDebut, LocalTime heureFin) {
        List<String> conflits = new ArrayList<>();
        
        // 1. Récupérer les personnages de la scène via les dialogues
        List<Personnage> personnagesScene = personnageRepository.findPersonnagesBySceneId(sceneId);
        
        if (personnagesScene.isEmpty()) {
            return new ConflictVerificationResult(false, conflits);
        }
        
        // 2. Récupérer les comédiens associés à ces personnages
        Set<Comedien> comediensScene = new HashSet<>();
        for (Personnage personnage : personnagesScene) {
            if (personnage.getComedien() != null) {
                comediensScene.add(personnage.getComedien());
            }
        }
        
        if (comediensScene.isEmpty()) {
            return new ConflictVerificationResult(false, conflits);
        }
        
        // 3. Vérifier les disponibilités des comédiens
        for (Comedien comedien : comediensScene) {
            List<DisponibiliteComedien> disponibilites = disponibiliteComedienRepository
                .findByComedienAndDate(comedien, dateTournage);
            
            for (DisponibiliteComedien disponibilite : disponibilites) {
                String statut = disponibilite.getStatut().toLowerCase();
                
                if ("indisponible".equals(statut)) {
                    String message = String.format(
                        "Le comédien %s est marqué comme INDISPONIBLE le %s",
                        comedien.getNom(),
                        dateTournage.toString()
                    );
                    conflits.add(message);
                } else if ("occupe".equals(statut)) {
                    String message = String.format(
                        "Le comédien %s est marqué comme OCCUPÉ le %s",
                        comedien.getNom(),
                        dateTournage.toString()
                    );
                    conflits.add(message);
                }
            }
        }
        
        // 4. Récupérer tous les tournages à la même date pour vérifier les conflits horaires
        List<SceneTournage> tournagesMemeDate = sceneTournageRepository.findByDateTournage(dateTournage);
        
        for (Comedien comedien : comediensScene) {
            // 5. Vérifier les conflits horaires pour chaque comédien
            List<SceneTournage> conflitsComedien = tournagesMemeDate.stream()
                .filter(tournage -> !tournage.getScene().getId().equals(sceneId)) // Exclure la scène actuelle
                .filter(tournage -> estComedienDansScene(comedien.getId(), tournage.getScene().getId()))
                .filter(tournage -> chevauchementHoraires(heureDebut, heureFin, 
                         tournage.getHeureDebut(), tournage.getHeureFin()))
                .collect(Collectors.toList());
            
            if (!conflitsComedien.isEmpty()) {
                for (SceneTournage conflit : conflitsComedien) {
                    // Trouver le nom du personnage que le comédien joue dans la scène en conflit
                    String nomPersonnageConflit = trouverNomPersonnageComedien(comedien.getId(), conflit.getScene().getId());
                    
                    String message = String.format(
                        "Le comédien %s (joue %s) est déjà en tournage pour la scène '%s' (Séquence %d) de %s à %s",
                        comedien.getNom(),
                        nomPersonnageConflit != null ? nomPersonnageConflit : "un personnage",
                        conflit.getScene().getTitre(),
                        conflit.getScene().getSequence().getOrdre(),
                        formatHeure(conflit.getHeureDebut()),
                        formatHeure(conflit.getHeureFin())
                    );
                    conflits.add(message);
                }
            }
        }
        
        return new ConflictVerificationResult(!conflits.isEmpty(), conflits);
    }
    
    // Méthode pour vérifier les disponibilités seulement (sans conflits horaires)
    public ConflictVerificationResult verifierDisponibilitesComediens(Long sceneId, LocalDate dateTournage) {
        List<String> conflits = new ArrayList<>();
        
        // Récupérer les personnages de la scène via les dialogues
        List<Personnage> personnagesScene = personnageRepository.findPersonnagesBySceneId(sceneId);
        
        if (personnagesScene.isEmpty()) {
            return new ConflictVerificationResult(false, conflits);
        }
        
        // Récupérer les comédiens associés à ces personnages
        Set<Comedien> comediensScene = new HashSet<>();
        for (Personnage personnage : personnagesScene) {
            if (personnage.getComedien() != null) {
                comediensScene.add(personnage.getComedien());
            }
        }
        
        if (comediensScene.isEmpty()) {
            return new ConflictVerificationResult(false, conflits);
        }
        
        // Vérifier les disponibilités des comédiens
        for (Comedien comedien : comediensScene) {
            List<DisponibiliteComedien> disponibilites = disponibiliteComedienRepository
                .findByComedienAndDate(comedien, dateTournage);
            
            for (DisponibiliteComedien disponibilite : disponibilites) {
                String statut = disponibilite.getStatut().toLowerCase();
                
                if ("indisponible".equals(statut)) {
                    String message = String.format(
                        "Le comédien %s est marqué comme INDISPONIBLE le %s",
                        comedien.getNom(),
                        dateTournage.toString()
                    );
                    conflits.add(message);
                } else if ("occupe".equals(statut)) {
                    String message = String.format(
                        "Le comédien %s est marqué comme OCCUPÉ le %s",
                        comedien.getNom(),
                        dateTournage.toString()
                    );
                    conflits.add(message);
                }
            }
        }
        
        return new ConflictVerificationResult(!conflits.isEmpty(), conflits);
    }
    
    private boolean estComedienDansScene(Long comedienId, Long sceneId) {
        return personnageRepository.existsComedienInScene(comedienId, sceneId);
    }
    
    private String trouverNomPersonnageComedien(Long comedienId, Long sceneId) {
        return personnageRepository.findPersonnageNameByComedienAndScene(comedienId, sceneId)
                .orElse(null);
    }
    
    private boolean chevauchementHoraires(LocalTime debut1, LocalTime fin1, 
                                         LocalTime debut2, LocalTime fin2) {
        return !(fin1.isBefore(debut2) || debut1.isAfter(fin2));
    }
    
    private String formatHeure(LocalTime heure) {
        return heure.toString().substring(0, 5); // Format HH:mm
    }
    
    public static class ConflictVerificationResult {
        private final boolean hasConflicts;
        private final List<String> conflicts;
        
        public ConflictVerificationResult(boolean hasConflicts, List<String> conflicts) {
            this.hasConflicts = hasConflicts;
            this.conflicts = conflicts;
        }
        
        // Getters
        public boolean isHasConflicts() { return hasConflicts; }
        public List<String> getConflicts() { return conflicts; }
    }
}