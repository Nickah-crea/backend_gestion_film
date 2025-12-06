package com.example.films.dto;

import lombok.Data;

@Data
public class CreateSequenceDTO {
    private String titre;
    private Integer ordre;
    private String synopsis;
    private Long statutId;
}