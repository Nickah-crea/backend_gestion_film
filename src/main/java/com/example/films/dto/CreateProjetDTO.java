package com.example.films.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateProjetDTO {
    private String titre;
    private String synopsis;
    private Long genreId;
    private Long statutId;
    private LocalDate dateDebut;
    private LocalDate dateFin;
}