package com.example.films.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatistiquesVerificationDTO {
    private Long totalVerifications;
    private Long verificationsConformes;
    private Long verificationsNonConformes;
    private Long raccordsCritiques;
    private Double tauxConformite;
    private String moyenneTempsVerification;
    private Integer verificationsCeMois;
    private String tauxResolution;
}