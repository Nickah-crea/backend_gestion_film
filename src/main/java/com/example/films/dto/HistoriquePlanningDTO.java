package com.example.films.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class HistoriquePlanningDTO {
    
    private Long id;
    private Long sceneId;
    private String typePlanning;
    private LocalDate ancienneDate;
    private String ancienneHeureDebut;
    private String ancienneHeureFin;
    private String ancienStatut;
    private Long ancienLieuId;
    private String ancienLieuNom;
    private Long ancienPlateauId;
    private String ancienPlateauNom;
    private String raisonReplanification;
    private LocalDateTime dateReplanification;
    private Long replanificationId;
    private String sceneTitre;
    private String projetTitre;
    
    // Constructeurs
    public HistoriquePlanningDTO() {}
    
    public HistoriquePlanningDTO(Long id, Long sceneId, String typePlanning, 
                                LocalDate ancienneDate, String ancienStatut, 
                                String raisonReplanification, LocalDateTime dateReplanification) {
        this.id = id;
        this.sceneId = sceneId;
        this.typePlanning = typePlanning;
        this.ancienneDate = ancienneDate;
        this.ancienStatut = ancienStatut;
        this.raisonReplanification = raisonReplanification;
        this.dateReplanification = dateReplanification;
    }
}