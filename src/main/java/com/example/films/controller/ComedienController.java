package com.example.films.controller;

import com.example.films.dto.ComedienDTO;
import com.example.films.dto.CreateComedienDTO;
import com.example.films.service.ComedienService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/comediens")
public class ComedienController {
    private final ComedienService comedienService;

    public ComedienController(ComedienService comedienService) {
        this.comedienService = comedienService;
    }

    @GetMapping("/projet/{projetId}")
    public ResponseEntity<List<ComedienDTO>> getComediensByProjet(@PathVariable Long projetId) {
        List<ComedienDTO> comediens = comedienService.getComediensByProjet(projetId);
        return ResponseEntity.ok(comediens);
    }

    @GetMapping
    public ResponseEntity<List<ComedienDTO>> getAllComediens() {
        List<ComedienDTO> comediens = comedienService.getAllComediens();
        return ResponseEntity.ok(comediens);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComedienDTO> getComedienById(@PathVariable Long id) {
        ComedienDTO comedien = comedienService.getComedienById(id);
        return ResponseEntity.ok(comedien);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ComedienDTO> createComedien(
        @RequestParam("projetId") Long projetId, 
        @RequestParam("nom") String nom,
        @RequestParam("age") Integer age,
        @RequestParam("email") String email,
        @RequestParam(value = "photo", required = false) MultipartFile photo,
        @RequestParam(value = "datesDisponibilite", required = false) List<LocalDate> datesDisponibilite,
        @RequestParam(value = "statutsDisponibilite", required = false) List<String> statutsDisponibilite) {
        
        CreateComedienDTO createComedienDTO = new CreateComedienDTO();
        createComedienDTO.setProjetId(projetId); 
        createComedienDTO.setNom(nom);
        createComedienDTO.setAge(age);
        createComedienDTO.setEmail(email);
        createComedienDTO.setDatesDisponibilite(datesDisponibilite);
        createComedienDTO.setStatutsDisponibilite(statutsDisponibilite);

        if (photo != null && !photo.isEmpty()) {
            try {
                String photoPath = comedienService.savePhoto(photo);
                createComedienDTO.setPhotoPath(photoPath);
            } catch (IOException e) {
                // L'exception sera automatiquement gérée par GlobalExceptionHandler
                // car FileStorageException est lancée dans savePhoto
                throw new RuntimeException("Erreur lors de la sauvegarde de la photo", e);
            }
        }

        ComedienDTO createdComedien = comedienService.createComedien(createComedienDTO);
        return new ResponseEntity<>(createdComedien, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ComedienDTO> updateComedien(
            @PathVariable Long id,
            @RequestParam(value = "nom", required = false) String nom,
            @RequestParam(value = "age", required = false) Integer age,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "projetId", required = false) Long projetId,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestParam(value = "datesDisponibilite", required = false) List<LocalDate> datesDisponibilite,
            @RequestParam(value = "statutsDisponibilite", required = false) List<String> statutsDisponibilite) {
        
        CreateComedienDTO updateComedienDTO = new CreateComedienDTO();
        
        if (nom != null) updateComedienDTO.setNom(nom);
        if (age != null) updateComedienDTO.setAge(age);
        if (email != null) updateComedienDTO.setEmail(email);
        if (projetId != null) updateComedienDTO.setProjetId(projetId);
        
        if (datesDisponibilite != null) updateComedienDTO.setDatesDisponibilite(datesDisponibilite);
        if (statutsDisponibilite != null) updateComedienDTO.setStatutsDisponibilite(statutsDisponibilite);

        if (photo != null && !photo.isEmpty()) {
            try {
                String photoPath = comedienService.savePhoto(photo);
                updateComedienDTO.setPhotoPath(photoPath);
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de la sauvegarde de la photo", e);
            }
        }

        ComedienDTO updatedComedien = comedienService.updateComedien(id, updateComedienDTO);
        return ResponseEntity.ok(updatedComedien);
    }

    @GetMapping("/photo/{filename}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable String filename) throws IOException {
        byte[] photo = comedienService.getPhoto(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(photo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComedien(@PathVariable Long id) {
        comedienService.deleteComedien(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/disponibilites")
    public ResponseEntity<Void> addDisponibilite(@PathVariable Long id,
                                                @RequestParam LocalDate date,
                                                @RequestParam String statut) {
        comedienService.addDisponibilite(id, date, statut);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/disponibilites/{disponibiliteId}")
    public ResponseEntity<Void> updateDisponibilite(@PathVariable Long id,
                                                @PathVariable Long disponibiliteId,
                                                @RequestBody DisponibiliteUpdateRequest request) {
        comedienService.updateDisponibilite(id, disponibiliteId, request.getDate(), request.getStatut());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/disponibilites/{disponibiliteId}")
    public ResponseEntity<Void> deleteDisponibilite(@PathVariable Long id,
                                                @PathVariable Long disponibiliteId) {
        comedienService.deleteDisponibilite(id, disponibiliteId);
        return ResponseEntity.ok().build();
    }

    public static class DisponibiliteUpdateRequest {
        private LocalDate date;
        private String statut;
        
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public String getStatut() { return statut; }
        public void setStatut(String statut) { this.statut = statut; }
    }
}