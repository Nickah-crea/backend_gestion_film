package com.example.films.controller;

import com.example.films.dto.CommentaireDTO;
import com.example.films.dto.CreateSequenceCommentaireDTO;
import com.example.films.service.SequenceCommentaireService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sequence-commentaires")
public class SequenceCommentaireController {
    private final SequenceCommentaireService sequenceCommentaireService;

    public SequenceCommentaireController(SequenceCommentaireService sequenceCommentaireService) {
        this.sequenceCommentaireService = sequenceCommentaireService;
    }

    @PostMapping
    public ResponseEntity<CommentaireDTO> createCommentaire(
            @RequestBody CreateSequenceCommentaireDTO createCommentaireDTO,
            @RequestHeader("X-User-Id") Long utilisateurId) {
        try {
            CommentaireDTO createdCommentaire = sequenceCommentaireService.createCommentaireForSequence(createCommentaireDTO, utilisateurId);
            return new ResponseEntity<>(createdCommentaire, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/sequence/{sequenceId}")
    public ResponseEntity<List<CommentaireDTO>> getCommentairesBySequenceId(@PathVariable Long sequenceId) {
        try {
            List<CommentaireDTO> commentaires = sequenceCommentaireService.getCommentairesBySequenceId(sequenceId);
            return ResponseEntity.ok(commentaires);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/sequence/{sequenceId}/count")
    public ResponseEntity<Integer> getNombreCommentairesBySequenceId(@PathVariable Long sequenceId) {
        try {
            Integer count = sequenceCommentaireService.getNombreCommentairesBySequenceId(sequenceId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommentaire(@PathVariable Long id) {
        try {
            sequenceCommentaireService.deleteCommentaire(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}