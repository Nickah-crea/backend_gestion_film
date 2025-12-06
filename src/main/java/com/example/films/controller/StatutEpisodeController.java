
package com.example.films.controller;

import com.example.films.entity.StatutEpisode;
import com.example.films.service.StatutEpisodeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/statuts-episode")
public class StatutEpisodeController {
    private final StatutEpisodeService statutEpisodeService;

    public StatutEpisodeController(StatutEpisodeService statutEpisodeService) {
        this.statutEpisodeService = statutEpisodeService;
    }

    @GetMapping
    public List<StatutEpisode> getAllStatuts() {
        return statutEpisodeService.getAllStatuts();
    }
}