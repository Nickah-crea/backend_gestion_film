package com.example.films.service;

import com.example.films.dto.CreateScenePlateauDTO;
import com.example.films.dto.ScenePlateauDTO;
import com.example.films.entity.ScenePlateau;
import com.example.films.entity.Scene;
import com.example.films.entity.Plateau;
import com.example.films.repository.ScenePlateauRepository;
import com.example.films.repository.SceneRepository;
import com.example.films.repository.PlateauRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScenePlateauService {
    private final ScenePlateauRepository scenePlateauRepository;
    private final SceneRepository sceneRepository;
    private final PlateauRepository plateauRepository;

    public ScenePlateauService(ScenePlateauRepository scenePlateauRepository,
                             SceneRepository sceneRepository,
                             PlateauRepository plateauRepository) {
        this.scenePlateauRepository = scenePlateauRepository;
        this.sceneRepository = sceneRepository;
        this.plateauRepository = plateauRepository;
    }

    public List<ScenePlateauDTO> getScenePlateauxBySceneId(Long sceneId) {
        List<ScenePlateau> scenePlateaux = scenePlateauRepository.findBySceneId(sceneId);
        return scenePlateaux.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<ScenePlateauDTO> getScenePlateauxByPlateauId(Long plateauId) {
        List<ScenePlateau> scenePlateaux = scenePlateauRepository.findByPlateauId(plateauId);
        return scenePlateaux.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<ScenePlateauDTO> getScenePlateauxByProjetId(Long projetId) {
        List<ScenePlateau> scenePlateaux = scenePlateauRepository.findByProjetId(projetId);
        return scenePlateaux.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public ScenePlateauDTO getScenePlateauById(Long id) {
        ScenePlateau scenePlateau = scenePlateauRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Association scène-plateau non trouvée"));
        return convertToDTO(scenePlateau);
    }

    @Transactional
    public ScenePlateauDTO createScenePlateau(CreateScenePlateauDTO createScenePlateauDTO) {
        // Vérifier si l'association existe déjà
        if (scenePlateauRepository.existsBySceneIdAndPlateauId(
            createScenePlateauDTO.getSceneId(), createScenePlateauDTO.getPlateauId())) {
            throw new RuntimeException("Cette scène utilise déjà ce plateau");
        }

        ScenePlateau scenePlateau = new ScenePlateau();
        scenePlateau.setDescriptionUtilisation(createScenePlateauDTO.getDescriptionUtilisation());

        Scene scene = sceneRepository.findById(createScenePlateauDTO.getSceneId())
                .orElseThrow(() -> new RuntimeException("Scène non trouvée"));
        scenePlateau.setScene(scene);

        Plateau plateau = plateauRepository.findById(createScenePlateauDTO.getPlateauId())
                .orElseThrow(() -> new RuntimeException("Plateau non trouvé"));
        scenePlateau.setPlateau(plateau);

        ScenePlateau savedScenePlateau = scenePlateauRepository.save(scenePlateau);
        return convertToDTO(savedScenePlateau);
    }

    @Transactional
    public ScenePlateauDTO updateScenePlateau(Long id, CreateScenePlateauDTO updateScenePlateauDTO) {
        ScenePlateau scenePlateau = scenePlateauRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Association scène-plateau non trouvée"));

        scenePlateau.setDescriptionUtilisation(updateScenePlateauDTO.getDescriptionUtilisation());

        if (!scenePlateau.getScene().getId().equals(updateScenePlateauDTO.getSceneId())) {
            Scene scene = sceneRepository.findById(updateScenePlateauDTO.getSceneId())
                    .orElseThrow(() -> new RuntimeException("Scène non trouvée"));
            scenePlateau.setScene(scene);
        }

        if (!scenePlateau.getPlateau().getId().equals(updateScenePlateauDTO.getPlateauId())) {
            Plateau plateau = plateauRepository.findById(updateScenePlateauDTO.getPlateauId())
                    .orElseThrow(() -> new RuntimeException("Plateau non trouvé"));
            scenePlateau.setPlateau(plateau);
        }

        ScenePlateau updatedScenePlateau = scenePlateauRepository.save(scenePlateau);
        return convertToDTO(updatedScenePlateau);
    }

    @Transactional
    public void deleteScenePlateau(Long id) {
        ScenePlateau scenePlateau = scenePlateauRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Association scène-plateau non trouvée"));
        scenePlateauRepository.delete(scenePlateau);
    }

    private ScenePlateauDTO convertToDTO(ScenePlateau scenePlateau) {
        ScenePlateauDTO dto = new ScenePlateauDTO();
        dto.setId(scenePlateau.getId());
        dto.setSceneId(scenePlateau.getScene().getId());
        dto.setPlateauId(scenePlateau.getPlateau().getId());
        dto.setDescriptionUtilisation(scenePlateau.getDescriptionUtilisation());
        dto.setCreeLe(scenePlateau.getCreeLe());
        
        // Informations de la scène
        dto.setSceneTitre(scenePlateau.getScene().getTitre());
        if (scenePlateau.getScene().getSequence() != null) {
            dto.setSequenceTitre(scenePlateau.getScene().getSequence().getTitre());
            if (scenePlateau.getScene().getSequence().getEpisode() != null) {
                dto.setEpisodeTitre(scenePlateau.getScene().getSequence().getEpisode().getTitre());
                if (scenePlateau.getScene().getSequence().getEpisode().getProjet() != null) {
                    dto.setProjetTitre(scenePlateau.getScene().getSequence().getEpisode().getProjet().getTitre());
                }
            }
        }
        
        // Informations du plateau
        dto.setPlateauNom(scenePlateau.getPlateau().getNom());
        dto.setPlateauType(scenePlateau.getPlateau().getTypePlateau());
        if (scenePlateau.getPlateau().getLieu() != null) {
            dto.setLieuNom(scenePlateau.getPlateau().getLieu().getNomLieu());
        }
        
        return dto;
    }
}