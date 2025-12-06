package com.example.films.controller;

import com.example.films.dto.*;
import com.example.films.service.RaccordSceneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/raccords")
public class RaccordSceneController {
    
    private final RaccordSceneService raccordSceneService;
    
    public RaccordSceneController(RaccordSceneService raccordSceneService) {
        this.raccordSceneService = raccordSceneService;
    }
    
    @PostMapping("/scene-liaison")
    public ResponseEntity<?> createRaccordAvecPhotosExistantes(@RequestBody CreateRaccordSceneDTO createRaccordSceneDTO) {
        try {
            RaccordDTO createdRaccord = raccordSceneService.createRaccordAvecPhotosExistantes(createRaccordSceneDTO);
            return new ResponseEntity<>(createdRaccord, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    }
    
    @GetMapping("/scenes/{sceneId}/photos")
    public ResponseEntity<List<RaccordImageDTO>> getPhotosByScene(@PathVariable Long sceneId) {
        try {
            List<RaccordImageDTO> photos = raccordSceneService.getPhotosByScene(sceneId);
            return ResponseEntity.ok(photos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/scenes/projet/{projetId}")
    public ResponseEntity<List<SceneDTO>> getScenesByProjet(@PathVariable Long projetId) {
        try {
            List<SceneDTO> scenes = raccordSceneService.getScenesByProjet(projetId);
            return ResponseEntity.ok(scenes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/scenes/episode/{episodeId}")
    public ResponseEntity<List<SceneDTO>> getScenesByEpisode(@PathVariable Long episodeId) {
        try {
            List<SceneDTO> scenes = raccordSceneService.getScenesByEpisode(episodeId);
            return ResponseEntity.ok(scenes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/scenes/sequence/{sequenceId}")
    public ResponseEntity<List<SceneDTO>> getScenesBySequence(@PathVariable Long sequenceId) {
        try {
            List<SceneDTO> scenes = raccordSceneService.getScenesBySequence(sequenceId);
            return ResponseEntity.ok(scenes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{raccordId}/shared-images")
    public ResponseEntity<List<RaccordImageDTO>> getSharedImages(@PathVariable Long raccordId) {
        try {
            List<RaccordImageDTO> sharedImages = raccordSceneService.getSharedImages(raccordId);
            return ResponseEntity.ok(sharedImages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/scene-liaison-shared")
    public ResponseEntity<?> createRaccordAvecPartage(@RequestBody CreateRaccordSceneDTO createRaccordSceneDTO) {
        try {
            RaccordDTO createdRaccord = raccordSceneService.createRaccordAvecPartageImages(createRaccordSceneDTO);
            return new ResponseEntity<>(createdRaccord, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    }

     @GetMapping("/scenes/{sceneId}/all-photos")
    public ResponseEntity<List<RaccordImageDTO>> getAllPhotosForScene(@PathVariable Long sceneId) {
        try {
            List<RaccordImageDTO> photos = raccordSceneService.getAllPhotosForScene(sceneId);
            return ResponseEntity.ok(photos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{raccordId}/shared-images/{imageId}")
    public ResponseEntity<Void> dissocierImagePartagee(@PathVariable Long raccordId, @PathVariable Long imageId) {
        try {
            raccordSceneService.dissocierImagePartagee(raccordId, imageId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}