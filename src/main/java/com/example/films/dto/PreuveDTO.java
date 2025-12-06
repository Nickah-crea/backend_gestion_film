package com.example.films.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PreuveDTO {
    private Long id;
    private String nomFichier;
    private String cheminFichier;
    private String description;
    private LocalDateTime dateCreation;
}