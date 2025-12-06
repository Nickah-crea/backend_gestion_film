package com.example.films.controller;

import com.example.films.dto.CreateScenePlateauDTO;
import com.example.films.dto.ScenePlateauDTO;
import com.example.films.service.ScenePlateauService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scene-plateaux")
public class ScenePlateauController {
    private final ScenePlateauService scenePlateauService;

    public ScenePlateauController(ScenePlateauService scenePlateauService) {
        this.scenePlateauService = scenePlateauService;
    }

    @GetMapping("/scenes/{sceneId}")
    public List<ScenePlateauDTO> getScenePlateauxBySceneId(@PathVariable Long sceneId) {
        return scenePlateauService.getScenePlateauxBySceneId(sceneId);
    }

    @GetMapping("/plateaux/{plateauId}")
    public List<ScenePlateauDTO> getScenePlateauxByPlateauId(@PathVariable Long plateauId) {
        return scenePlateauService.getScenePlateauxByPlateauId(plateauId);
    }

    @GetMapping("/projets/{projetId}")
    public List<ScenePlateauDTO> getScenePlateauxByProjetId(@PathVariable Long projetId) {
        return scenePlateauService.getScenePlateauxByProjetId(projetId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScenePlateauDTO> getScenePlateauById(@PathVariable Long id) {
        try {
            ScenePlateauDTO scenePlateau = scenePlateauService.getScenePlateauById(id);
            return ResponseEntity.ok(scenePlateau);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<ScenePlateauDTO> createScenePlateau(@RequestBody CreateScenePlateauDTO createScenePlateauDTO) {
        try {
            ScenePlateauDTO createdScenePlateau = scenePlateauService.createScenePlateau(createScenePlateauDTO);
            return new ResponseEntity<>(createdScenePlateau, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScenePlateauDTO> updateScenePlateau(@PathVariable Long id, @RequestBody CreateScenePlateauDTO updateScenePlateauDTO) {
        try {
            ScenePlateauDTO updatedScenePlateau = scenePlateauService.updateScenePlateau(id, updateScenePlateauDTO);
            return ResponseEntity.ok(updatedScenePlateau);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScenePlateau(@PathVariable Long id) {
        try {
            scenePlateauService.deleteScenePlateau(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}