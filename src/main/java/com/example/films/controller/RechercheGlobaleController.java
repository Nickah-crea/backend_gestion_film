// RechercheGlobaleController.java
package com.example.films.controller;

import com.example.films.dto.RechercheGlobaleDTO;
import com.example.films.service.RechercheGlobaleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recherche-globale")
@CrossOrigin(origins = "http://localhost:5173")
public class RechercheGlobaleController {
    
    private final RechercheGlobaleService rechercheGlobaleService;
    
    public RechercheGlobaleController(RechercheGlobaleService rechercheGlobaleService) {
        this.rechercheGlobaleService = rechercheGlobaleService;
    }
    
    @GetMapping
    public ResponseEntity<List<RechercheGlobaleDTO>> rechercherGlobalement(
            @RequestParam String q,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String specificDate) {
        
        try {
            List<RechercheGlobaleDTO> resultats = rechercheGlobaleService.rechercherGlobalement(q, statut, date, specificDate);
            return ResponseEntity.ok(resultats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}




