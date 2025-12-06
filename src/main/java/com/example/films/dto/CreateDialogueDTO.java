package com.example.films.dto;

import lombok.Data;

@Data
public class CreateDialogueDTO {
    private Long sceneId;
    private Long personnageId;
    private String texte;
    private Integer ordre;
    private String observation;
    
}