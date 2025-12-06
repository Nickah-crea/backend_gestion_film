package com.example.films.service;

import com.example.films.entity.StatutScene;
import com.example.films.repository.StatutSceneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatutSceneService {
    private final StatutSceneRepository statutSceneRepository;

    public StatutSceneService(StatutSceneRepository statutSceneRepository) {
        this.statutSceneRepository = statutSceneRepository;
    }

    public List<StatutScene> getAllStatuts() {
        return statutSceneRepository.findAll();
    }
}