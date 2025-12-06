package com.example.films.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DisponibiliteDTO {
    private Long id;
    private LocalDate date;
    private String statut;
}