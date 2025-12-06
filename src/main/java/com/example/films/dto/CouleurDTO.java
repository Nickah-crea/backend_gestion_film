package com.example.films.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CouleurDTO {
    private Long id;
    private String nom;
    private String valeurHex;
    private Boolean estDefaut;
    private LocalDateTime creeLe;
    private LocalDateTime modifieLe;
}