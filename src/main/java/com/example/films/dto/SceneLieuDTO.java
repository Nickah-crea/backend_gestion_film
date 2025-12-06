package com.example.films.dto;

import lombok.Data;

@Data
public class SceneLieuDTO {
    private Long id;
    private Long sceneId;
    private Long lieuId;
    private String descriptionUtilisation;
    private String sceneTitre;
    private String lieuNom;
    private Long plateauId;
    private String plateauNom; 

    private String sequenceTitre;
    private String episodeTitre;
    private String projetTitre;
    private Long projetId;

}