package com.example.films.controller;

import com.example.films.dto.RaccordAlerteDTO;
import com.example.films.dto.RaccordAlerteCalendrierDTO;
import com.example.films.service.RaccordAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/raccords/alertes")
@RequiredArgsConstructor
public class RaccordAlerteController {
    
    private final RaccordAlertService raccordAlerteService;
    
    @GetMapping
    public ResponseEntity<List<RaccordAlerteDTO>> getAlertesRaccords() {
        try {
            List<RaccordAlerteDTO> alertes = raccordAlerteService.getAlertesPourRaccords();
            return ResponseEntity.ok(alertes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/raccord/{raccordId}")
    public ResponseEntity<RaccordAlerteDTO> getAlertePourRaccord(@PathVariable Long raccordId) {
        try {

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

   @GetMapping("/critiques/calendrier")
    public ResponseEntity<List<RaccordAlerteCalendrierDTO>> getAlertesCritiquesPourCalendrier() {
        try {
            List<RaccordAlerteDTO> alertes = raccordAlerteService.getAlertesPourRaccords();
            
            // Filtrer seulement les alertes critiques
            List<RaccordAlerteCalendrierDTO> alertesCalendrier = alertes.stream()
                .filter(alerte -> "CRITIQUE".equals(alerte.getNiveauAlerte()))
                .map(this::convertToCalendrierDTO)
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(alertesCalendrier);
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.badRequest().build();
        }
    }

    private RaccordAlerteCalendrierDTO convertToCalendrierDTO(RaccordAlerteDTO alerte) {
        RaccordAlerteCalendrierDTO dto = new RaccordAlerteCalendrierDTO();
        dto.setRaccordId(alerte.getRaccordId());
        dto.setDescription(alerte.getDescription());
        dto.setTypeRaccord(alerte.getTypeRaccord());
        dto.setNiveauAlerte(alerte.getNiveauAlerte());
        dto.setMessagesAlerte(alerte.getMessagesAlerte());
        dto.setDateTournageSource(alerte.getDateTournageSource());
        dto.setDateTournageCible(alerte.getDateTournageCible());
        
        dto.setSceneSourceTitre(alerte.getSceneSourceTitre());
        dto.setSceneCibleTitre(alerte.getSceneCibleTitre());
        
        return dto;
    }

}