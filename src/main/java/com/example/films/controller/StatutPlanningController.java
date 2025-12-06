package com.example.films.controller;

import com.example.films.dto.StatutPlanningDTO;
import com.example.films.service.StatutPlanningService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/statuts-planning")
public class StatutPlanningController {
    private final StatutPlanningService statutPlanningService;

    public StatutPlanningController(StatutPlanningService statutPlanningService) {
        this.statutPlanningService = statutPlanningService;
    }

    @GetMapping
    public List<StatutPlanningDTO> getAllStatuts() {
        return statutPlanningService.getAllStatuts();
    }
}
