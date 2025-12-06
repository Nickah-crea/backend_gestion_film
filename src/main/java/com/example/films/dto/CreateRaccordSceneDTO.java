// CreateRaccordSceneDTO.java
package com.example.films.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateRaccordSceneDTO {
    private Long sceneSourceId;
    private Long sceneCibleId;
    private List<Long> typesRaccord;
    private String description;
    private Boolean estCritique = false;
    private Long statutRaccordId = 1L;
    private List<Long> photosIds;
    private List<Long> personnagesIds;    
    private List<Long> comediensIds; 
}