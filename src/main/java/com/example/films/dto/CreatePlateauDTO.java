package com.example.films.dto;

import lombok.Data;

@Data
public class CreatePlateauDTO {
    private Long lieuId;
    private String nom;
    private String typePlateau;
    private String description;
}