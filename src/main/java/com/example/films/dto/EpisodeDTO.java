
package com.example.films.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EpisodeDTO {
    private Long idEpisode;
    private Long projetId;
    private String titre;
    private Integer ordre;
    private String synopsis;
    private LocalDateTime creeLe;
    private LocalDateTime modifieLe;
    private LocalDateTime dateFin;
    private String statutNom;
    private Integer nombreSequences;
    private RealisateurDTO realisateur; 
    private ScenaristeDTO scenariste; 
    private ProjetDTO projet;
}