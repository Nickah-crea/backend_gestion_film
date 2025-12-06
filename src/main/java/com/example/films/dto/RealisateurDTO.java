package com.example.films.dto;

import lombok.Data;

@Data
public class RealisateurDTO {
    private Long idRealisateur;
    private Long idUtilisateur;
    private String nom;
    private String email;
    private String specialite;
    private String biographie;
}