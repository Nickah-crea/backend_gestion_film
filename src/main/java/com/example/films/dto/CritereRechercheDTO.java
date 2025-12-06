// CritereRechercheDTO.java
package com.example.films.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

// @Getter
// @Setter
// CritereRechercheDTO.java - AJOUTS
@Data
public class CritereRechercheDTO {
    private String termeRecherche;
    private List<String> typesRecherche; // "personnages", "lieux", "plateaux", "scenes", "episodes", "sequences"
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Long projetId;
    private Long episodeId; // NOUVEAU
    private Long sequenceId; // NOUVEAU
    private List<String> statuts;
    private String regroupement; // "plateau", "lieu", "personnage", "statut", "episode", "sequence"
    private Integer page = 0;
    private Integer taille = 20;
}

    
    