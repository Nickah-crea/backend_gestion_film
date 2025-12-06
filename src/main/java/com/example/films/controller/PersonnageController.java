package com.example.films.controller;

import com.example.films.dto.CreatePersonnageDTO;
import com.example.films.dto.PersonnageDTO;
import com.example.films.entity.Personnage;
import com.example.films.service.PersonnageService;
import com.example.films.repository.PersonnageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RestController
@RequestMapping("/personnages")
public class PersonnageController {
    
    private final PersonnageService personnageService;
    private final PersonnageRepository personnageRepository;
    
    public PersonnageController(PersonnageService personnageService,PersonnageRepository personnageRepository) {
        this.personnageService = personnageService;
        this.personnageRepository = personnageRepository;
    }
    
    @GetMapping
    public ResponseEntity<List<PersonnageDTO>> getAllPersonnages() {
        try {
            List<PersonnageDTO> personnages = personnageService.getAllPersonnages();
            return ResponseEntity.ok(personnages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PersonnageDTO> getPersonnageById(@PathVariable Long id) {
        try {
            PersonnageDTO personnage = personnageService.getPersonnageById(id);
            return ResponseEntity.ok(personnage);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<PersonnageDTO> createPersonnage(@RequestBody CreatePersonnageDTO createPersonnageDTO) {
        try {
            PersonnageDTO createdPersonnage = personnageService.createPersonnage(createPersonnageDTO);
            return new ResponseEntity<>(createdPersonnage, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PersonnageDTO> updatePersonnage(@PathVariable Long id, 
                                                        @RequestBody CreatePersonnageDTO updatePersonnageDTO) {
        try {
            PersonnageDTO updatedPersonnage = personnageService.updatePersonnage(id, updatePersonnageDTO);
            return ResponseEntity.ok(updatedPersonnage);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePersonnage(@PathVariable Long id) {
        try {
            personnageService.deletePersonnage(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/projet/{projetId}")
    public ResponseEntity<List<PersonnageDTO>> getPersonnagesByProjet(@PathVariable Long projetId) {
        try {
            List<PersonnageDTO> personnages = personnageService.getPersonnagesByProjet(projetId);
            return ResponseEntity.ok(personnages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/comedien/{comedienId}")
    public ResponseEntity<List<PersonnageDTO>> getPersonnagesByComedien(@PathVariable Long comedienId) {
        try {
            List<PersonnageDTO> personnages = personnageService.getPersonnagesByComedien(comedienId);
            return ResponseEntity.ok(personnages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/sans-comedien")
    public ResponseEntity<List<PersonnageDTO>> getPersonnagesSansComedien() {
        try {
            List<PersonnageDTO> personnages = personnageService.getPersonnagesSansComedien();
            return ResponseEntity.ok(personnages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

