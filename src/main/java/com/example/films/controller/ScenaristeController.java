package com.example.films.controller;

import com.example.films.dto.ScenaristeDTO;
import com.example.films.service.ScenaristeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scenaristes")
public class ScenaristeController {
    
    private final ScenaristeService scenaristeService;
    
    public ScenaristeController(ScenaristeService scenaristeService) {
        this.scenaristeService = scenaristeService;
    }
    
    @GetMapping
    public List<ScenaristeDTO> getAllScenaristes() {
        return scenaristeService.getAllScenaristes();
    }
}