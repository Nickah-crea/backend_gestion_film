package com.example.films.controller;

import com.example.films.dto.CreateProjetDTO;
import com.example.films.dto.ProjetDTO;
import com.example.films.service.ProjetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projets")
public class ProjetController {
    private final ProjetService projetService;

    public ProjetController(ProjetService projetService) {
        this.projetService = projetService;
    }

    @GetMapping
    public List<ProjetDTO> getAllProjets() {
        return projetService.getAllProjetsWithDetails();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjetDTO> getProjetById(@PathVariable Long id) {
        try {
            ProjetDTO projet = projetService.getProjetById(id);
            return ResponseEntity.ok(projet);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<ProjetDTO> createProjet(@RequestBody CreateProjetDTO createProjetDTO) {
        try {
            ProjetDTO createdProjet = projetService.createProjet(createProjetDTO);
            return new ResponseEntity<>(createdProjet, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjetDTO> updateProjet(@PathVariable Long id, @RequestBody CreateProjetDTO updateProjetDTO) {
        try {
            ProjetDTO updatedProjet = projetService.updateProjet(id, updateProjetDTO);
            return ResponseEntity.ok(updatedProjet);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjet(@PathVariable Long id) {
        try {
            projetService.deleteProjet(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}