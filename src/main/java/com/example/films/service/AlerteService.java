package com.example.films.service;

import com.example.films.dto.SceneTournageDTO;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AlerteService {
    private final SceneTournageService sceneTournageService;
    //private final EmailService emailService; // À implémenter si nécessaire

    public AlerteService(SceneTournageService sceneTournageService) {
        this.sceneTournageService = sceneTournageService;
    }

    @Scheduled(cron = "0 0 9 * * ?") // Tous les jours à 9h
    public void verifierTournagesAConfirmer() {
        List<SceneTournageDTO> tournagesAConfirmer = sceneTournageService.getTournagesAConfirmer();
        
        if (!tournagesAConfirmer.isEmpty()) {
            envoyerAlerteTournagesAConfirmer(tournagesAConfirmer);
        }
    }

    private void envoyerAlerteTournagesAConfirmer(List<SceneTournageDTO> tournages) {
        // Implémentez l'envoi d'email ou de notification
        System.out.println("=== ALERTE: Tournages à confirmer ===");
        tournages.forEach(tournage -> {
            System.out.println("• " + tournage.getSceneTitre() + " - " + 
                             tournage.getDateTournage() + " - " + 
                             tournage.getProjetTitre());
        });
    }
}