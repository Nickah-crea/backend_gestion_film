package com.example.films.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class ReplanificationDTO {
    private Long id;
    private Long sceneId; 
    private String sceneTitre; 
    private List<Long> raccordIds; 
    private List<String> typesRaccordsConcernes; 
    private LocalDate nouvelleDate;
     private LocalTime nouvelleHeureDebut; 
    private LocalTime nouvelleHeureFin;
    private String raison;
    private String statut;
    private LocalDateTime creeLe;
}