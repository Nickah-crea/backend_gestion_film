package com.example.films.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SceneDTO {
    private Long idScene;
    private Long sequenceId;
    private String titre;
    private Integer ordre;
    private String synopsis;
    private LocalDateTime creeLe;
    private LocalDateTime modifieLe;
    private String statutNom;
    private String sequenceTitre;
    private LocalDateTime dateFin;
    private List<SceneLieuDTO> sceneLieus; 
    private List<DialogueDTO> dialogues;
}