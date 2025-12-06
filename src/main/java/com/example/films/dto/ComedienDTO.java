package com.example.films.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ComedienDTO {
    private Long id;
    private String nom;
     private Long projetId;
    private String projetTitre;
    private Integer age;
    private String email;
    private String photoPath;
    private LocalDateTime creeLe;
    private LocalDateTime modifieLe;
    private List<DisponibiliteDTO> disponibilites;
}