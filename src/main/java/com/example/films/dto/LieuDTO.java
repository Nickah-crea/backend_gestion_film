package com.example.films.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LieuDTO {
    private Long id;
    private Long projetId;
    private String nomLieu;
    private String typeLieu;
    private String adresse;
    private LocalDateTime creeLe;
    private LocalDateTime modifieLe;
    private String projetTitre;
}