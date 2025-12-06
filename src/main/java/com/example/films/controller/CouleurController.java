package com.example.films.controller;

import com.example.films.dto.CouleurDTO;
import com.example.films.service.CouleurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/couleurs")
public class CouleurController {
    
    private final CouleurService couleurService;
    
    public CouleurController(CouleurService couleurService) {
        this.couleurService = couleurService;
    }
    
    @GetMapping
    public ResponseEntity<List<CouleurDTO>> getAllCouleurs() {
        try {
            List<CouleurDTO> couleurs = couleurService.getAllCouleurs();
            return ResponseEntity.ok(couleurs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CouleurDTO> getCouleurById(@PathVariable Long id) {
        try {
            CouleurDTO couleur = couleurService.getCouleurById(id);
            return ResponseEntity.ok(couleur);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/hex/{valeurHex}")
    public ResponseEntity<CouleurDTO> getCouleurByHex(@PathVariable String valeurHex) {
        try {
            CouleurDTO couleur = couleurService.getCouleurByHex(valeurHex);
            return ResponseEntity.ok(couleur);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/defaut")
    public ResponseEntity<List<CouleurDTO>> getCouleursDefaut() {
        try {
            List<CouleurDTO> couleurs = couleurService.getCouleursDefaut();
            return ResponseEntity.ok(couleurs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
