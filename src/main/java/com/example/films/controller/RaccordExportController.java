package com.example.films.controller;

import com.example.films.dto.RaccordExportDTO;
import com.example.films.entity.Raccord; // Added import
import com.example.films.service.RaccordExportService;
import com.example.films.service.PDFExportService;
import com.example.films.repository.RaccordRepository; // Ensure this is imported
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors; // Added import

@RestController
@RequestMapping("/raccords/export")
@RequiredArgsConstructor
public class RaccordExportController {

    private final RaccordExportService raccordExportService;
    private final PDFExportService pdfExportService;
    private final RaccordRepository raccordRepository; // Added field

    @GetMapping("/comedien/{comedienId}")
    public ResponseEntity<List<RaccordExportDTO>> getRaccordsByComedien(@PathVariable Long comedienId) {
        try {
            List<RaccordExportDTO> raccords = raccordExportService.getRaccordsForExportByComedien(comedienId);
            if (raccords.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(raccords);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/type/{typeCode}")
    public ResponseEntity<List<RaccordExportDTO>> getRaccordsByType(@PathVariable String typeCode) {
        try {
            List<RaccordExportDTO> raccords = raccordExportService.getRaccordsForExportByType(typeCode);
            if (raccords.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(raccords);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/projet/{projetId}")
    public ResponseEntity<List<RaccordExportDTO>> getRaccordsByProjet(@PathVariable Long projetId) {
        try {
            List<RaccordExportDTO> raccords = raccordExportService.getRaccordsForExportByProjet(projetId);
            if (raccords.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(raccords);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/scene/{sceneId}")
    public ResponseEntity<List<RaccordExportDTO>> getRaccordsByScene(@PathVariable Long sceneId) {
        try {
            List<RaccordExportDTO> raccords = raccordExportService.getRaccordsForExportByScene(sceneId);
            if (raccords.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(raccords);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

