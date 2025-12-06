package com.example.films.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DialogueSurlignageDTO {
    private Long id;
    private Long dialogueId;
    private CouleurDTO couleur;
    private Long utilisateurId;
    private String utilisateurNom; // Optionnel: pour afficher le nom de l'utilisateur
    private String texteSurligne;
    private Integer debutIndex;
    private Integer finIndex;
    private LocalDateTime creeLe;
}

