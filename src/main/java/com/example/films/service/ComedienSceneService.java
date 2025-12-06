package com.example.films.service;

import com.example.films.dto.ComedienSceneDTO;
import com.example.films.dto.CreateComedienSceneDTO;
import com.example.films.entity.ComedienScene;
import com.example.films.repository.ComedienSceneRepository;
import com.example.films.repository.ComedienRepository;
import com.example.films.repository.SceneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComedienSceneService {
    
    private final ComedienSceneRepository comedienSceneRepository;
    private final ComedienRepository comedienRepository;
    private final SceneRepository sceneRepository;
    private final SceneStatutService sceneStatutService;
    
    public List<ComedienSceneDTO> getAllLiaisons() {
        return comedienSceneRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public ComedienSceneDTO createLiaison(CreateComedienSceneDTO dto) {
        // Vérifier si l'association existe déjà
        if (comedienSceneRepository.existsBySceneIdAndComedienId(dto.getIdScene(), dto.getIdComedien())) {
            throw new RuntimeException("Cette association existe déjà");
        }
        
        var comedien = comedienRepository.findById(dto.getIdComedien())
                .orElseThrow(() -> new RuntimeException("Comédien non trouvé"));
        
        var scene = sceneRepository.findById(dto.getIdScene())
                .orElseThrow(() -> new RuntimeException("Scène non trouvée"));
        
        var liaison = new ComedienScene();
        liaison.setComedien(comedien);
        liaison.setScene(scene);
        
        var savedLiaison = comedienSceneRepository.save(liaison);
        return convertToDTO(savedLiaison);
    }
    
    public void deleteLiaison(Long id) {
        if (!comedienSceneRepository.existsById(id)) {
            throw new RuntimeException("Association non trouvée");
        }
        comedienSceneRepository.deleteById(id);
    }
    
    private ComedienSceneDTO convertToDTO(ComedienScene liaison) {
        var dto = new ComedienSceneDTO();
        dto.setId(liaison.getId());
        dto.setIdScene(liaison.getScene().getId());
        dto.setIdComedien(liaison.getComedien().getId());
        dto.setComedienNom(liaison.getComedien().getNom());
        dto.setSceneTitre(liaison.getScene().getTitre());
        
        // Accéder au titre de la séquence via l'objet sequence
        if (liaison.getScene().getSequence() != null) {
            dto.setSequenceTitre(liaison.getScene().getSequence().getTitre());
        } else {
            dto.setSequenceTitre("Non assigné");
        }
        
        // Récupérer le statut actuel de la scène
        dto.setSceneStatut(sceneStatutService.getCurrentStatutForScene(liaison.getScene().getId()));
        dto.setCreeLe(liaison.getCreeLe());
        return dto;
    }
    public List<ComedienSceneDTO> getComediensBySceneId(Long sceneId) {
    List<ComedienScene> liaisons = comedienSceneRepository.findBySceneId(sceneId);
    return liaisons.stream().map(this::convertToDTO).collect(Collectors.toList());
}
}