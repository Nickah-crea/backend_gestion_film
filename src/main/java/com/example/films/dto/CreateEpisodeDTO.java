// CreateEpisodeDTO.java
package com.example.films.dto;

import lombok.Data;

@Data
public class CreateEpisodeDTO {
    private String titre;
    private Integer ordre;
    private String synopsis;
    private Long statutId;
    private Long realisateurId; 
    private Long scenaristeId;
}