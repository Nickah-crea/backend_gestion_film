package com.example.films.service;

import com.example.films.dto.CreateSceneDTO;
import com.example.films.dto.SceneDTO;
import com.example.films.entity.Scene;
import com.example.films.entity.SceneStatut;
import com.example.films.entity.Sequence;
import com.example.films.entity.StatutScene;
import com.example.films.repository.SceneRepository;
import com.example.films.repository.SceneStatutRepository;
import com.example.films.repository.SequenceRepository;
import com.example.films.repository.StatutSceneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.films.dto.RechercheSceneDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Comparator;

@Service
public class SceneService {
    private final SceneRepository sceneRepository;
    private final SceneStatutRepository sceneStatutRepository;
    private final SequenceRepository sequenceRepository;
    private final StatutSceneRepository statutSceneRepository;

    public SceneService(SceneRepository sceneRepository,
                       SceneStatutRepository sceneStatutRepository,
                       SequenceRepository sequenceRepository,
                       StatutSceneRepository statutSceneRepository) {
        this.sceneRepository = sceneRepository;
        this.sceneStatutRepository = sceneStatutRepository;
        this.sequenceRepository = sequenceRepository;
        this.statutSceneRepository = statutSceneRepository;
    }

    public List<SceneDTO> getScenesBySequenceId(Long sequenceId) {
        List<Scene> scenes = sceneRepository.findBySequenceId(sequenceId);
        return scenes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<SceneDTO> getScenesByProjetId(Long projetId) {
        List<Scene> scenes = sceneRepository.findBySequenceEpisodeProjetId(projetId);
        return scenes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SceneDTO createScene(Long sequenceId, CreateSceneDTO createSceneDTO) {
        // Créer la scène
        Scene scene = new Scene();
        scene.setTitre(createSceneDTO.getTitre());
        scene.setOrdre(createSceneDTO.getOrdre());
        scene.setSynopsis(createSceneDTO.getSynopsis());
        
        // Associer la séquence
        Sequence sequence = sequenceRepository.findById(sequenceId)
                .orElseThrow(() -> new RuntimeException("Séquence non trouvée"));
        scene.setSequence(sequence);
        
        // Sauvegarder la scène
        Scene savedScene = sceneRepository.save(scene);
        
        // Créer le statut de la scène
        SceneStatut sceneStatut = new SceneStatut();
        sceneStatut.setScene(savedScene);
        
        StatutScene statut = statutSceneRepository.findById(createSceneDTO.getStatutId())
                .orElseThrow(() -> new RuntimeException("Statut non trouvé"));
        sceneStatut.setStatut(statut);
        sceneStatut.setDateDebut(LocalDateTime.now());
        
        sceneStatutRepository.save(sceneStatut);
        
        // Retourner le DTO
        return convertToDTO(savedScene);
    }

    private SceneDTO convertToDTO(Scene scene) {
        SceneDTO dto = new SceneDTO();
        dto.setIdScene(scene.getId());
        dto.setSequenceId(scene.getSequence().getId());
        dto.setTitre(scene.getTitre());
        dto.setOrdre(scene.getOrdre());
        dto.setSynopsis(scene.getSynopsis());
        dto.setCreeLe(scene.getCreeLe());
        dto.setModifieLe(scene.getModifieLe());
        dto.setSequenceTitre(scene.getSequence().getTitre());
        
        // Récupérer le statut le plus récent
        Optional<SceneStatut> statutOpt = sceneStatutRepository.findLatestStatutBySceneId(scene.getId());
        if (statutOpt.isPresent()) {
            SceneStatut statut = statutOpt.get();
            dto.setStatutNom(statut.getStatut().getNomStatutsScene());
            
            // Si le statut est terminal, utiliser date_fin
            if (statut.getDateFin() != null && 
                ("tournee".equals(statut.getStatut().getCode()) || 
                 "validee".equals(statut.getStatut().getCode()))) {
                dto.setDateFin(statut.getDateFin());
            }
        } else {
            dto.setStatutNom("Non défini");
        }
        
        return dto;
    }

    @Transactional
    public SceneDTO updateScene(Long id, CreateSceneDTO updateSceneDTO) {
        Scene scene = sceneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scène non trouvée"));
        
        scene.setTitre(updateSceneDTO.getTitre());
        scene.setOrdre(updateSceneDTO.getOrdre());
        scene.setSynopsis(updateSceneDTO.getSynopsis());
        
        Scene updatedScene = sceneRepository.save(scene);
        
        // Mettre à jour le statut si nécessaire
        if (updateSceneDTO.getStatutId() != null) {
            Optional<SceneStatut> currentStatutOpt = sceneStatutRepository.findLatestStatutBySceneId(id);
            
            if (currentStatutOpt.isPresent()) {
                SceneStatut currentStatut = currentStatutOpt.get();
                // Si le statut a changé, créer une nouvelle entrée
                if (!currentStatut.getStatut().getId().equals(updateSceneDTO.getStatutId())) {
                    currentStatut.setDateFin(LocalDateTime.now());
                    sceneStatutRepository.save(currentStatut);
                    
                    SceneStatut newStatut = new SceneStatut();
                    newStatut.setScene(scene);
                    StatutScene statut = statutSceneRepository.findById(updateSceneDTO.getStatutId())
                            .orElseThrow(() -> new RuntimeException("Statut non trouvé"));
                    newStatut.setStatut(statut);
                    newStatut.setDateDebut(LocalDateTime.now());
                    sceneStatutRepository.save(newStatut);
                }
            }
        }
        
        return convertToDTO(updatedScene);
    }
    @Transactional
    public void deleteScene(Long id) {
        Scene scene = sceneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scène non trouvée"));
        
        // Sauvegarder l'ordre et la séquence avant suppression
        Integer deletedOrder = scene.getOrdre();
        Long sequenceId = scene.getSequence().getId();
        
        // Supprimer la scène
        sceneRepository.delete(scene);
        
        // Recalculer les ordres des scènes restantes
        recalculerOrdresScenes(sequenceId, deletedOrder);
    }

    private void recalculerOrdresScenes(Long sequenceId, Integer deletedOrder) {
        List<Scene> scenes = sceneRepository.findBySequenceId(sequenceId);
        
        // Trier par ordre actuel
        List<Scene> scenesToUpdate = scenes.stream()
                .filter(scene -> scene.getOrdre() > deletedOrder)
                .sorted(Comparator.comparingInt(Scene::getOrdre))
                .collect(Collectors.toList());
        
        // Mettre à jour les ordres
        int newOrder = deletedOrder;
        for (Scene scene : scenesToUpdate) {
            scene.setOrdre(newOrder);
            sceneRepository.save(scene);
            newOrder++;
        }
    }

    public SceneDTO getSceneById(Long id) {
        Scene scene = sceneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scène non trouvée"));
        return convertToDTO(scene);
    }

    public List<SceneDTO> getScenesByEpisodeId(Long episodeId) {
        List<Scene> scenes = sceneRepository.findByEpisodeId(episodeId);
        return scenes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SceneDTO> getAllScenes() {
        List<Scene> scenes = sceneRepository.findAllOrdered();
        
        return scenes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<RechercheSceneDTO> rechercherScenes(String query) {
        return sceneRepository.rechercherScenes(query.toLowerCase());
    }

    @Transactional(readOnly = true)
    public List<SceneDTO> getScenesByProjet(Long projetId) {
        List<Scene> scenes = sceneRepository.findByProjetId(projetId);
        return scenes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

}

