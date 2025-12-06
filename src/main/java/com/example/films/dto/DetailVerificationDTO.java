package com.example.films.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DetailVerificationDTO {
    private Long id;
    private String raccordDescription;
    private String typeRaccord;
    private String statutVerification;
    private Boolean estCritique;
    private String sceneSourceTitre;
    private String sceneCibleTitre;
    private String verificateurNom;
    private String verificateurRole;
    private LocalDateTime dateVerification;
    private String notes;
    private List<PreuveDTO> preuves;
    private List<HistoriqueVerificationDTO> historique;
}