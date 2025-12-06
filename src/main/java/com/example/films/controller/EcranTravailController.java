package com.example.films.controller;

import com.example.films.dto.DialogueDTO;
import com.example.films.dto.EpisodeDTO;
import com.example.films.dto.SceneDTO;
import com.example.films.dto.SceneLieuDTO;
import com.example.films.dto.SequenceDTO;
import com.example.films.dto.ProjetDTO;
import com.example.films.service.AuthorizationService;
import com.example.films.service.DialogueService;
import com.example.films.service.EpisodeService;
import com.example.films.service.SceneLieuService;
import com.example.films.service.SceneService;
import com.example.films.service.SequenceService;
import com.example.films.service.ProjetService; // Ajouter cette importation
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ecran-travail")
@CrossOrigin(origins = "http://localhost:5173")
public class EcranTravailController {
    private static final Logger logger = LoggerFactory.getLogger(EcranTravailController.class);
    private final EpisodeService episodeService;
    private final SequenceService sequenceService;
    private final SceneService sceneService;
    private final SceneLieuService sceneLieuService;
    private final DialogueService dialogueService;
    private final AuthorizationService authorizationService;
    private final ProjetService projetService; // Ajouter cette déclaration

    public EcranTravailController(EpisodeService episodeService, SequenceService sequenceService,
                                  SceneService sceneService, SceneLieuService sceneLieuService,
                                  DialogueService dialogueService, AuthorizationService authorizationService,
                                  ProjetService projetService) { // Ajouter ce paramètre
        this.episodeService = episodeService;
        this.sequenceService = sequenceService;
        this.sceneService = sceneService;
        this.sceneLieuService = sceneLieuService;
        this.dialogueService = dialogueService;
        this.authorizationService = authorizationService;
        this.projetService = projetService; // Initialiser le service
    }

    // Récupérer tous les informations d'un projet
    @GetMapping("/projets/{projetId}/infos")
    public ResponseEntity<ProjetDTO> getProjetInfos(@PathVariable Long projetId) {
        logger.info("Récupération infos projet ID: {}", projetId);
        try {
            ProjetDTO projet = projetService.getProjetById(projetId); // Maintenant ça fonctionnera
            return ResponseEntity.ok(projet);
        } catch (RuntimeException e) {
            logger.error("Projet non trouvé ID: {}", projetId);
            return ResponseEntity.notFound().build();
        }
    }
 
    // Récupérer tous les épisodes d'un projet
    @GetMapping("/projets/{projetId}/episodes")
    public ResponseEntity<List<EpisodeDTO>> getEpisodes(@PathVariable Long projetId) {
        logger.info("Requête pour récupérer les épisodes du projet ID: {}", projetId);
        try {
            List<EpisodeDTO> episodes = episodeService.getEpisodesByProjetId(projetId);
            if (episodes.isEmpty()) {
                logger.warn("Aucun épisode trouvé pour le projet ID: {}", projetId);
                return ResponseEntity.noContent().build(); // 204 si aucun épisode
            }
            logger.info("Épisodes trouvés: {}", episodes.size());
            return ResponseEntity.ok(episodes);
        } catch (RuntimeException e) {
            logger.error("Erreur lors de la récupération des épisodes pour le projet ID: {}. Erreur: {}", projetId, e.getMessage());
            return ResponseEntity.status(404).body(null); // 404 si projet non trouvé
        }
    }

    // Récupérer toutes les séquences d'un épisode
    @GetMapping("/episodes/{episodeId}/sequences")
    public ResponseEntity<List<SequenceDTO>> getSequences(@PathVariable Long episodeId) {
        logger.info("Requête pour récupérer les séquences de l'épisode ID: {}", episodeId);
        try {
            List<SequenceDTO> sequences = sequenceService.getSequencesByEpisodeId(episodeId);
            if (sequences.isEmpty()) {
                logger.warn("Aucune séquence trouvée pour l'épisode ID: {}", episodeId);
                return ResponseEntity.noContent().build();
            }
            logger.info("Séquences trouvées: {}", sequences.size());
            return ResponseEntity.ok(sequences);
        } catch (RuntimeException e) {
            logger.error("Erreur lors de la récupération des séquences pour l'épisode ID: {}. Erreur: {}", episodeId, e.getMessage());
            return ResponseEntity.status(404).body(null);
        }
    }

    // Récupérer les détails d'une séquence (avec scènes, dialogues, lieux/plateaux)
   @GetMapping("/sequences/{sequenceId}")
    public ResponseEntity<SequenceDTO> getSequenceDetails(@PathVariable Long sequenceId) {
        logger.info("Détails séquence ID: {}", sequenceId);
        try {
            SequenceDTO sequence = sequenceService.getSequenceById(sequenceId);
            logger.info("Séquence trouvée: {}", sequence.getTitre());
            
            List<SceneDTO> scenes = sceneService.getScenesBySequenceId(sequenceId);
            logger.info("Nombre de scènes trouvées: {}", scenes.size());
            
            for (SceneDTO scene : scenes) {
                logger.info("Traitement scène ID: {}", scene.getIdScene());
                
                List<SceneLieuDTO> sceneLieus = sceneLieuService.getSceneLieusBySceneId(scene.getIdScene());
                logger.info("Lieux pour scène {}: {}", scene.getIdScene(), sceneLieus.size());
                scene.setSceneLieus(sceneLieus);
                
                List<DialogueDTO> dialogues = dialogueService.getDialoguesByScene(scene.getIdScene());
                logger.info("Dialogues pour scène {}: {}", scene.getIdScene(), dialogues.size());
                scene.setDialogues(dialogues);
            }
            sequence.setScenes(scenes);
            return ResponseEntity.ok(sequence);
        } catch (RuntimeException e) {
            logger.error("Erreur détails séquence: {}", e.getMessage());
            return ResponseEntity.status(404).body(null);
        }
    }

    // Récupérer la séquence suivante
    @GetMapping("/sequences/{sequenceId}/next")
    public ResponseEntity<SequenceDTO> getNextSequence(@PathVariable Long sequenceId) {
        logger.info("Requête pour récupérer la séquence suivante pour ID: {}", sequenceId);
        try {
            SequenceDTO currentSequence = sequenceService.getSequenceById(sequenceId);
            Long episodeId = currentSequence.getEpisodeId();
            List<SequenceDTO> sequences = sequenceService.getSequencesByEpisodeId(episodeId)
                    .stream().sorted((s1, s2) -> Integer.compare(s1.getOrdre(), s2.getOrdre()))
                    .collect(Collectors.toList());

            int currentIndex = -1;
            for (int i = 0; i < sequences.size(); i++) {
                if (sequences.get(i).getIdSequence().equals(sequenceId)) {
                    currentIndex = i;
                    break;
                }
            }

            if (currentIndex < sequences.size() - 1) {
                SequenceDTO nextSequence = sequences.get(currentIndex + 1);
                logger.info("Séquance suivante trouvée dans le même épisode: {}", nextSequence.getIdSequence());
                return loadSequenceDetails(nextSequence);
            }

            // Passage aux épisodes suivants
            EpisodeDTO currentEpisode = episodeService.getEpisodeById(episodeId);
            List<EpisodeDTO> episodes = episodeService.getEpisodesByProjetId(currentEpisode.getProjetId())
                    .stream().sorted((e1, e2) -> Integer.compare(e1.getOrdre(), e2.getOrdre()))
                    .collect(Collectors.toList());
            int episodeIndex = -1;
            for (int i = 0; i < episodes.size(); i++) {
                if (episodes.get(i).getIdEpisode().equals(episodeId)) {
                    episodeIndex = i;
                    break;
                }
            }

            // Boucle pour trouver le prochain épisode avec au moins une séquence
            episodeIndex++;
            while (episodeIndex < episodes.size()) {
                Long nextEpisodeId = episodes.get(episodeIndex).getIdEpisode();
                List<SequenceDTO> nextEpisodeSequences = sequenceService.getSequencesByEpisodeId(nextEpisodeId)
                        .stream().sorted((s1, s2) -> Integer.compare(s1.getOrdre(), s2.getOrdre()))
                        .collect(Collectors.toList());
                if (!nextEpisodeSequences.isEmpty()) {
                    SequenceDTO nextSequence = nextEpisodeSequences.get(0);
                    logger.info("Séquance suivante trouvée dans l'épisode suivant: {}", nextSequence.getIdSequence());
                    return loadSequenceDetails(nextSequence);
                }
                episodeIndex++;  // Passer à l'épisode suivant si vide
            }

            logger.warn("Aucune séquence suivante trouvée pour ID: {}", sequenceId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("Erreur lors de la récupération de la séquence suivante pour ID: {}. Erreur: {}", sequenceId, e.getMessage());
            return ResponseEntity.status(404).body(null);
        }
    }

    // Récupérer la séquence précédente
    @GetMapping("/sequences/{sequenceId}/prev")
    public ResponseEntity<SequenceDTO> getPrevSequence(@PathVariable Long sequenceId) {
        logger.info("Requête pour récupérer la séquence précédente pour ID: {}", sequenceId);
        try {
            SequenceDTO currentSequence = sequenceService.getSequenceById(sequenceId);
            Long episodeId = currentSequence.getEpisodeId();
            List<SequenceDTO> sequences = sequenceService.getSequencesByEpisodeId(episodeId)
                    .stream().sorted((s1, s2) -> Integer.compare(s1.getOrdre(), s2.getOrdre()))
                    .collect(Collectors.toList());

            int currentIndex = -1;
            for (int i = 0; i < sequences.size(); i++) {
                if (sequences.get(i).getIdSequence().equals(sequenceId)) {
                    currentIndex = i;
                    break;
                }
            }

            if (currentIndex > 0) {
                SequenceDTO prevSequence = sequences.get(currentIndex - 1);
                logger.info("Séquance précédente trouvée dans le même épisode: {}", prevSequence.getIdSequence());
                return loadSequenceDetails(prevSequence);
            }

            // Passage aux épisodes précédents
            EpisodeDTO currentEpisode = episodeService.getEpisodeById(episodeId);
            List<EpisodeDTO> episodes = episodeService.getEpisodesByProjetId(currentEpisode.getProjetId())
                    .stream().sorted((e1, e2) -> Integer.compare(e1.getOrdre(), e2.getOrdre()))
                    .collect(Collectors.toList());
            int episodeIndex = -1;
            for (int i = 0; i < episodes.size(); i++) {
                if (episodes.get(i).getIdEpisode().equals(episodeId)) {
                    episodeIndex = i;
                    break;
                }
            }

            // Boucle pour trouver l'épisode précédent avec au moins une séquence
            episodeIndex--;
            while (episodeIndex >= 0) {
                Long prevEpisodeId = episodes.get(episodeIndex).getIdEpisode();
                List<SequenceDTO> prevEpisodeSequences = sequenceService.getSequencesByEpisodeId(prevEpisodeId)
                        .stream().sorted((s1, s2) -> Integer.compare(s1.getOrdre(), s2.getOrdre()))
                        .collect(Collectors.toList());
                if (!prevEpisodeSequences.isEmpty()) {
                    SequenceDTO prevSequence = prevEpisodeSequences.get(prevEpisodeSequences.size() - 1);
                    logger.info("Séquance précédente trouvée dans l'épisode précédent: {}", prevSequence.getIdSequence());
                    return loadSequenceDetails(prevSequence);
                }
                episodeIndex--;  // Passer à l'épisode précédent si vide
            }

            logger.warn("Aucune séquence précédente trouvée pour ID: {}", sequenceId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("Erreur lors de la récupération de la séquence précédente pour ID: {}. Erreur: {}", sequenceId, e.getMessage());
            return ResponseEntity.status(404).body(null);
        }
    }

    private ResponseEntity<SequenceDTO> loadSequenceDetails(SequenceDTO sequence) {
        List<SceneDTO> scenes = sceneService.getScenesBySequenceId(sequence.getIdSequence());
        for (SceneDTO scene : scenes) {
            List<SceneLieuDTO> sceneLieus = sceneLieuService.getSceneLieusBySceneId(scene.getIdScene());
            scene.setSceneLieus(sceneLieus);
            List<DialogueDTO> dialogues = dialogueService.getDialoguesByScene(scene.getIdScene());
            scene.setDialogues(dialogues);
        }
        sequence.setScenes(scenes);
        return ResponseEntity.ok(sequence);
    }
}


