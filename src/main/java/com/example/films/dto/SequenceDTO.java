package com.example.films.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SequenceDTO {
    private Long idSequence;
    private Long episodeId;
    private String titre;
    private Integer ordre;
    private String synopsis;
    private LocalDateTime creeLe;
    private LocalDateTime modifieLe;
     private LocalDateTime dateFin;
    private String statutNom;
    private EpisodeDTO episode;
    private String episodeTitre;
    private List<SceneDTO> scenes;
}