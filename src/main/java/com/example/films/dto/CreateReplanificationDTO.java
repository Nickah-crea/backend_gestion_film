package com.example.films.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class CreateReplanificationDTO {
    private Long sceneId; 
    private LocalDate nouvelleDate;
     private LocalTime nouvelleHeureDebut; 
    private LocalTime nouvelleHeureFin;  
    private String raison;
    private List<Long> raccordIds;
}