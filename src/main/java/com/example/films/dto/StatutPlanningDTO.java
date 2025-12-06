package com.example.films.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatutPlanningDTO {
    private Long id;
    private String code;
    private String nomStatut;
    private String description;
    private Integer ordreAffichage;
    private Boolean estActif;
}
