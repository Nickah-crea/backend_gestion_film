package com.example.films.controller;

import com.example.films.dto.DialogueSurlignageDTO;
import com.example.films.service.DialogueSurlignageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dialogue-surlignages")
public class DialogueSurlignageController {
    
    private final DialogueSurlignageService surlignageService;
    
    public DialogueSurlignageController(DialogueSurlignageService surlignageService) {
        this.surlignageService = surlignageService;
    }
    
    @GetMapping("/dialogue/{dialogueId}")
    public ResponseEntity<List<DialogueSurlignageDTO>> getSurlignagesByDialogue(@PathVariable Long dialogueId) {
        try {
            List<DialogueSurlignageDTO> surlignages = surlignageService.getSurlignagesByDialogue(dialogueId);
            return ResponseEntity.ok(surlignages);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<DialogueSurlignageDTO> addSurlignage(@RequestBody DialogueSurlignageDTO surlignageDTO) {
        try {
            DialogueSurlignageDTO saved = surlignageService.addSurlignage(surlignageDTO);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeSurlignage(@PathVariable Long id) {
        try {
            surlignageService.removeSurlignage(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @DeleteMapping("/dialogue/{dialogueId}/utilisateur/{utilisateurId}")
    public ResponseEntity<Void> removeSurlignagesByUser(@PathVariable Long dialogueId, @PathVariable Long utilisateurId) {
        try {
            surlignageService.removeSurlignagesByDialogueAndUser(dialogueId, utilisateurId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

