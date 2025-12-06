package com.example.films.controller;

import com.example.films.entity.StatutProjet;
import com.example.films.service.StatutProjetService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/statuts-projet")
public class StatutProjetController {
    private final StatutProjetService statutProjetService;

    public StatutProjetController(StatutProjetService statutProjetService) {
        this.statutProjetService = statutProjetService;
    }

    @GetMapping
    public List<StatutProjet> getAllStatuts() {
        return statutProjetService.getAllStatuts();
    }
}