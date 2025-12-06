package com.example.films.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationRaccordDTO {
    private Long id;
    private Long utilisateurId;
    private String utilisateurNom;
    private Long raccordId;
     private String typeRaccord;
     private Long typeRaccordId;
     private String raccordDescription;
    private LocalDateTime dateVerification;
    private Long statutVerificationId;
    private String statutVerificationNom;
    private String notesVerification;
    private String preuveImage;
    private Boolean estCritique;
    private String sceneSourceTitre;
    private String sceneCibleTitre;
    private String verificateurNom;
    private String verificateurRole;
     private String notes;
    private Integer nombrePreuves;
    private Long projetId;
    
    
}

   
   

   