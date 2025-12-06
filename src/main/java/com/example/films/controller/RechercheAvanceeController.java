// RechercheAvanceeController.java - VERSION SIMPLIFIÉE
package com.example.films.controller;

import com.example.films.dto.RechercheAvanceeDTO;
import com.example.films.dto.CritereRechercheDTO;
import com.example.films.service.RechercheAvanceeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recherche-avancee")
@CrossOrigin(origins = "http://localhost:5173")
public class RechercheAvanceeController {
    
    private final RechercheAvanceeService rechercheAvanceeService;
    
    public RechercheAvanceeController(RechercheAvanceeService rechercheAvanceeService) {
        this.rechercheAvanceeService = rechercheAvanceeService;
    }
    
    @PostMapping
    public ResponseEntity<List<RechercheAvanceeDTO>> rechercherAvance(@RequestBody CritereRechercheDTO criteres) {
        try {
            System.out.println("Requête reçue pour recherche avancée");
            System.out.println("Terme recherche: " + criteres.getTermeRecherche());
            System.out.println("Types recherche: " + criteres.getTypesRecherche());
            System.out.println("Projet ID: " + criteres.getProjetId());
            
            List<RechercheAvanceeDTO> resultats = rechercheAvanceeService.rechercherAvance(criteres);
            return ResponseEntity.ok(resultats);
        } catch (Exception e) {
            System.err.println("Erreur dans le controller: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/statuts")
    public ResponseEntity<List<String>> getStatutsDisponibles() {
        try {
            List<String> statuts = List.of("planifie", "confirme", "en_cours", "termine", "reporte", "annule");
            return ResponseEntity.ok(statuts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/details/{type}/{id}")
    public ResponseEntity<RechercheAvanceeDTO> getDetailsResultat(
            @PathVariable String type, 
            @PathVariable Long id) {
        try {
            RechercheAvanceeDTO details = rechercheAvanceeService.getDetailsParTypeEtId(type, id);
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            System.err.println("Erreur détails: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/details/{type}/{id}/complets")
    public ResponseEntity<Map<String, Object>> getDetailsComplets(
            @PathVariable String type, 
            @PathVariable Long id) {
        try {
            // Pour l'instant, retourner juste les détails de base
            RechercheAvanceeDTO details = rechercheAvanceeService.getDetailsParTypeEtId(type, id);
            Map<String, Object> response = Map.of(
                "informations", details,
                "statistiques", Map.of("nbElements", 1),
                "informationsComplementaires", Map.of("type", type)
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Erreur détails complets: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}

