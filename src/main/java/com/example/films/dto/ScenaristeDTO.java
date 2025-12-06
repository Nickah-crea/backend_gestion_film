package com.example.films.dto;

import lombok.Data;

@Data
public class ScenaristeDTO {
    private Long idScenariste;
    private Long idUtilisateur;
    private String nom;
    private String email;
    private String specialite;
    private String biographie;
}