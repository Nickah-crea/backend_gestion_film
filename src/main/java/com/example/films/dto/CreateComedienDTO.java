package com.example.films.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreateComedienDTO {
    private Long projetId;
    private String nom;
    private Integer age;
    private String email;
    private String photoPath;
    private List<LocalDate> datesDisponibilite; 
    private List<String> statutsDisponibilite;  
}