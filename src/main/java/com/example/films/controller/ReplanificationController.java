package com.example.films.controller;

import com.example.films.dto.CreateReplanificationDTO;
import com.example.films.dto.ReplanificationDTO;
import com.example.films.service.ReplanificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/replanifications")
public class ReplanificationController {
    
    private final ReplanificationService replanificationService;
    
    public ReplanificationController(ReplanificationService replanificationService) {
        this.replanificationService = replanificationService;
    }
    
    @GetMapping
    public ResponseEntity<List<ReplanificationDTO>> getAllReplanifications() {
        try {
            List<ReplanificationDTO> replanifications = replanificationService.getAllReplanifications();
            return ResponseEntity.ok(replanifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ReplanificationDTO> getReplanificationById(@PathVariable Long id) {
        try {
            ReplanificationDTO replanification = replanificationService.getReplanificationById(id);
            return ResponseEntity.ok(replanification);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/scene/{sceneId}")
    public ResponseEntity<List<ReplanificationDTO>> getReplanificationsByScene(@PathVariable Long sceneId) {
        try {
            List<ReplanificationDTO> replanifications = replanificationService.getReplanificationsByScene(sceneId);
            return ResponseEntity.ok(replanifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/projet/{projetId}")
    public ResponseEntity<List<ReplanificationDTO>> getReplanificationsByProjet(@PathVariable Long projetId) {
        try {
            List<ReplanificationDTO> replanifications = replanificationService.getReplanificationsByProjet(projetId);
            return ResponseEntity.ok(replanifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<ReplanificationDTO>> getReplanificationsByStatut(@PathVariable String statut) {
        try {
            List<ReplanificationDTO> replanifications = replanificationService.getReplanificationsByStatut(statut);
            return ResponseEntity.ok(replanifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/a-valider")
    public ResponseEntity<List<ReplanificationDTO>> getReplanificationsAValider() {
        try {
            List<ReplanificationDTO> replanifications = replanificationService.getReplanificationsAValider();
            return ResponseEntity.ok(replanifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createReplanification(@RequestBody CreateReplanificationDTO createDTO) {
        try {
            ReplanificationDTO created = replanificationService.createReplanification(createDTO);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    }
    
    @PutMapping("/{id}/statut/{statut}")
    public ResponseEntity<?> updateReplanificationStatut(@PathVariable Long id, @PathVariable String statut) {
        try {
            ReplanificationDTO updated = replanificationService.updateReplanificationStatut(id, statut);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReplanification(@PathVariable Long id, @RequestBody CreateReplanificationDTO updateDTO) {
        try {
            ReplanificationDTO updated = replanificationService.updateReplanification(id, updateDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReplanification(@PathVariable Long id) {
        try {
            replanificationService.deleteReplanification(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    }
    
    @GetMapping("/scene/{sceneId}/can-create")
    public ResponseEntity<Boolean> canCreateReplanification(@PathVariable Long sceneId) {
        try {
            boolean canCreate = replanificationService.canCreateReplanification(sceneId);
            return ResponseEntity.ok(canCreate);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/terminer")
    public ResponseEntity<?> terminerReplanification(@PathVariable Long id) {
        try {
            ReplanificationDTO updated = replanificationService.terminerReplanification(id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur");
        }
    }
}