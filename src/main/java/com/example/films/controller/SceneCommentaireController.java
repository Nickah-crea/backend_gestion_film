package com.example.films.controller;

import com.example.films.dto.CommentaireDTO;
import com.example.films.dto.CreateSceneCommentaireDTO;
import com.example.films.service.SceneCommentaireService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scene-commentaires")
public class SceneCommentaireController {
    
    private final SceneCommentaireService sceneCommentaireService;
    
    public SceneCommentaireController(SceneCommentaireService sceneCommentaireService) {
        this.sceneCommentaireService = sceneCommentaireService;
    }
    
    @PostMapping
    public ResponseEntity<CommentaireDTO> createCommentaire(
            @RequestBody CreateSceneCommentaireDTO createSceneCommentaireDTO,
            @RequestHeader("X-User-Id") Long utilisateurId) {
        try {
            CommentaireDTO createdCommentaire = sceneCommentaireService.createCommentaireForScene(createSceneCommentaireDTO, utilisateurId);
            return new ResponseEntity<>(createdCommentaire, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/scene/{sceneId}")
    public ResponseEntity<List<CommentaireDTO>> getCommentairesBySceneId(@PathVariable Long sceneId) {
        try {
            List<CommentaireDTO> commentaires = sceneCommentaireService.getCommentairesBySceneId(sceneId);
            return ResponseEntity.ok(commentaires);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/scene/{sceneId}/count")
    public ResponseEntity<Integer> getNombreCommentairesBySceneId(@PathVariable Long sceneId) {
        try {
            Integer count = sceneCommentaireService.getNombreCommentairesBySceneId(sceneId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommentaire(@PathVariable Long id) {
        try {
            sceneCommentaireService.deleteCommentaire(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}