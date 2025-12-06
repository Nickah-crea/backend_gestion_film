package com.example.films.controller;

import com.example.films.dto.HistoriquePlanningDTO;
import com.example.films.service.HistoriquePlanningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historique-planning")
@RequiredArgsConstructor
public class HistoriquePlanningController {
    
    private final HistoriquePlanningService historiquePlanningService;
    
    @GetMapping("/scene/{sceneId}")
    public ResponseEntity<List<HistoriquePlanningDTO>> getHistoriqueByScene(@PathVariable Long sceneId) {
        try {
            List<HistoriquePlanningDTO> historique = historiquePlanningService.getHistoriqueBySceneId(sceneId);
            return ResponseEntity.ok(historique);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/projet/{projetId}")
    public ResponseEntity<List<HistoriquePlanningDTO>> getHistoriqueByProjet(@PathVariable Long projetId) {
        try {
            List<HistoriquePlanningDTO> historique = historiquePlanningService.getHistoriqueByProjetId(projetId);
            return ResponseEntity.ok(historique);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/scene/{sceneId}")
    public ResponseEntity<Void> supprimerHistoriqueByScene(@PathVariable Long sceneId) {
        try {
            historiquePlanningService.supprimerHistoriqueBySceneId(sceneId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/scene/{sceneId}/count")
    public ResponseEntity<Long> compterReplanificationsByScene(@PathVariable Long sceneId) {
        try {
            Long count = historiquePlanningService.compterReplanificationsBySceneId(sceneId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}