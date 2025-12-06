package com.example.films.controller;

import com.example.films.dto.CreatePlateauDTO;
import com.example.films.dto.LieuDTO;
import com.example.films.dto.PlateauDTO;
import com.example.films.service.AuthorizationService;
import com.example.films.service.LieuService;
import com.example.films.service.PlateauService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plateaux")
public class PlateauController {
    private final PlateauService plateauService;
    private final AuthorizationService authorizationService;
    private final LieuService lieuService;

    public PlateauController(PlateauService plateauService, 
                            AuthorizationService authorizationService,
                            LieuService lieuService) {
        this.plateauService = plateauService;
        this.authorizationService = authorizationService;
        this.lieuService = lieuService;
    }

    @GetMapping("/lieux/{lieuId}")
    public List<PlateauDTO> getPlateauxByLieuId(@PathVariable Long lieuId) {
        return plateauService.getPlateauxByLieuId(lieuId);
    }

    /*@GetMapping("/scenes/{sceneId}")
    public List<PlateauDTO> getPlateauxBySceneId(@PathVariable Long sceneId) {
        return plateauService.getPlateauxBySceneId(sceneId);
    }*/

    @GetMapping("/projets/{projetId}")
    public List<PlateauDTO> getPlateauxByProjetId(@PathVariable Long projetId) {
        return plateauService.getPlateauxByProjetId(projetId);
    }

    @GetMapping
    public ResponseEntity<List<PlateauDTO>> getAllPlateaux() {
        try {
            List<PlateauDTO> plateaux = plateauService.getAllPlateaux();
            return ResponseEntity.ok(plateaux);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlateauDTO> getPlateauById(@PathVariable Long id) {
        try {
            PlateauDTO plateau = plateauService.getPlateauById(id);
            return ResponseEntity.ok(plateau);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<PlateauDTO> createPlateau(@RequestBody CreatePlateauDTO createPlateauDTO,
                                                @RequestHeader("X-User-Id") Long userId) {
        try {
            // Récupérer le lieu pour obtenir l'ID du projet
            LieuDTO lieu = lieuService.getLieuById(createPlateauDTO.getLieuId());
            if (!authorizationService.hasAccessToLieuCreation(userId, lieu.getProjetId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            PlateauDTO createdPlateau = plateauService.createPlateau(createPlateauDTO);
            return new ResponseEntity<>(createdPlateau, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlateauDTO> updatePlateau(@PathVariable Long id, 
                                                @RequestBody CreatePlateauDTO updatePlateauDTO,
                                                @RequestHeader("X-User-Id") Long userId) {
        try {
            PlateauDTO existingPlateau = plateauService.getPlateauById(id);
            LieuDTO lieu = lieuService.getLieuById(existingPlateau.getLieuId());
            
            if (!authorizationService.hasAccessToLieuCreation(userId, lieu.getProjetId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            PlateauDTO updatedPlateau = plateauService.updatePlateau(id, updatePlateauDTO);
            return ResponseEntity.ok(updatedPlateau);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlateau(@PathVariable Long id,
                                            @RequestHeader("X-User-Id") Long userId) {
        try {
            PlateauDTO existingPlateau = plateauService.getPlateauById(id);
            LieuDTO lieu = lieuService.getLieuById(existingPlateau.getLieuId());
            
            if (!authorizationService.hasAccessToLieuCreation(userId, lieu.getProjetId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            plateauService.deletePlateau(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}