// ConflictController.java - Ajouter un nouvel endpoint
package com.example.films.controller;

import com.example.films.service.ConflictVerificationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/conflicts")
@CrossOrigin(origins = "http://localhost:5173")
public class ConflictController {
    
    private final ConflictVerificationService conflictVerificationService;
    
    public ConflictController(ConflictVerificationService conflictVerificationService) {
        this.conflictVerificationService = conflictVerificationService;
    }
    
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> verifierConflits(
            @RequestParam Long sceneId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTournage,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime heureDebut,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime heureFin) {
        
        try {
            ConflictVerificationService.ConflictVerificationResult result = 
                conflictVerificationService.verifierConflitsComediens(sceneId, dateTournage, heureDebut, heureFin);
            
            Map<String, Object> response = new HashMap<>();
            response.put("hasConflicts", result.isHasConflicts());
            response.put("conflicts", result.getConflicts());
            response.put("sceneId", sceneId);
            response.put("dateTournage", dateTournage);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("hasConflicts", false);
            errorResponse.put("error", "Erreur lors de la vérification des conflits: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // NOUVEAU: Endpoint pour vérifier seulement les disponibilités
    @GetMapping("/check-disponibilites")
    public ResponseEntity<Map<String, Object>> verifierDisponibilites(
            @RequestParam Long sceneId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTournage) {
        
        try {
            ConflictVerificationService.ConflictVerificationResult result = 
                conflictVerificationService.verifierDisponibilitesComediens(sceneId, dateTournage);
            
            Map<String, Object> response = new HashMap<>();
            response.put("hasConflicts", result.isHasConflicts());
            response.put("conflicts", result.getConflicts());
            response.put("sceneId", sceneId);
            response.put("dateTournage", dateTournage);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("hasConflicts", false);
            errorResponse.put("error", "Erreur lors de la vérification des disponibilités: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}