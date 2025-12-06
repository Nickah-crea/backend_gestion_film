package com.example.films.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DialogueDTO {
    private Long id;
    private String texte;
    private Integer ordre;
    private String observation;
    private LocalDateTime creeLe;
    private LocalDateTime modifieLe;
    
    // Références
    private Long sceneId;
    private String sceneTitre;
    
    private Long personnageId;
    private String personnageNom;
    
    private Long projetId;
    private String projetTitre;
}