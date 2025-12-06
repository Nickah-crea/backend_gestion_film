package com.example.films.dto;

import lombok.Data;

@Data
public class CreateDialogueCommentaireDTO {
    private String contenu;
    private Long dialogueId;
}