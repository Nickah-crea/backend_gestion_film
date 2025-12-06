package com.example.films.controller;

import com.example.films.entity.StatutScene;
import com.example.films.service.StatutSceneService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/statuts-scene")
public class StatutSceneController {
    private final StatutSceneService statutSceneService;

    public StatutSceneController(StatutSceneService statutSceneService) {
        this.statutSceneService = statutSceneService;
    }

    @GetMapping
    public List<StatutScene> getAllStatuts() {
        return statutSceneService.getAllStatuts();
    }
}