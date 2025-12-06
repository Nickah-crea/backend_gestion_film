package com.example.films.controller;

import com.example.films.dto.CreateSceneLieuDTO;
import com.example.films.dto.PlateauDTO;
import com.example.films.dto.SceneLieuDTO;
import com.example.films.service.SceneLieuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scene-lieux")
public class SceneLieuController {
    private final SceneLieuService sceneLieuService;

    public SceneLieuController(SceneLieuService sceneLieuService) {
        this.sceneLieuService = sceneLieuService;
    }

    @GetMapping("/scenes/{sceneId}")
    public List<SceneLieuDTO> getSceneLieusBySceneId(@PathVariable Long sceneId) {
        return sceneLieuService.getSceneLieusBySceneId(sceneId);
    }

    @GetMapping("/lieux/{lieuId}")
    public List<SceneLieuDTO> getSceneLieusByLieuId(@PathVariable Long lieuId) {
        return sceneLieuService.getSceneLieusByLieuId(lieuId);
    }

    @GetMapping("/projets/{projetId}")
    public List<SceneLieuDTO> getSceneLieusByProjetId(@PathVariable Long projetId) {
        return sceneLieuService.getSceneLieusByProjetId(projetId);
    }

    @GetMapping("/lieux/{lieuId}/plateaux")
    public List<PlateauDTO> getPlateauxByLieuId(@PathVariable Long lieuId) {
        return sceneLieuService.getPlateauxByLieuId(lieuId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SceneLieuDTO> getSceneLieuById(@PathVariable Long id) {
        try {
            SceneLieuDTO sceneLieu = sceneLieuService.getSceneLieuById(id);
            return ResponseEntity.ok(sceneLieu);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<SceneLieuDTO> createSceneLieu(@RequestBody CreateSceneLieuDTO createSceneLieuDTO) {
        try {
            SceneLieuDTO createdSceneLieu = sceneLieuService.createSceneLieu(createSceneLieuDTO);
            return new ResponseEntity<>(createdSceneLieu, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SceneLieuDTO> updateSceneLieu(@PathVariable Long id, @RequestBody CreateSceneLieuDTO updateSceneLieuDTO) {
        try {
            SceneLieuDTO updatedSceneLieu = sceneLieuService.updateSceneLieu(id, updateSceneLieuDTO);
            return ResponseEntity.ok(updatedSceneLieu);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSceneLieu(@PathVariable Long id) {
        try {
            sceneLieuService.deleteSceneLieu(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}