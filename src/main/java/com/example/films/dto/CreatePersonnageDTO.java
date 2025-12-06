package com.example.films.dto;

import lombok.Data;

@Data
public class CreatePersonnageDTO {
    private String nom;
    private String description;
    private Long projetId;
    private Long comedienId;
}