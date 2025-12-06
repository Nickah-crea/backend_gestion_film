
package com.example.films.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TypeRaccordDTO {
    private Long id;
    private String code;
    private String nomType;
    private String description;
    private LocalDateTime creeLe;
    
}