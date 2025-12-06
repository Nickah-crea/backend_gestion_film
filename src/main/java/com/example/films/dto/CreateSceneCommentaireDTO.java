package com.example.films.dto;

import lombok.Data;

@Data
public class CreateSceneCommentaireDTO {
    private String contenu;
    private Long sceneId;
}