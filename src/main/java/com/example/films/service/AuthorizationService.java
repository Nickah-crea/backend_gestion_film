package com.example.films.service;

import com.example.films.repository.*;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {
    
    private final UtilisateurRepository utilisateurRepository;
    private final RealisateurRepository realisateurRepository;
    private final ScenaristeRepository scenaristeRepository;
    private final EpisodeRealisateurRepository episodeRealisateurRepository;
    private final EpisodeScenaristeRepository episodeScenaristeRepository;
    private final ProjetRepository projetRepository;
    private final SequenceRepository sequenceRepository;
    private final SceneRepository sceneRepository;
    
    public AuthorizationService(UtilisateurRepository utilisateurRepository,
                               RealisateurRepository realisateurRepository,
                               ScenaristeRepository scenaristeRepository,
                               EpisodeRealisateurRepository episodeRealisateurRepository,
                               EpisodeScenaristeRepository episodeScenaristeRepository,
                               ProjetRepository projetRepository,
                               SequenceRepository sequenceRepository,
                               SceneRepository sceneRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.realisateurRepository = realisateurRepository;
        this.scenaristeRepository = scenaristeRepository;
        this.episodeRealisateurRepository = episodeRealisateurRepository;
        this.episodeScenaristeRepository = episodeScenaristeRepository;
        this.projetRepository = projetRepository;
        this.sequenceRepository = sequenceRepository;
        this.sceneRepository = sceneRepository;
    }
    
    public boolean hasAccessToEpisode(Long utilisateurId, Long episodeId) {
        var utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        if ("ADMIN".equals(utilisateur.getRole())) {
            return true;
        }
        
        if ("REALISATEUR".equals(utilisateur.getRole())) {
            var realisateur = realisateurRepository.findByUtilisateurId(utilisateurId)
                    .orElseThrow(() -> new RuntimeException("Réalisateur non trouvé"));
            
            return episodeRealisateurRepository.findByEpisodeIdAndRealisateurId(episodeId, realisateur.getId())
                    .isPresent();
        }
        
        if ("SCENARISTE".equals(utilisateur.getRole())) {
            var scenariste = scenaristeRepository.findByUtilisateurId(utilisateurId)
                    .orElseThrow(() -> new RuntimeException("Scénariste non trouvé"));
            
            return episodeScenaristeRepository.findByEpisodeIdAndScenaristeId(episodeId, scenariste.getId())
                    .isPresent();
        }
        
        return false;
    }

    public boolean hasReadAccessToEpisode(Long utilisateurId, Long episodeId) {
        // Pour la lecture, permettre l'accès même si non associé
        var utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        if ("ADMIN".equals(utilisateur.getRole())) {
            return true;
        }
        
        // Les réalisateurs et scénaristes peuvent voir tous les épisodes du projet
        return true;
    }

    public boolean hasAccessToSequence(Long utilisateurId, Long sequenceId) {
        // Récupérer l'épisode parent de la séquence
        var sequence = sequenceRepository.findById(sequenceId)
                .orElseThrow(() -> new RuntimeException("Séquence non trouvée"));
        
        return hasAccessToEpisode(utilisateurId, sequence.getEpisode().getId());
    }

    public boolean hasAccessToScene(Long utilisateurId, Long sceneId) {
        // Récupérer la séquence parent de la scène
        var scene = sceneRepository.findById(sceneId)
                .orElseThrow(() -> new RuntimeException("Scène non trouvée"));
        
        return hasAccessToSequence(utilisateurId, scene.getSequence().getId());
    }

    public boolean hasAccessToProjet(Long utilisateurId, Long projetId) {
        var utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        if ("ADMIN".equals(utilisateur.getRole())) {
            return true;
        }
        
        // Vérifier si l'utilisateur est associé au projet via ses épisodes
        if ("REALISATEUR".equals(utilisateur.getRole())) {
            var realisateur = realisateurRepository.findByUtilisateurId(utilisateurId)
                    .orElseThrow(() -> new RuntimeException("Réalisateur non trouvé"));
            
            return episodeRealisateurRepository.existsByRealisateurIdAndProjetId(realisateur.getId(), projetId);
        }
        
        if ("SCENARISTE".equals(utilisateur.getRole())) {
            var scenariste = scenaristeRepository.findByUtilisateurId(utilisateurId)
                    .orElseThrow(() -> new RuntimeException("Scénariste non trouvé"));
            
            return episodeScenaristeRepository.existsByScenaristeIdAndProjetId(scenariste.getId(), projetId);
        }
        
        return false;
    }

    public boolean hasAccessToSceneCreation(Long utilisateurId, Long episodeId) {
        return hasAccessToEpisode(utilisateurId, episodeId);
    }

    public boolean hasAccessToDialogueCreation(Long utilisateurId, Long sceneId) {
        var scene = sceneRepository.findById(sceneId)
                .orElseThrow(() -> new RuntimeException("Scène non trouvée"));
        
        return hasAccessToEpisode(utilisateurId, scene.getSequence().getEpisode().getId());
    }

    public boolean hasAccessToLieuCreation(Long utilisateurId, Long projetId) {
        var utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        if ("ADMIN".equals(utilisateur.getRole())) {
            return true;
        }
        
        // Vérifier si l'utilisateur est associé au projet via ses épisodes
        if ("REALISATEUR".equals(utilisateur.getRole())) {
            var realisateur = realisateurRepository.findByUtilisateurId(utilisateurId)
                    .orElseThrow(() -> new RuntimeException("Réalisateur non trouvé"));
            
            return episodeRealisateurRepository.existsByRealisateurIdAndProjetId(realisateur.getId(), projetId);
        }
        
        if ("SCENARISTE".equals(utilisateur.getRole())) {
            var scenariste = scenaristeRepository.findByUtilisateurId(utilisateurId)
                    .orElseThrow(() -> new RuntimeException("Scénariste non trouvé"));
            
            return episodeScenaristeRepository.existsByScenaristeIdAndProjetId(scenariste.getId(), projetId);
        }
        
        return false;
    }
}