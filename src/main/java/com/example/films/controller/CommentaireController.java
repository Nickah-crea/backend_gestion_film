package com.example.films.controller;

import com.example.films.dto.CommentaireDTO;
import com.example.films.dto.CreateCommentaireDTO;
import com.example.films.service.CommentaireService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/commentaires")
public class CommentaireController {
    private final CommentaireService commentaireService;

    public CommentaireController(CommentaireService commentaireService) {
        this.commentaireService = commentaireService;
    }

    @PostMapping
    public ResponseEntity<CommentaireDTO> createCommentaire(
            @RequestBody CreateCommentaireDTO createCommentaireDTO,
            @RequestHeader("X-User-Id") Long utilisateurId) {
        try {
            CommentaireDTO createdCommentaire = commentaireService.createCommentaireForEpisode(createCommentaireDTO, utilisateurId);
            return new ResponseEntity<>(createdCommentaire, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/episode/{episodeId}")
    public ResponseEntity<List<CommentaireDTO>> getCommentairesByEpisodeId(@PathVariable Long episodeId) {
        try {
            List<CommentaireDTO> commentaires = commentaireService.getCommentairesByEpisodeId(episodeId);
            return ResponseEntity.ok(commentaires);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/episode/{episodeId}/count")
    public ResponseEntity<Integer> getNombreCommentairesByEpisodeId(@PathVariable Long episodeId) {
        try {
            Integer count = commentaireService.getNombreCommentairesByEpisodeId(episodeId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommentaire(@PathVariable Long id) {
        try {
            commentaireService.deleteCommentaire(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}