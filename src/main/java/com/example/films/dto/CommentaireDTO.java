package com.example.films.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentaireDTO {
    private Long id;
    private String contenu;
    private LocalDateTime creeLe;
    private LocalDateTime modifieLe;
    private Long utilisateurId;
    private String utilisateurNom;
    private String utilisateurEmail;
}