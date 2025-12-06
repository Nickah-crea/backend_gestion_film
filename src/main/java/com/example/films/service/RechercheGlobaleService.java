// RechercheGlobaleService.java
package com.example.films.service;

import com.example.films.dto.RechercheGlobaleDTO;
import com.example.films.repository.*;
import com.example.films.entity.Projet;
import com.example.films.entity.Episode;
import com.example.films.entity.Sequence;
import com.example.films.entity.Scene;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.time.LocalDateTime;
import java.time.LocalDate; 

@Service
public class RechercheGlobaleService {
    
    private final ProjetRepository projetRepository;
    private final EpisodeRepository episodeRepository;
    private final SequenceRepository sequenceRepository;
    private final SceneRepository sceneRepository;
    private final ProjetStatutRepository projetStatutRepository;
    private final EpisodeStatutRepository episodeStatutRepository;
    private final SequenceStatutRepository sequenceStatutRepository;
    private final SceneStatutRepository sceneStatutRepository;
    
    public RechercheGlobaleService(ProjetRepository projetRepository,
                                 EpisodeRepository episodeRepository,
                                 SequenceRepository sequenceRepository,
                                 SceneRepository sceneRepository,
                                 ProjetStatutRepository projetStatutRepository,
                                 EpisodeStatutRepository episodeStatutRepository,
                                 SequenceStatutRepository sequenceStatutRepository,
                                 SceneStatutRepository sceneStatutRepository) {
        this.projetRepository = projetRepository;
        this.episodeRepository = episodeRepository;
        this.sequenceRepository = sequenceRepository;
        this.sceneRepository = sceneRepository;
        this.projetStatutRepository = projetStatutRepository;
        this.episodeStatutRepository = episodeStatutRepository;
        this.sequenceStatutRepository = sequenceStatutRepository;
        this.sceneStatutRepository = sceneStatutRepository;
    }
    
    public List<RechercheGlobaleDTO> rechercherGlobalement(String query, String statut, String dateFiltre, String specificDate) {
        // Recherche dans les projets
        List<RechercheGlobaleDTO> projets = rechercherProjets(query, statut, dateFiltre, specificDate);
        
        // Recherche dans les épisodes
        List<RechercheGlobaleDTO> episodes = rechercherEpisodes(query, statut, dateFiltre, specificDate); // CORRIGÉ
        
        // Recherche dans les séquences
        List<RechercheGlobaleDTO> sequences = rechercherSequences(query, statut, dateFiltre, specificDate); // CORRIGÉ
        
        // Recherche dans les scènes
        List<RechercheGlobaleDTO> scenes = rechercherScenes(query, statut, dateFiltre, specificDate); // CORRIGÉ
        
        // Combiner tous les résultats
        List<RechercheGlobaleDTO> tousResultats = Stream.concat(
            projets.stream(),
            Stream.concat(
                episodes.stream(),
                Stream.concat(sequences.stream(), scenes.stream())
            )
        ).collect(Collectors.toList());
        
        // Trier par date de modification (les plus récents en premier)
        return tousResultats.stream()
                .sorted((a, b) -> b.getModifieLe().compareTo(a.getModifieLe()))
                .collect(Collectors.toList());
    }
    
    private List<RechercheGlobaleDTO> rechercherProjets(String query, String statut, String dateFiltre, String specificDate) {
        String queryLower = query.toLowerCase();
        
        return projetRepository.findAllWithGenre().stream()
                .filter(projet -> 
                    projet.getTitre().toLowerCase().contains(queryLower) ||
                    (projet.getSynopsis() != null && projet.getSynopsis().toLowerCase().contains(queryLower)) ||
                    (projet.getGenre() != null && projet.getGenre().getNomGenre().toLowerCase().contains(queryLower))
                )
                .filter(projet -> statut == null || statut.isEmpty()) // Pas de filtre statut pour l'instant
                .filter(projet -> filtrerParDate(projet.getModifieLe(), dateFiltre, specificDate))
                .map(projet -> new RechercheGlobaleDTO(
                    projet.getId(), "projet", projet.getTitre(), projet.getSynopsis(),
                    "Non défini", projet.getModifieLe(), projet.getCreeLe(),
                    projet.getId(), projet.getTitre(), null, null, null, null,
                    null, null, null
                ))
                .collect(Collectors.toList());
    }

    // CORRIGÉ : Ajout du paramètre specificDate
    private List<RechercheGlobaleDTO> rechercherEpisodes(String query, String statut, String dateFiltre, String specificDate) {
        String queryLower = query.toLowerCase();
        
        return episodeRepository.findAll().stream()
                .filter(episode -> 
                    episode.getTitre().toLowerCase().contains(queryLower) ||
                    (episode.getSynopsis() != null && episode.getSynopsis().toLowerCase().contains(queryLower)) ||
                    (episode.getProjet() != null && episode.getProjet().getTitre().toLowerCase().contains(queryLower))
                )
                .filter(episode -> statut == null || statut.isEmpty() || 
                         (getEpisodeStatutNom(episode.getId()) != null && getEpisodeStatutNom(episode.getId()).equalsIgnoreCase(statut)))
                .filter(episode -> filtrerParDate(episode.getModifieLe(), dateFiltre, specificDate)) // CORRIGÉ
                .map(episode -> new RechercheGlobaleDTO(
                    episode.getId(), "episode", episode.getTitre(), episode.getSynopsis(),
                    getEpisodeStatutNom(episode.getId()), episode.getModifieLe(), episode.getCreeLe(),
                    episode.getProjet().getId(), episode.getProjet().getTitre(),
                    episode.getId(), episode.getTitre(), null, null,
                    episode.getOrdre(), null, null
                ))
                .collect(Collectors.toList());
    }
    
    // CORRIGÉ : Ajout du paramètre specificDate
    private List<RechercheGlobaleDTO> rechercherSequences(String query, String statut, String dateFiltre, String specificDate) {
        String queryLower = query.toLowerCase();
        
        return sequenceRepository.findAll().stream()
                .filter(sequence -> 
                    sequence.getTitre().toLowerCase().contains(queryLower) ||
                    (sequence.getSynopsis() != null && sequence.getSynopsis().toLowerCase().contains(queryLower)) ||
                    (sequence.getEpisode() != null && sequence.getEpisode().getTitre().toLowerCase().contains(queryLower)) ||
                    (sequence.getEpisode().getProjet() != null && sequence.getEpisode().getProjet().getTitre().toLowerCase().contains(queryLower))
                )
                .filter(sequence -> statut == null || statut.isEmpty() || 
                         (getSequenceStatutNom(sequence.getId()) != null && getSequenceStatutNom(sequence.getId()).equalsIgnoreCase(statut)))
                .filter(sequence -> filtrerParDate(sequence.getModifieLe(), dateFiltre, specificDate)) // CORRIGÉ
                .map(sequence -> new RechercheGlobaleDTO(
                    sequence.getId(), "sequence", sequence.getTitre(), sequence.getSynopsis(),
                    getSequenceStatutNom(sequence.getId()), sequence.getModifieLe(), sequence.getCreeLe(),
                    sequence.getEpisode().getProjet().getId(), sequence.getEpisode().getProjet().getTitre(),
                    sequence.getEpisode().getId(), sequence.getEpisode().getTitre(),
                    sequence.getId(), sequence.getTitre(),
                    sequence.getEpisode().getOrdre(), sequence.getOrdre(), null
                ))
                .collect(Collectors.toList());
    }
    
    // CORRIGÉ : Ajout du paramètre specificDate
    private List<RechercheGlobaleDTO> rechercherScenes(String query, String statut, String dateFiltre, String specificDate) {
        String queryLower = query.toLowerCase();
        
        return sceneRepository.findAll().stream()
                .filter(scene -> 
                    scene.getTitre().toLowerCase().contains(queryLower) ||
                    (scene.getSynopsis() != null && scene.getSynopsis().toLowerCase().contains(queryLower)) ||
                    (scene.getSequence() != null && scene.getSequence().getTitre().toLowerCase().contains(queryLower)) ||
                    (scene.getSequence().getEpisode() != null && scene.getSequence().getEpisode().getTitre().toLowerCase().contains(queryLower)) ||
                    (scene.getSequence().getEpisode().getProjet() != null && scene.getSequence().getEpisode().getProjet().getTitre().toLowerCase().contains(queryLower))
                )
                .filter(scene -> statut == null || statut.isEmpty() || 
                         (getSceneStatutNom(scene.getId()) != null && getSceneStatutNom(scene.getId()).equalsIgnoreCase(statut)))
                .filter(scene -> filtrerParDate(scene.getModifieLe(), dateFiltre, specificDate)) // CORRIGÉ
                .map(scene -> new RechercheGlobaleDTO(
                    scene.getId(), "scene", scene.getTitre(), scene.getSynopsis(),
                    getSceneStatutNom(scene.getId()), scene.getModifieLe(), scene.getCreeLe(),
                    scene.getSequence().getEpisode().getProjet().getId(), 
                    scene.getSequence().getEpisode().getProjet().getTitre(),
                    scene.getSequence().getEpisode().getId(), scene.getSequence().getEpisode().getTitre(),
                    scene.getSequence().getId(), scene.getSequence().getTitre(),
                    scene.getSequence().getEpisode().getOrdre(), scene.getSequence().getOrdre(), scene.getOrdre()
                ))
                .collect(Collectors.toList());
    }
    
    // Méthodes pour récupérer les statuts depuis les repositories
    private String getProjetStatutNom(Long projetId) {
        return projetStatutRepository.findLatestStatutByProjetId(projetId)
                .map(statut -> statut.getStatut().getNomStatutsProjet())
                .orElse("Non défini");
    }
    
    private String getEpisodeStatutNom(Long episodeId) {
        return episodeStatutRepository.findLatestStatutByEpisodeId(episodeId)
                .map(statut -> statut.getStatut().getNomStatutsEpisode())
                .orElse("Non défini");
    }
    
    private String getSequenceStatutNom(Long sequenceId) {
        return sequenceStatutRepository.findLatestStatutBySequenceId(sequenceId)
                .map(statut -> statut.getStatut().getNomStatutsSequence())
                .orElse("Non défini");
    }
    
    private String getSceneStatutNom(Long sceneId) {
        return sceneStatutRepository.findLatestStatutBySceneId(sceneId)
                .map(statut -> statut.getStatut().getNomStatutsScene())
                .orElse("Non défini");
    }
    
    private boolean filtrerParDate(LocalDateTime date, String dateFiltre, String specificDate) {
        // PRIORITÉ à la date spécifique
        if (specificDate != null && !specificDate.isEmpty()) {
            try {
                LocalDate selectedDate = LocalDate.parse(specificDate);
                LocalDateTime dateDebut = selectedDate.atStartOfDay();
                LocalDateTime dateFin = selectedDate.atTime(23, 59, 59);
                
                return !date.isBefore(dateDebut) && !date.isAfter(dateFin);
            } catch (Exception e) {
                // En cas d'erreur de parsing, ignorer le filtre de date spécifique
                System.err.println("Erreur de parsing de la date spécifique: " + specificDate);
            }
        }
        
        // Fallback sur les périodes prédéfinies si pas de date spécifique
        if (dateFiltre == null || dateFiltre.isEmpty()) {
            return true;
        }
        
        LocalDateTime maintenant = LocalDateTime.now();
        LocalDateTime dateDebut;
        
        switch (dateFiltre) {
            case "today":
                dateDebut = maintenant.toLocalDate().atStartOfDay();
                break;
            case "this_week":
                dateDebut = maintenant.minusDays(maintenant.getDayOfWeek().getValue() - 1).toLocalDate().atStartOfDay();
                break;
            case "this_month":
                dateDebut = maintenant.withDayOfMonth(1).toLocalDate().atStartOfDay();
                break;
            case "this_year":
                dateDebut = maintenant.withDayOfYear(1).toLocalDate().atStartOfDay();
                break;
            case "recent":
                dateDebut = maintenant.minusDays(7);
                break;
            default:
                return true;
        }
        
        return !date.isBefore(dateDebut);
    }
}
