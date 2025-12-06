package com.example.films.service;

import com.example.films.dto.ScenaristeDTO;
import com.example.films.entity.Scenariste;
import com.example.films.repository.ScenaristeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScenaristeService {
    
    private final ScenaristeRepository scenaristeRepository;
    
    public ScenaristeService(ScenaristeRepository scenaristeRepository) {
        this.scenaristeRepository = scenaristeRepository;
    }
    
    public List<ScenaristeDTO> getAllScenaristes() {
        List<Scenariste> scenaristes = scenaristeRepository.findAllWithUtilisateur();
        return scenaristes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private ScenaristeDTO convertToDTO(Scenariste scenariste) {
        ScenaristeDTO dto = new ScenaristeDTO();
        dto.setIdScenariste(scenariste.getId());
        dto.setIdUtilisateur(scenariste.getUtilisateur().getId());
        dto.setNom(scenariste.getUtilisateur().getNom());
        dto.setEmail(scenariste.getUtilisateur().getEmail());
        dto.setSpecialite(scenariste.getSpecialite());
        dto.setBiographie(scenariste.getBiographie());
        return dto;
    }
}