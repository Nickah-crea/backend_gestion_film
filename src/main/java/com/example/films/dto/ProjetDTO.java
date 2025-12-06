package com.example.films.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProjetDTO {
    private Long id;
    private String titre;
    private String synopsis;
    private Long genreId;
    private String genreNom;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private LocalDateTime creeLe;
    private LocalDateTime modifieLe;
    private String statutNom;
}