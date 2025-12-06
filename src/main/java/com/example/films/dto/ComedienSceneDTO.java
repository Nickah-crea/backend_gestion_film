package com.example.films.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ComedienSceneDTO {
    private Long id;
    private Long idScene;
    private Long idComedien;
    private String comedienNom;
    private String sceneTitre;
    private String sequenceTitre;
    private String sceneStatut;
    private LocalDateTime creeLe;
}