package com.example.films.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class RaccordExportDTO {
    private Long id;
    private String projetTitre;
    private String sequenceTitre;
    private String episodeTitre;
    private String sceneSourceTitre;
    private String sceneCibleTitre;
    private String typeRaccordNom;
    private String description;
    private Boolean estCritique;
    private String personnageNom;
    private String comedienNom;
    private String statutRaccordNom;
    private LocalDate dateTournageSource;
    private LocalDate dateTournageCible;
    private List<RaccordImageDTO> images;
    
    // Méthodes utilitaires pour l'export
    public String getInfoComplet() {
        return String.format("Raccord %s - %s -> %s", 
            typeRaccordNom, sceneSourceTitre, sceneCibleTitre);
    }
    
    public boolean hasImages() {
        return images != null && !images.isEmpty();
    }
}

