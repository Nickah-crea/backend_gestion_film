package com.example.films.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AjustementPlanningDTO {
    private Long sceneId;
    private LocalDate nouvelleDate;
    private String nouvelleHeureDebut;
    private String nouvelleHeureFin;
    private String raisonAjustement;
    private Long utilisateurId; 
    private Boolean notifierEquipe;
    private String notesSupplementaires;

      public AjustementPlanningDTO() {}
    
    public AjustementPlanningDTO(Long sceneId, LocalDate nouvelleDate, String nouvelleHeureDebut, 
                                String nouvelleHeureFin, String raisonAjustement) {
        this.sceneId = sceneId;
        this.nouvelleDate = nouvelleDate;
        this.nouvelleHeureDebut = nouvelleHeureDebut;
        this.nouvelleHeureFin = nouvelleHeureFin;
        this.raisonAjustement = raisonAjustement;
    }
}