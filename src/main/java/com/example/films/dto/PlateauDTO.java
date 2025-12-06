package com.example.films.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PlateauDTO {
    private Long id;
    private Long lieuId;
    private Long sceneId;
    private String nom;
    private String typePlateau;
    private String description;
    private LocalDateTime creeLe;
    private LocalDateTime modifieLe;
    

    private String lieuNom;
    private String sceneTitre;
    private String sequenceTitre;
    private String episodeTitre;
    private String projetTitre;
}