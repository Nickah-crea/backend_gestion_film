package com.example.films.dto;

import lombok.Data;

public class RechercheSequenceDTO {
    private Long id;
    private String titre;
    private String synopsis;
    private String episodeTitre;
    private String projetTitre;
    private Long projetId; // AJOUTER CETTE PROPRIÉTÉ
    private Long episodeId; // AJOUTER AUSSI POUR LA NAVIGATION
    private Integer ordre;
    
    public RechercheSequenceDTO(Long id, String titre, String synopsis, String episodeTitre, 
                              String projetTitre, Long projetId, Long episodeId, Integer ordre) {
        this.id = id;
        this.titre = titre;
        this.synopsis = synopsis;
        this.episodeTitre = episodeTitre;
        this.projetTitre = projetTitre;
        this.projetId = projetId;
        this.episodeId = episodeId;
        this.ordre = ordre;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    
    public String getSynopsis() { return synopsis; }
    public void setSynopsis(String synopsis) { this.synopsis = synopsis; }
    
    public String getEpisodeTitre() { return episodeTitre; }
    public void setEpisodeTitre(String episodeTitre) { this.episodeTitre = episodeTitre; }
    
    public String getProjetTitre() { return projetTitre; }
    public void setProjetTitre(String projetTitre) { this.projetTitre = projetTitre; }
    
    public Long getProjetId() { return projetId; }
    public void setProjetId(Long projetId) { this.projetId = projetId; }
    
    public Long getEpisodeId() { return episodeId; }
    public void setEpisodeId(Long episodeId) { this.episodeId = episodeId; }
    
    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }
}
