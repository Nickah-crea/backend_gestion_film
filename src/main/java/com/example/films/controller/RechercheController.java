package com.example.films.controller;

import com.example.films.dto.*;
import com.example.films.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recherche")
@CrossOrigin(origins = "http://localhost:5173")
public class RechercheController {
    
    private static final Logger logger = LoggerFactory.getLogger(RechercheController.class);
    
    private final EpisodeService episodeService;
    private final SceneService sceneService;
    private final SequenceService sequenceService;
    
    public RechercheController(EpisodeService episodeService, 
                              SceneService sceneService, 
                              SequenceService sequenceService) {
        this.episodeService = episodeService;
        this.sceneService = sceneService;
        this.sequenceService = sequenceService;
    }
    
    @GetMapping("/episodes")
    public ResponseEntity<List<RechercheEpisodeDTO>> rechercherEpisodes(@RequestParam String q) {
        try {
            logger.info("Recherche d'épisodes avec query: {}", q);
            List<RechercheEpisodeDTO> resultats = episodeService.rechercherEpisodes(q);
            logger.info("Nombre de résultats épisodes: {}", resultats.size());
            return ResponseEntity.ok(resultats);
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche d'épisodes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/scenes")
    public ResponseEntity<List<RechercheSceneDTO>> rechercherScenes(@RequestParam String q) {
        try {
            logger.info("Recherche de scènes avec query: {}", q);
            List<RechercheSceneDTO> resultats = sceneService.rechercherScenes(q);
            logger.info("Nombre de résultats scènes: {}", resultats.size());
            return ResponseEntity.ok(resultats);
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche de scènes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/sequences")
    public ResponseEntity<List<RechercheSequenceDTO>> rechercherSequences(@RequestParam String q) {
        try {
            logger.info("Recherche de séquences avec query: {}", q);
            List<RechercheSequenceDTO> resultats = sequenceService.rechercherSequences(q);
            logger.info("Nombre de résultats séquences: {}", resultats.size());
            return ResponseEntity.ok(resultats);
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche de séquences", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("API de recherche fonctionnelle");
    }    
}
