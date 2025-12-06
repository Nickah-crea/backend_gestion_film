package com.example.films.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRaccordDTO {
    private Long sceneSourceId;
    private Long sceneCibleId;
    private Long typeRaccordId;
    private String description;
    private Boolean estCritique = false;
    private Long statutRaccordId = 1L;
    private List<MultipartFile> images;
     private Long personnageId;
    private Long comedienId;
    
    // constructeur sans images pour faciliter la création
    public CreateRaccordDTO(Long sceneSourceId, Long sceneCibleId, Long typeRaccordId, 
                           String description, Boolean estCritique, Long statutRaccordId) {
        this.sceneSourceId = sceneSourceId;
        this.sceneCibleId = sceneCibleId;
        this.typeRaccordId = typeRaccordId;
        this.description = description;
        this.estCritique = estCritique;
        this.statutRaccordId = statutRaccordId;
    }
}