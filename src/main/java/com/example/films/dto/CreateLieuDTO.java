package com.example.films.dto;

import lombok.Data;

@Data
public class CreateLieuDTO {
    private Long projetId;
    private String nomLieu;
    private String typeLieu;
    private String adresse;
}