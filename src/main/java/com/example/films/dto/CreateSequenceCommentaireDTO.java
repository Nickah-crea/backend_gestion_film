package com.example.films.dto;

import lombok.Data;

@Data
public class CreateSequenceCommentaireDTO {
    private String contenu;
    private Long sequenceId;
}