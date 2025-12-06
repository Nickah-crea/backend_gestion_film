package com.example.films.dto;

import lombok.Data;

@Data
public class CreateCommentaireDTO {
    private String contenu;
    private Long episodeId;
}