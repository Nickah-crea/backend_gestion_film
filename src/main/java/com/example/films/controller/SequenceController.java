package com.example.films.controller;

import com.example.films.dto.CreateSequenceDTO;
import com.example.films.dto.SequenceDTO;
import com.example.films.service.AuthorizationService;
import com.example.films.service.SequenceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sequences")
public class SequenceController {
    private final SequenceService sequenceService;
    private final AuthorizationService authorizationService;

    public SequenceController(SequenceService sequenceService, 
                              AuthorizationService authorizationService) {
        this.sequenceService = sequenceService;
        this.authorizationService = authorizationService; 
    }

    @GetMapping("/episodes/{episodeId}")
    public ResponseEntity<List<SequenceDTO>> getSequencesByEpisodeId(@PathVariable Long episodeId,
                                                                    @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            // Vérifier l'accès en lecture à l'épisode (moins restrictif)
            if (userId != null && !authorizationService.hasReadAccessToEpisode(userId, episodeId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            List<SequenceDTO> sequences = sequenceService.getSequencesByEpisodeId(episodeId);
            return ResponseEntity.ok(sequences);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/projet/{projetId}")
    public ResponseEntity<List<SequenceDTO>> getSequencesByProjetId(@PathVariable Long projetId,
                                                                  @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            // Vérifier l'accès au projet si userId est fourni
            if (userId != null && !authorizationService.hasAccessToProjet(userId, projetId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            List<SequenceDTO> sequences = sequenceService.getSequencesByProjetId(projetId);
            return ResponseEntity.ok(sequences);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SequenceDTO> getSequenceById(@PathVariable Long id) {
        try {
            SequenceDTO sequence = sequenceService.getSequenceById(id);
            return ResponseEntity.ok(sequence);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/episodes/{episodeId}")
    public ResponseEntity<SequenceDTO> createSequence(@PathVariable Long episodeId, 
                                                    @RequestBody CreateSequenceDTO createSequenceDTO,
                                                    @RequestHeader("X-User-Id") Long userId) {
        try {
            // Vérifier que l'utilisateur a accès à l'épisode pour modification
            if (!authorizationService.hasAccessToEpisode(userId, episodeId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            SequenceDTO createdSequence = sequenceService.createSequence(episodeId, createSequenceDTO);
            return new ResponseEntity<>(createdSequence, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SequenceDTO> updateSequence(@PathVariable Long id, @RequestBody CreateSequenceDTO updateSequenceDTO) {
        try {
            SequenceDTO updatedSequence = sequenceService.updateSequence(id, updateSequenceDTO);
            return ResponseEntity.ok(updatedSequence);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSequence(@PathVariable Long id) {
        try {
            sequenceService.deleteSequence(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
