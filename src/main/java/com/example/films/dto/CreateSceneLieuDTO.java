package com.example.films.dto;

import lombok.Data;

@Data
public class CreateSceneLieuDTO {
    private Long sceneId;
    private Long lieuId;
     private Long plateauId; 
    private String descriptionUtilisation;
}