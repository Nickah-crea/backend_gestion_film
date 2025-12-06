package com.example.films.controller;

import com.example.films.dto.*;
import com.example.films.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/statistics")
@CrossOrigin(origins = "http://localhost:5173") // Autoriser le frontend Vue.js
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }
    
    @GetMapping("/global")
    public ResponseEntity<?> getGlobalStatistics(
            @RequestParam(value = "projetId", required = false) Long projetId,
            @RequestParam(value = "periode", defaultValue = "all") String periode) {
        try {
            log.info("Requête reçue - projetId: {}, periode: {}", projetId, periode);
            GlobalStatisticsDTO stats = statisticsService.getGlobalStatistics(projetId, periode);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Erreur dans le contrôleur statistiques", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erreur serveur", "message", e.getMessage()));
        }
    }
    
    @GetMapping("/kpi")
    public ResponseEntity<?> getKPI() {
        try {
            Map<String, Object> kpi = statisticsService.getKPIData();
            return ResponseEntity.ok(kpi);
        } catch (Exception e) {
            log.error("Erreur lors du chargement des KPI", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erreur serveur", "message", e.getMessage()));
        }
    }
}
