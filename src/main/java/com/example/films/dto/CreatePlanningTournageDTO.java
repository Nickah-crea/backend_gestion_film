package com.example.films.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreatePlanningTournageDTO {
    private Long sceneId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateTournage;
    
    private String heureDebut;
    private String heureFin;
    private Long statutId;
    private String description;
    private String lieuTournage;
    private Long lieuId;       
    private Long plateauId;
}

