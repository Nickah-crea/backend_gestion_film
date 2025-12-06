package com.example.films.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class PlanningTournageDTO {
    private Long id;
    private Long sceneId;
    private String sceneTitre;
    private Long sequenceId;
    private String sequenceTitre;
    private Long episodeId;
    private String episodeTitre;
    private Long projetId;
    private String projetTitre;
    private LocalDate dateTournage;
    private String heureDebut;
    private String heureFin;
    private Long statutId;
    private String statutNom;
    private String statutCode;
    private String description;
    private String lieuTournage;
    private LocalDateTime creeLe;
    private LocalDateTime modifieLe;
    private Long lieuId;
    private String lieuNom;
    private Long plateauId;
    private String plateauNom;
}

