package com.example.films.service;

import com.example.films.dto.CreateSceneLieuDTO;
import com.example.films.dto.PlateauDTO;
import com.example.films.dto.SceneLieuDTO;
import com.example.films.entity.Scene;
import com.example.films.entity.SceneLieu;
import com.example.films.entity.Lieu;
import com.example.films.entity.Plateau;
import com.example.films.repository.SceneLieuRepository;
import com.example.films.repository.SceneRepository;
import com.example.films.repository.LieuRepository;
import com.example.films.repository.PlateauRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SceneLieuService {
    private final SceneLieuRepository sceneLieuRepository;
    private final SceneRepository sceneRepository;
    private final LieuRepository lieuRepository;
     private final PlateauRepository plateauRepository; 

    public SceneLieuService(SceneLieuRepository sceneLieuRepository, 
                          SceneRepository sceneRepository, 
                          LieuRepository lieuRepository,
                          PlateauRepository plateauRepository) {
        this.sceneLieuRepository = sceneLieuRepository;
        this.sceneRepository = sceneRepository;
        this.lieuRepository = lieuRepository;
        this.plateauRepository = plateauRepository;
    }

    public List<SceneLieuDTO> getSceneLieusBySceneId(Long sceneId) {
        List<SceneLieu> sceneLieus = sceneLieuRepository.findBySceneId(sceneId);
        return sceneLieus.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<SceneLieuDTO> getSceneLieusByLieuId(Long lieuId) {
        List<SceneLieu> sceneLieus = sceneLieuRepository.findByLieuId(lieuId);
        return sceneLieus.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<SceneLieuDTO> getSceneLieusByProjetId(Long projetId) {
        List<SceneLieu> sceneLieus = sceneLieuRepository.findByProjetId(projetId);
        return sceneLieus.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public SceneLieuDTO getSceneLieuById(Long id) {
        SceneLieu sceneLieu = sceneLieuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Association scène-lieu non trouvée"));
        return convertToDTO(sceneLieu);
    }


    @Transactional
    public SceneLieuDTO createSceneLieu(CreateSceneLieuDTO createSceneLieuDTO) {
    // Vérifier si l'association existe déjà avec le plateau
    if (createSceneLieuDTO.getPlateauId() != null) {
        if (sceneLieuRepository.existsBySceneIdAndLieuIdAndPlateauId(
            createSceneLieuDTO.getSceneId(), 
            createSceneLieuDTO.getLieuId(),
            createSceneLieuDTO.getPlateauId())) {
            throw new RuntimeException("Cette scène utilise déjà ce lieu avec ce plateau");
        }
    } else {
        if (sceneLieuRepository.existsBySceneIdAndLieuId(
            createSceneLieuDTO.getSceneId(), createSceneLieuDTO.getLieuId())) {
            throw new RuntimeException("Cette scène utilise déjà ce lieu");
        }
    }

    SceneLieu sceneLieu = new SceneLieu();
    sceneLieu.setDescriptionUtilisation(createSceneLieuDTO.getDescriptionUtilisation());

    Scene scene = sceneRepository.findById(createSceneLieuDTO.getSceneId())
            .orElseThrow(() -> new RuntimeException("Scène non trouvée"));
    sceneLieu.setScene(scene);

    Lieu lieu = lieuRepository.findById(createSceneLieuDTO.getLieuId())
            .orElseThrow(() -> new RuntimeException("Lieu non trouvé"));
    sceneLieu.setLieu(lieu);

    // Gestion du plateau
    if (createSceneLieuDTO.getPlateauId() != null) {
        Plateau plateau = plateauRepository.findById(createSceneLieuDTO.getPlateauId())
                .orElseThrow(() -> new RuntimeException("Plateau non trouvé"));
        sceneLieu.setPlateau(plateau);
    }

    SceneLieu savedSceneLieu = sceneLieuRepository.save(sceneLieu);
    return convertToDTO(savedSceneLieu);
}

    @Transactional
    public SceneLieuDTO updateSceneLieu(Long id, CreateSceneLieuDTO updateSceneLieuDTO) {
        SceneLieu sceneLieu = sceneLieuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Association scène-lieu non trouvée"));

        sceneLieu.setDescriptionUtilisation(updateSceneLieuDTO.getDescriptionUtilisation());

        if (!sceneLieu.getScene().getId().equals(updateSceneLieuDTO.getSceneId())) {
            Scene scene = sceneRepository.findById(updateSceneLieuDTO.getSceneId())
                    .orElseThrow(() -> new RuntimeException("Scène non trouvée"));
            sceneLieu.setScene(scene);
        }

        if (!sceneLieu.getLieu().getId().equals(updateSceneLieuDTO.getLieuId())) {
            Lieu lieu = lieuRepository.findById(updateSceneLieuDTO.getLieuId())
                    .orElseThrow(() -> new RuntimeException("Lieu non trouvé"));
            sceneLieu.setLieu(lieu);
        }

        SceneLieu updatedSceneLieu = sceneLieuRepository.save(sceneLieu);
        return convertToDTO(updatedSceneLieu);
    }

    @Transactional
    public void deleteSceneLieu(Long id) {
        SceneLieu sceneLieu = sceneLieuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Association scène-lieu non trouvée"));
        sceneLieuRepository.delete(sceneLieu);
    }

     // Méthode pour récupérer les plateaux par lieu
    public List<PlateauDTO> getPlateauxByLieuId(Long lieuId) {
        List<Plateau> plateaux = plateauRepository.findByLieuId(lieuId);
        return plateaux.stream().map(this::convertPlateauToDTO).collect(Collectors.toList());
    }
    
    private PlateauDTO convertPlateauToDTO(Plateau plateau) {
        PlateauDTO dto = new PlateauDTO();
        dto.setId(plateau.getId());
        dto.setNom(plateau.getNom());
        dto.setTypePlateau(plateau.getTypePlateau());
        dto.setDescription(plateau.getDescription());
        dto.setLieuId(plateau.getLieu().getId());
        dto.setLieuNom(plateau.getLieu().getNomLieu());
        return dto;
    }


    private SceneLieuDTO convertToDTO(SceneLieu sceneLieu) {
        SceneLieuDTO dto = new SceneLieuDTO();
        dto.setId(sceneLieu.getId());
        dto.setSceneId(sceneLieu.getScene().getId());
        dto.setLieuId(sceneLieu.getLieu().getId());
        dto.setDescriptionUtilisation(sceneLieu.getDescriptionUtilisation());
        dto.setSceneTitre(sceneLieu.getScene().getTitre());
        dto.setLieuNom(sceneLieu.getLieu().getNomLieu());
        
        // Informations du plateau si disponible
        if (sceneLieu.getPlateau() != null) {
            dto.setPlateauId(sceneLieu.getPlateau().getId());
            dto.setPlateauNom(sceneLieu.getPlateau().getNom());
        }

        dto.setSequenceTitre(sceneLieu.getScene().getSequence().getTitre());
        dto.setEpisodeTitre(sceneLieu.getScene().getSequence().getEpisode().getTitre());
        dto.setProjetTitre(sceneLieu.getScene().getSequence().getEpisode().getProjet().getTitre());
        dto.setProjetId(sceneLieu.getScene().getSequence().getEpisode().getProjet().getId());
        return dto;
    }

    
}