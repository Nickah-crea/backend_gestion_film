package com.example.films.controller;

import com.example.films.dto.CreateDialogueDTO;
import com.example.films.dto.DialogueDTO;
import com.example.films.service.AuthorizationService;
import com.example.films.service.DialogueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dialogues")
public class DialogueController {
    
    private final DialogueService dialogueService;
     private final AuthorizationService authorizationService;
    
    public DialogueController(DialogueService dialogueService, 
                              AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
        this.dialogueService = dialogueService;
    }
    
    @GetMapping
    public ResponseEntity<List<DialogueDTO>> getAllDialogues() {
        try {
            List<DialogueDTO> dialogues = dialogueService.getAllDialogues();
            return ResponseEntity.ok(dialogues);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DialogueDTO> getDialogueById(@PathVariable Long id) {
        try {
            DialogueDTO dialogue = dialogueService.getDialogueById(id);
            return ResponseEntity.ok(dialogue);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<DialogueDTO> createDialogue(@RequestBody CreateDialogueDTO createDialogueDTO,
                                                    @RequestHeader("X-User-Id") Long userId) {
        try {
            if (!authorizationService.hasAccessToDialogueCreation(userId, createDialogueDTO.getSceneId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            DialogueDTO createdDialogue = dialogueService.createDialogue(createDialogueDTO);
            return new ResponseEntity<>(createdDialogue, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DialogueDTO> updateDialogue(@PathVariable Long id,
                                                    @RequestBody CreateDialogueDTO updateDialogueDTO,
                                                    @RequestHeader("X-User-Id") Long userId) {
        try {
            // Récupérer le dialogue pour obtenir la scène parente
            DialogueDTO existingDialogue = dialogueService.getDialogueById(id);
            if (!authorizationService.hasAccessToDialogueCreation(userId, existingDialogue.getSceneId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            DialogueDTO updatedDialogue = dialogueService.updateDialogue(id, updateDialogueDTO);
            return ResponseEntity.ok(updatedDialogue);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDialogue(@PathVariable Long id,
                                            @RequestHeader("X-User-Id") Long userId) {
        try {
            DialogueDTO existingDialogue = dialogueService.getDialogueById(id);
            if (!authorizationService.hasAccessToDialogueCreation(userId, existingDialogue.getSceneId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            dialogueService.deleteDialogue(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/scene/{sceneId}")
    public ResponseEntity<List<DialogueDTO>> getDialoguesByScene(@PathVariable Long sceneId) {
        try {
            List<DialogueDTO> dialogues = dialogueService.getDialoguesByScene(sceneId);
            return ResponseEntity.ok(dialogues);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/personnage/{personnageId}")
    public ResponseEntity<List<DialogueDTO>> getDialoguesByPersonnage(@PathVariable Long personnageId) {
        try {
            List<DialogueDTO> dialogues = dialogueService.getDialoguesByPersonnage(personnageId);
            return ResponseEntity.ok(dialogues);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/scene/{sceneId}/ordre")
    public ResponseEntity<Void> updateDialoguesOrdre(@PathVariable Long sceneId,
                                                    @RequestBody List<Long> dialogueIdsInOrder) {
        try {
            dialogueService.updateDialoguesOrdre(sceneId, dialogueIdsInOrder);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}