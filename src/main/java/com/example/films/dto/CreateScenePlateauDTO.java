package com.example.films.dto;

import lombok.Data;

@Data
public class CreateScenePlateauDTO {
    private Long sceneId;
    private Long plateauId;
    private String descriptionUtilisation;
}