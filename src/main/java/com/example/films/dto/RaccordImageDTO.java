package com.example.films.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RaccordImageDTO {
    private Long id;
    private String nomFichier;
    private String cheminFichier;
    private String descriptionImage;
    private Boolean estImageReference;
    private LocalDateTime creeLe;
     private Long typeRaccordId;
}