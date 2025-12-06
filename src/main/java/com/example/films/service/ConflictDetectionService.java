package com.example.films.service;

import com.example.films.entity.PlanningTournage;
import com.example.films.repository.PlanningTournageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConflictDetectionService {
    
    private final PlanningTournageRepository planningTournageRepository;
    
    public boolean detecterConflits(Long sceneId, LocalDate date, String heureDebut, String heureFin) {
        // Récupérer tous les tournages pour la même date
        List<PlanningTournage> tournagesMemeDate = planningTournageRepository.findByDateTournageBetween(date, date);
        
        // Convertir les heures String en LocalTime
        LocalTime debut = convertirHeure(heureDebut);
        LocalTime fin = convertirHeure(heureFin);
        
        for (PlanningTournage tournage : tournagesMemeDate) {
            // Ignorer le tournage actuel de la même scène
            if (tournage.getScene().getId().equals(sceneId)) {
                continue;
            }
            
            LocalTime debutExistante = convertirHeure(tournage.getHeureDebut());
            LocalTime finExistante = convertirHeure(tournage.getHeureFin());
            
            // Vérifier les chevauchements
            if ((debut.isBefore(finExistante) && fin.isAfter(debutExistante))) {
                return true; // Conflit détecté
            }
        }
        
        return false; // Aucun conflit
    }
    
    private LocalTime convertirHeure(String heure) {
        if (heure == null || heure.isEmpty()) {
            return LocalTime.of(0, 0);
        }
        
        // Gérer différents formats d'heure
        if (heure.length() == 5) { // Format "HH:mm"
            return LocalTime.parse(heure);
        } else if (heure.length() >= 8) { // Format "HH:mm:ss"
            return LocalTime.parse(heure.substring(0, 5));
        } else {
            throw new IllegalArgumentException("Format d'heure non supporté: " + heure);
        }
    }
}