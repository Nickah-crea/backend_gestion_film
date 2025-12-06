package com.example.films.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HistoriqueVerificationDTO {
    private Long id;
    private String statut;
    private LocalDateTime date;
    private String verificateur;
    private String notes;
}