package com.example.films.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RechercheGlobaleDTO {
    private Long id;
    private String type; // "projet", "episode", "sequence", "scene"
    private String titre;
    private String synopsis;
    private String statutNom;
    private LocalDateTime modifieLe;
    private LocalDateTime creeLe;
    
    // Liens hiérarchiques
    private Long projetId;
    private String projetTitre;
    private Long episodeId;
    private String episodeTitre;
    private Long sequenceId;
    private String sequenceTitre;
    
    // Ordres pour le tri
    private Integer ordreEpisode;
    private Integer ordreSequence;
    private Integer ordreScene;
    
    public RechercheGlobaleDTO() {}
    
    public RechercheGlobaleDTO(Long id, String type, String titre, String synopsis, 
                              String statutNom, LocalDateTime modifieLe, LocalDateTime creeLe,
                              Long projetId, String projetTitre, Long episodeId, String episodeTitre,
                              Long sequenceId, String sequenceTitre, Integer ordreEpisode, 
                              Integer ordreSequence, Integer ordreScene) {
        this.id = id;
        this.type = type;
        this.titre = titre;
        this.synopsis = synopsis;
        this.statutNom = statutNom;
        this.modifieLe = modifieLe;
        this.creeLe = creeLe;
        this.projetId = projetId;
        this.projetTitre = projetTitre;
        this.episodeId = episodeId;
        this.episodeTitre = episodeTitre;
        this.sequenceId = sequenceId;
        this.sequenceTitre = sequenceTitre;
        this.ordreEpisode = ordreEpisode;
        this.ordreSequence = ordreSequence;
        this.ordreScene = ordreScene;
    }
}
