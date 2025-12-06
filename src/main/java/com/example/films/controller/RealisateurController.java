package com.example.films.controller;

import com.example.films.dto.RealisateurDTO;
import com.example.films.service.RealisateurService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/realisateurs")
public class RealisateurController {
    
    private final RealisateurService realisateurService;
    
    public RealisateurController(RealisateurService realisateurService) {
        this.realisateurService = realisateurService;
    }
    
    @GetMapping
    public List<RealisateurDTO> getAllRealisateurs() {
        return realisateurService.getAllRealisateurs();
    }
}