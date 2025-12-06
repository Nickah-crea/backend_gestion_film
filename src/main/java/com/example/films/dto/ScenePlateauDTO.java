package com.example.films.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ScenePlateauDTO {
    private Long id;
    private Long sceneId;
    private Long plateauId;
    private String descriptionUtilisation;
    private LocalDateTime creeLe;
    
    // Informations de la scène
    private String sceneTitre;
    private String sequenceTitre;
    private String episodeTitre;
    private String projetTitre;
    
    // Informations du plateau
    private String plateauNom;
    private String plateauType;
    private String lieuNom;
}