package com.example.films.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class CreateSceneTournageDTO {
    private Long sceneId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateTournage;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime heureDebut;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime heureFin;
    
    private Long lieuId;
    private Long plateauId;
     private String statutTournage;
    private String notes;
}