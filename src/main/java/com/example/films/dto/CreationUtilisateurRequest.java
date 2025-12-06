package com.example.films.dto;

import lombok.Data;

@Data
public class CreationUtilisateurRequest {
    private String nom;
    private String email;
    private String motDePasse;
    private String role;
    private String specialite;
    private String biographie;
}
