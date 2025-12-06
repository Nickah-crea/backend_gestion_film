package com.example.films.service;

import com.example.films.dto.RaccordAlerteDTO;
import com.example.films.entity.PlanningTournage;
import com.example.films.entity.Raccord;
import com.example.films.entity.SceneTournage;
import com.example.films.repository.PlanningTournageRepository;
import com.example.films.repository.RaccordRepository;
import com.example.films.repository.SceneTournageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RaccordAlertService {
    
    private final RaccordRepository raccordRepository;
    private final SceneTournageRepository sceneTournageRepository;
    private final SceneTournageService sceneTournageService;
    private final PlanningTournageRepository planningTournageRepository;
    
    // Seuil d'alerte pour les dates éloignées (en jours)
    private static final int SEUIL_ALERTE_JOURS = 7;
    
    public List<RaccordAlerteDTO> getAlertesPourRaccords() {
        List<Raccord> raccords = raccordRepository.findAllWithDetails();
        List<RaccordAlerteDTO> alertes = new ArrayList<>();
        
        for (Raccord raccord : raccords) {
            RaccordAlerteDTO alerte = analyserRaccord(raccord);
            if (alerte != null) {
                alertes.add(alerte);
            }
        }
        
        return alertes;
    }
    
   private Optional<PlanningTournage> getTournagePourScene(Long sceneId) {
    return planningTournageRepository.findBySceneId(sceneId)
            .stream()
            .findFirst();
}

// Mettre à jour la méthode analyserRaccord
public RaccordAlerteDTO analyserRaccord(Raccord raccord) {
    RaccordAlerteDTO alerte = new RaccordAlerteDTO();
    alerte.setRaccordId(raccord.getId());
    alerte.setDescription(raccord.getDescription());
    alerte.setTypeRaccord(raccord.getTypeRaccord().getNomType());
    alerte.setEstCritique(raccord.getEstCritique());

    alerte.setSceneSourceTitre(raccord.getSceneSource().getTitre());
    alerte.setSceneCibleTitre(raccord.getSceneCible().getTitre());
    alerte.setSceneSourceId(raccord.getSceneSource().getId());
    alerte.setSceneCibleId(raccord.getSceneCible().getId());
    
    // Récupérer les tournages des scènes source et cible
    Optional<PlanningTournage> tournageSource = getTournagePourScene(raccord.getSceneSource().getId());
    Optional<PlanningTournage> tournageCible = getTournagePourScene(raccord.getSceneCible().getId());
    
    boolean hasAlert = false;
    List<String> messagesAlerte = new ArrayList<>();
    
    // Vérification 1: Scène source déjà tournée vs scène cible pas planifiée
    if (tournageSource.isPresent() && "termine".equals(tournageSource.get().getStatut().getCode()) && 
        (tournageCible.isEmpty() || !"termine".equals(tournageCible.get().getStatut().getCode()))) {
        messagesAlerte.add("⚠️ Scène source déjà tournée, mais scène cible pas encore terminée");
        hasAlert = true;
    }
    
    // Vérification 2: Écart de dates important
    if (tournageSource.isPresent() && tournageCible.isPresent()) {
        LocalDate dateSource = tournageSource.get().getDateTournage();
        LocalDate dateCible = tournageCible.get().getDateTournage();
        
        long ecartJours = Math.abs(java.time.temporal.ChronoUnit.DAYS.between(dateSource, dateCible));
        
        if (ecartJours > SEUIL_ALERTE_JOURS) {
            messagesAlerte.add("📅 Écart important de " + ecartJours + " jours entre les tournages");
            hasAlert = true;
        }
        
        // Vérification 3: Incohérence chronologique
        if (dateCible.isBefore(dateSource)) {
            messagesAlerte.add("❌ Incohérence chronologique: scène cible tournée avant scène source");
            hasAlert = true;
            alerte.setIncoherenceChronologique(true);
        } else {
            alerte.setIncoherenceChronologique(false);
        }
        
        alerte.setDateTournageSource(dateSource);
        alerte.setDateTournageCible(dateCible);
        alerte.setEcartJours(ecartJours);
    }
    
    // Vérification 4: Scène source planifiée, cible non planifiée
    if (tournageSource.isPresent() && tournageCible.isEmpty()) {
        messagesAlerte.add("📋 Scène source planifiée, scène cible non planifiée");
        hasAlert = true;
    }
    
    alerte.setMessagesAlerte(messagesAlerte);
    alerte.setNiveauAlerte(calculerNiveauAlerte(messagesAlerte, alerte.isIncoherenceChronologique()));
    
    return hasAlert ? alerte : null;
}

    private String calculerNiveauAlerte(List<String> messages, boolean incoherenceChronologique) {
        if (incoherenceChronologique) {
            return "CRITIQUE";
        }
        
        for (String message : messages) {
            if (message.contains("❌") || message.contains("tournée avant")) {
                return "CRITIQUE";
            }
            if (message.contains("⚠️") || message.contains("Écart important")) {
                return "MOYEN";
            }
        }
        
        return "FAIBLE";
    }
    
    public String getCouleurAlerte(RaccordAlerteDTO alerte) {
        return switch (alerte.getNiveauAlerte()) {
            case "CRITIQUE" -> "#dc3545"; // Rouge
            case "MOYEN" -> "#ffc107";    // Orange
            case "FAIBLE" -> "#17a2b8";   // Bleu
            default -> "#6c757d";         // Gris
        };
    }
    
    public List<LocalDate> getSuggestionsReplanification(Raccord raccord) {
        List<LocalDate> suggestions = new ArrayList<>();
        Optional<SceneTournage> tournageSource = sceneTournageRepository.findBySceneId(raccord.getSceneSource().getId());
        
        if (tournageSource.isPresent()) {
            LocalDate dateSource = tournageSource.get().getDateTournage();
            
            // Suggestion 1: Date idéale (1-2 jours après)
            suggestions.add(dateSource.plusDays(1));
            suggestions.add(dateSource.plusDays(2));
            
            // Suggestion 2: Même semaine
            suggestions.add(dateSource.plusDays(3));
            suggestions.add(dateSource.plusDays(4));
        }
        
        return suggestions;
    }
}