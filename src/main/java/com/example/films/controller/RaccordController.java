package com.example.films.controller;

import com.example.films.dto.*;
import com.example.films.service.RaccordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import com.example.films.entity.TypeRaccord; 
import com.example.films.entity.StatutRaccord; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.Arrays;

@RestController
@RequestMapping("/raccords")
public class RaccordController {
    
    private final RaccordService raccordService;
    
    public RaccordController(RaccordService raccordService) {
        this.raccordService = raccordService;
    }
    
    @GetMapping
    public ResponseEntity<List<RaccordDTO>> getAllRaccords() {
        try {
            List<RaccordDTO> raccords = raccordService.getAllRaccords();
            return ResponseEntity.ok(raccords);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/scene/{sceneId}")
    public ResponseEntity<List<RaccordDTO>> getRaccordsByScene(@PathVariable Long sceneId) {
        try {
            List<RaccordDTO> raccords = raccordService.getRaccordsByScene(sceneId);
            return ResponseEntity.ok(raccords);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RaccordDTO> getRaccordById(@PathVariable Long id) {
        try {
            RaccordDTO raccord = raccordService.getRaccordById(id);
            return ResponseEntity.ok(raccord);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

     @GetMapping("/personnage/{personnageId}")
    public ResponseEntity<List<RaccordDTO>> getRaccordsByPersonnage(@PathVariable Long personnageId) {
        try {
            List<RaccordDTO> raccords = raccordService.getRaccordsByPersonnage(personnageId);
            return ResponseEntity.ok(raccords);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/comedien/{comedienId}")
    public ResponseEntity<List<RaccordDTO>> getRaccordsByComedien(@PathVariable Long comedienId) {
        try {
            List<RaccordDTO> raccords = raccordService.getRaccordsByComedien(comedienId);
            return ResponseEntity.ok(raccords);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

     @GetMapping("/sans-personnage")
    public ResponseEntity<List<RaccordDTO>> getRaccordsSansPersonnage() {
        try {
            List<RaccordDTO> raccords = raccordService.getRaccordsSansPersonnage();
            return ResponseEntity.ok(raccords);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createRaccord(
            @RequestParam("sceneSourceId") Long sceneSourceId,
            @RequestParam("sceneCibleId") Long sceneCibleId,
            @RequestParam("typeRaccordId") Long typeRaccordId,
            @RequestParam("description") String description,
            @RequestParam(value = "estCritique", defaultValue = "false") Boolean estCritique,
            @RequestParam(value = "statutRaccordId", defaultValue = "1") Long statutRaccordId,
            @RequestParam(value = "personnageId", required = false) Long personnageId,
            @RequestParam(value = "comedienId", required = false) Long comedienId,
            @RequestParam(value = "images", required = false) MultipartFile[] images) {
        
        try {
            CreateRaccordDTO createRaccordDTO = new CreateRaccordDTO();
            createRaccordDTO.setSceneSourceId(sceneSourceId);
            createRaccordDTO.setSceneCibleId(sceneCibleId);
            createRaccordDTO.setTypeRaccordId(typeRaccordId);
            createRaccordDTO.setDescription(description);
            createRaccordDTO.setEstCritique(estCritique);
            createRaccordDTO.setStatutRaccordId(statutRaccordId);
            createRaccordDTO.setPersonnageId(personnageId);
            createRaccordDTO.setComedienId(comedienId);
            
            if (images != null && images.length > 0) {
                createRaccordDTO.setImages(Arrays.asList(images));
            }
            
            RaccordDTO createdRaccord = raccordService.createRaccord(createRaccordDTO);
            return new ResponseEntity<>(createdRaccord, HttpStatus.CREATED);
            
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    }
 
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateRaccord(
            @PathVariable Long id,
            @RequestParam(value = "sceneSourceId", required = false) Long sceneSourceId,
            @RequestParam(value = "sceneCibleId", required = false) Long sceneCibleId,
            @RequestParam(value = "typeRaccordId", required = false) Long typeRaccordId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "estCritique", required = false) Boolean estCritique,
            @RequestParam(value = "statutRaccordId", required = false) Long statutRaccordId,
            @RequestParam(value = "personnageId", required = false) Long personnageId,
            @RequestParam(value = "comedienId", required = false) Long comedienId,
            @RequestParam(value = "images", required = false) MultipartFile[] images) {
        
        try {
            CreateRaccordDTO updateRaccordDTO = new CreateRaccordDTO();
            updateRaccordDTO.setSceneSourceId(sceneSourceId);
            updateRaccordDTO.setSceneCibleId(sceneCibleId);
            updateRaccordDTO.setTypeRaccordId(typeRaccordId);
            updateRaccordDTO.setDescription(description);
            updateRaccordDTO.setEstCritique(estCritique);
            updateRaccordDTO.setStatutRaccordId(statutRaccordId);
            updateRaccordDTO.setPersonnageId(personnageId);
            updateRaccordDTO.setComedienId(comedienId);
            
            if (images != null && images.length > 0) {
                updateRaccordDTO.setImages(Arrays.asList(images));
            }
            
            RaccordDTO updatedRaccord = raccordService.updateRaccord(id, updateRaccordDTO);
            return ResponseEntity.ok(updatedRaccord);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRaccord(@PathVariable Long id) {
        try {
            raccordService.deleteRaccord(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/image/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        try {
            byte[] image = raccordService.getImage(filename);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(image);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/types")
    public ResponseEntity<List<TypeRaccord>> getAllTypesRaccord() {
        try {
            List<TypeRaccord> types = raccordService.getAllTypesRaccord();
            return ResponseEntity.ok(types);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/statuts")
    public ResponseEntity<List<StatutRaccord>> getAllStatutsRaccord() {
        try {
            List<StatutRaccord> statuts = raccordService.getAllStatutsRaccord();
            return ResponseEntity.ok(statuts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}