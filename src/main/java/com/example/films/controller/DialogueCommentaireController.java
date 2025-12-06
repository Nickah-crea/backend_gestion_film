package com.example.films.controller;

import com.example.films.dto.CommentaireDTO;
import com.example.films.dto.CreateDialogueCommentaireDTO;
import com.example.films.service.DialogueCommentaireService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dialogues/commentaires")
public class DialogueCommentaireController {
    
    private final DialogueCommentaireService dialogueCommentaireService;
    
    public DialogueCommentaireController(DialogueCommentaireService dialogueCommentaireService) {
        this.dialogueCommentaireService = dialogueCommentaireService;
    }
    
    @PostMapping
    public ResponseEntity<CommentaireDTO> createCommentaire(
            @RequestBody CreateDialogueCommentaireDTO createDialogueCommentaireDTO,
            @RequestHeader("X-User-Id") Long utilisateurId) {
        try {
            CommentaireDTO createdCommentaire = dialogueCommentaireService
                    .createCommentaireForDialogue(createDialogueCommentaireDTO, utilisateurId);
            return new ResponseEntity<>(createdCommentaire, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/dialogue/{dialogueId}")
    public ResponseEntity<List<CommentaireDTO>> getCommentairesByDialogueId(@PathVariable Long dialogueId) {
        try {
            List<CommentaireDTO> commentaires = dialogueCommentaireService.getCommentairesByDialogueId(dialogueId);
            return ResponseEntity.ok(commentaires);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/dialogue/{dialogueId}/count")
    public ResponseEntity<Integer> getNombreCommentairesByDialogueId(@PathVariable Long dialogueId) {
        try {
            Integer count = dialogueCommentaireService.getNombreCommentairesByDialogueId(dialogueId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommentaire(@PathVariable Long id) {
        try {
            dialogueCommentaireService.deleteCommentaire(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}