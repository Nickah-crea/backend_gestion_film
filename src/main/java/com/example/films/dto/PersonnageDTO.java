package com.example.films.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PersonnageDTO {
    private Long id;
    private String nom;
    private String description;
    private Long projetId;
    private String projetTitre;
    private Long comedienId;
    private String comedienNom;
    private LocalDateTime creeLe;
    private LocalDateTime modifieLe;
}