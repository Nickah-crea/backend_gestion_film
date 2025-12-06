package com.example.films.dto;

import lombok.Data;

@Data
public class UtilisateurCreeDTO {
    private Long idUtilisateur;
    private Long idRole; // id du scénariste ou réalisateur créé
    private String nom;
    private String email;
    private String role;
    private String specialite;
    private String biographie;
    private String message;
}