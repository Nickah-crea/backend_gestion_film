package com.example.films.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class RaccordAlerteCalendrierDTO {
    private Long raccordId;
    private String description;
    private String typeRaccord;
    private String niveauAlerte;
    private List<String> messagesAlerte;
    private LocalDate dateTournageSource;
    private LocalDate dateTournageCible;
    private String sceneSourceTitre;
    private String sceneCibleTitre;
    
    // Méthode utilitaire pour obtenir la date d'alerte
    public LocalDate getDateAlerte() {
        return dateTournageCible != null ? dateTournageCible : dateTournageSource;
    }
    
    public String getTitreAlerte() {
        return "🚨 Raccord Critique - " + typeRaccord;
    }
    
    public String getDescriptionComplete() {
        StringBuilder sb = new StringBuilder();
        sb.append("Raccord critique entre:\n");
        sb.append("• Source: ").append(sceneSourceTitre).append("\n");
        sb.append("• Cible: ").append(sceneCibleTitre).append("\n");
        sb.append("Type: ").append(typeRaccord).append("\n");
        sb.append("Description: ").append(description).append("\n");
        
        if (messagesAlerte != null && !messagesAlerte.isEmpty()) {
            sb.append("\nAlertes:\n");
            for (String message : messagesAlerte) {
                sb.append("• ").append(message).append("\n");
            }
        }
        
        return sb.toString();
    }
}