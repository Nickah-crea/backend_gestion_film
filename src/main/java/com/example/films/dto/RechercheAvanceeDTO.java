// RechercheAvanceeDTO.java
package com.example.films.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RechercheAvanceeDTO {
    private Long id;
    private String type; // "scene", "personnage", "lieu", "plateau"
    private String titre;
    private String description;
    private LocalDate dateTournage;
    private String heureDebut;
    private String heureFin;
    private String statut;
    private String lieuNom;
    private String plateauNom;
    private String personnageNom;
    private String comedienNom;
    private String projetTitre;
    private String episodeTitre;
    private String sequenceTitre;
    private List<String> dialogues;
    private LocalDateTime modifieLe;
    
    // Pour le regroupement
    private String groupeKey;
    private String groupeValeur;

     private Long projetId;
    private Long episodeId; 
    private Long sequenceId;
    private Long sceneId;
}
