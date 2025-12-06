package com.example.films.service;

import com.example.films.entity.SceneStatut;
import com.example.films.repository.SceneStatutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SceneStatutService {
    
    private final SceneStatutRepository sceneStatutRepository;
    
    public String getCurrentStatutForScene(Long sceneId) {
        Optional<SceneStatut> currentStatut = sceneStatutRepository.findLatestStatutBySceneId(sceneId);
        return currentStatut.map(statut -> statut.getStatut().getNomStatutsScene())
                .orElse("Non défini");
    }
}