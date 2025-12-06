package com.example.films.dto;

import lombok.Data;

@Data
public class CreateSceneDTO {
    private String titre;
    private Integer ordre;
    private String synopsis;
    private Long statutId;
     private Long sequenceId;
}