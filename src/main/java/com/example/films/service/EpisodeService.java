package com.example.films.service;

import com.example.films.dto.CreateEpisodeDTO;
import com.example.films.dto.EpisodeDTO;
import com.example.films.dto.RealisateurDTO;
import com.example.films.dto.ScenaristeDTO;
import com.example.films.entity.Episode;
import com.example.films.entity.EpisodeRealisateur;
import com.example.films.entity.EpisodeScenariste;
import com.example.films.entity.EpisodeStatut;
import com.example.films.entity.Projet;
import com.example.films.entity.Realisateur;
import com.example.films.entity.Scenariste;
import com.example.films.entity.StatutEpisode;
import com.example.films.entity.Utilisateur;
import com.example.films.repository.EpisodeRealisateurRepository;
import com.example.films.repository.EpisodeRepository;
import com.example.films.repository.EpisodeScenaristeRepository;
import com.example.films.repository.EpisodeStatutRepository;
import com.example.films.repository.ProjetRepository;
import com.example.films.repository.RealisateurRepository;
import com.example.films.repository.ScenaristeRepository;
import com.example.films.repository.SequenceRepository;
import com.example.films.repository.StatutEpisodeRepository;
import com.example.films.repository.UtilisateurRepository;
import com.example.films.dto.RechercheEpisodeDTO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Comparator;

@Service
public class EpisodeService {
    private final EpisodeRepository episodeRepository;
    private final EpisodeStatutRepository episodeStatutRepository;
    private final ProjetRepository projetRepository;
    private final StatutEpisodeRepository statutEpisodeRepository;
    private final SequenceRepository sequenceRepository;
    private final EpisodeScenaristeRepository episodeScenaristeRepository;
    private final EpisodeRealisateurRepository episodeRealisateurRepository;
    private final ScenaristeRepository scenaristeRepository;
    private final RealisateurRepository realisateurRepository;
    private final UtilisateurRepository utilisateurRepository;


    public EpisodeService(EpisodeRepository episodeRepository,
                         EpisodeStatutRepository episodeStatutRepository,
                         ProjetRepository projetRepository,
                         StatutEpisodeRepository statutEpisodeRepository,
                         SequenceRepository sequenceRepository,
                         EpisodeScenaristeRepository episodeScenaristeRepository,
                         EpisodeRealisateurRepository episodeRealisateurRepository,
                         ScenaristeRepository scenaristeRepository,
                         RealisateurRepository realisateurRepository,
                         UtilisateurRepository utilisateurRepository) {
        this.episodeRepository = episodeRepository;
        this.episodeStatutRepository = episodeStatutRepository;
        this.projetRepository = projetRepository;
        this.statutEpisodeRepository = statutEpisodeRepository;
        this.sequenceRepository = sequenceRepository;
        this.episodeScenaristeRepository = episodeScenaristeRepository;
        this.episodeRealisateurRepository = episodeRealisateurRepository;
        this.scenaristeRepository = scenaristeRepository;
        this.realisateurRepository = realisateurRepository;
        this.utilisateurRepository = utilisateurRepository;
        
    }

   public List<EpisodeDTO> getEpisodesByProjetId(Long projetId) {
        List<Episode> episodes = episodeRepository.findByProjetId(projetId);
        // Pas de throw si vide ; on retourne simplement la liste triée (potentiellement vide)
        List<Episode> sortedEpisodes = episodes.stream()
                .sorted(Comparator.comparingInt(Episode::getOrdre))
                .collect(Collectors.toList());
        return sortedEpisodes.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Nouvelle méthode pour vérifier si un ordre existe déjà
    public boolean orderExists(Long projetId, Integer order) {
        List<Episode> existingEpisodes = episodeRepository.findByProjetId(projetId);
        return existingEpisodes.stream()
                .anyMatch(ep -> ep.getOrdre().equals(order));
    }

    public List<EpisodeDTO> getEpisodesByUtilisateurId(Long utilisateurId) {
        // Récupérer l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        List<Episode> episodes = new ArrayList<>();
        
        if ("REALISATEUR".equals(utilisateur.getRole())) {
            // Récupérer les épisodes où l'utilisateur est réalisateur
            Realisateur realisateur = realisateurRepository.findByUtilisateurId(utilisateurId)
                    .orElseThrow(() -> new RuntimeException("Réalisateur non trouvé"));
            
            List<EpisodeRealisateur> episodeRealisateurs = episodeRealisateurRepository
                    .findByRealisateurId(realisateur.getId());
            
            episodes = episodeRealisateurs.stream()
                    .map(er -> er.getEpisode())
                    .collect(Collectors.toList());
            
        } else if ("SCENARISTE".equals(utilisateur.getRole())) {
            // Récupérer les épisodes où l'utilisateur est scénariste
            Scenariste scenariste = scenaristeRepository.findByUtilisateurId(utilisateurId)
                    .orElseThrow(() -> new RuntimeException("Scénariste non trouvé"));
            
            // CORRECTION : Utiliser le bon repository
            List<EpisodeScenariste> episodeScenaristes = episodeScenaristeRepository.findByScenaristeId(scenariste.getId());
            
            episodes = episodeScenaristes.stream()
                    .map(es -> es.getEpisode())
                    .collect(Collectors.toList());
        } else if ("ADMIN".equals(utilisateur.getRole())) {
            // Les admins voient tous les épisodes
            episodes = episodeRepository.findAll();
        }
        
        return episodes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public EpisodeDTO createEpisode(Long projetId, CreateEpisodeDTO createEpisodeDTO) {
        // Vérifier si l'ordre existe déjà pour ce projet
        List<Episode> existingEpisodes = episodeRepository.findByProjetId(projetId);
        boolean orderExists = existingEpisodes.stream()
                .anyMatch(ep -> ep.getOrdre().equals(createEpisodeDTO.getOrdre()));
        
        if (orderExists) {
            throw new RuntimeException("L'ordre " + createEpisodeDTO.getOrdre() + " existe déjà pour ce projet");
        }
        
        // Créer l'épisode
        Episode episode = new Episode();
        episode.setTitre(createEpisodeDTO.getTitre());
        episode.setOrdre(createEpisodeDTO.getOrdre());
        episode.setSynopsis(createEpisodeDTO.getSynopsis());
        
        // Associer le projet
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));
        episode.setProjet(projet);
        
        // Sauvegarder l'épisode
        Episode savedEpisode = episodeRepository.save(episode);
        
        // Créer le statut de l'épisode
        EpisodeStatut episodeStatut = new EpisodeStatut();
        episodeStatut.setEpisode(savedEpisode);
        
        StatutEpisode statut = statutEpisodeRepository.findById(createEpisodeDTO.getStatutId())
                .orElseThrow(() -> new RuntimeException("Statut non trouvé"));
        episodeStatut.setStatut(statut);
        episodeStatut.setDateDebut(LocalDateTime.now());
        
        episodeStatutRepository.save(episodeStatut);

        // Associer le Realisteur
          if (createEpisodeDTO.getRealisateurId() != null) {
            Realisateur realisateur = realisateurRepository.findById(createEpisodeDTO.getRealisateurId())
                    .orElseThrow(() -> new RuntimeException("Réalisateur non trouvé"));
            
            EpisodeRealisateur episodeRealisateur = new EpisodeRealisateur();
            episodeRealisateur.setEpisode(savedEpisode);
            episodeRealisateur.setRealisateur(realisateur);
            episodeRealisateur.setRoleRealisateur("Réalisateur principal");
            episodeRealisateur.setPourcentageContribution(100);
            
            episodeRealisateurRepository.save(episodeRealisateur);
        }

        // Associer le Scenariste
        if(createEpisodeDTO.getScenaristeId() != null) {
            Scenariste scenariste = scenaristeRepository.findById(createEpisodeDTO.getScenaristeId())
                    .orElseThrow(() -> new RuntimeException("Scénariste non trouvé"));
            
            EpisodeScenariste episodeScenariste = new EpisodeScenariste();
            episodeScenariste.setEpisode(savedEpisode);
            episodeScenariste.setScenariste(scenariste);
            episodeScenariste.setRoleScenariste("Scénariste principal");
            episodeScenariste.setPourcentageContribution(100);
            
            episodeScenaristeRepository.save(episodeScenariste);
        }
        
        // Retourner le DTO
        return convertToDTO(savedEpisode);
    }

    private EpisodeDTO convertToDTO(Episode episode) {
        EpisodeDTO dto = new EpisodeDTO();
        dto.setIdEpisode(episode.getId());
        dto.setProjetId(episode.getProjet().getId());
        dto.setTitre(episode.getTitre());
        dto.setOrdre(episode.getOrdre());
        dto.setSynopsis(episode.getSynopsis());
        dto.setCreeLe(episode.getCreeLe());
        dto.setModifieLe(episode.getModifieLe());
        
        
        // Récupérer le statut le plus récent
        Optional<EpisodeStatut> statutOpt = episodeStatutRepository.findLatestStatutByEpisodeId(episode.getId());
            if (statutOpt.isPresent()) {
                EpisodeStatut statut = statutOpt.get();
                dto.setStatutNom(statut.getStatut().getNomStatutsEpisode());
                
                // Si le statut est "Tourné" (ou autre statut terminal), utiliser date_fin
                if (statut.getDateFin() != null && 
                    ("tourne".equals(statut.getStatut().getCode()) || 
                    "valide".equals(statut.getStatut().getCode()))) {
                    dto.setDateFin(statut.getDateFin());
                }
            } else {
                dto.setStatutNom("Non défini");
            }
            
            dto.setNombreSequences(sequenceRepository.countByEpisodeId(episode.getId()));

            //Récupérer le Réalisateur 
            List<EpisodeRealisateur> realisateurs = episodeRealisateurRepository.findByEpisodeIdWithRealisateur(episode.getId());
                if (!realisateurs.isEmpty()) {
                    EpisodeRealisateur er = realisateurs.get(0);
                    RealisateurDTO realisateurDTO = new RealisateurDTO();
                    realisateurDTO.setIdRealisateur(er.getRealisateur().getId());
                    realisateurDTO.setNom(er.getRealisateur().getUtilisateur().getNom());
                    realisateurDTO.setEmail(er.getRealisateur().getUtilisateur().getEmail());
                    dto.setRealisateur(realisateurDTO);
                }

            // Récupérer le scénariste
            List<EpisodeScenariste> scenaristes = episodeScenaristeRepository.findByEpisodeIdWithScenariste(episode.getId());
            if (!scenaristes.isEmpty()) {
                EpisodeScenariste es = scenaristes.get(0);
                ScenaristeDTO scenaristeDTO = new ScenaristeDTO();
                scenaristeDTO.setIdScenariste(es.getScenariste().getId());
                scenaristeDTO.setNom(es.getScenariste().getUtilisateur().getNom());
                scenaristeDTO.setEmail(es.getScenariste().getUtilisateur().getEmail());
                dto.setScenariste(scenaristeDTO);
            }
            
            return dto;
        }

    @Transactional
    public EpisodeDTO updateEpisode(Long id, CreateEpisodeDTO updateEpisodeDTO) {
        Episode episode = episodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Épisode non trouvé"));
        
        episode.setTitre(updateEpisodeDTO.getTitre());
        episode.setOrdre(updateEpisodeDTO.getOrdre());
        episode.setSynopsis(updateEpisodeDTO.getSynopsis());
        
        Episode updatedEpisode = episodeRepository.save(episode);
        
        // Mettre à jour le statut si nécessaire
        if (updateEpisodeDTO.getStatutId() != null) {
            Optional<EpisodeStatut> currentStatutOpt = episodeStatutRepository.findLatestStatutByEpisodeId(id);
            
            if (currentStatutOpt.isPresent()) {
                EpisodeStatut currentStatut = currentStatutOpt.get();
                // Si le statut a changé, créer une nouvelle entrée
                if (!currentStatut.getStatut().getIdStatutEpisode().equals(updateEpisodeDTO.getStatutId())) {
                    currentStatut.setDateFin(LocalDateTime.now());
                    episodeStatutRepository.save(currentStatut);
                    
                    EpisodeStatut newStatut = new EpisodeStatut();
                    newStatut.setEpisode(episode);
                    StatutEpisode statut = statutEpisodeRepository.findById(updateEpisodeDTO.getStatutId())
                            .orElseThrow(() -> new RuntimeException("Statut non trouvé"));
                    newStatut.setStatut(statut);
                    newStatut.setDateDebut(LocalDateTime.now());
                    episodeStatutRepository.save(newStatut);
                }
            }
        }
        
        return convertToDTO(updatedEpisode);
    }

    public void deleteEpisode(Long id) {
        Episode episode = episodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Épisode non trouvé"));
        
        // Sauvegarder l'ordre et le projet avant suppression
        Integer deletedOrder = episode.getOrdre();
        Long projetId = episode.getProjet().getId();
        
        // Supprimer l'épisode
        episodeRepository.delete(episode);
        
        // Recalculer les ordres des épisodes restants
        recalculerOrdresEpisodes(projetId, deletedOrder);
    }

    private void recalculerOrdresEpisodes(Long projetId, Integer deletedOrder) {
        List<Episode> episodes = episodeRepository.findByProjetId(projetId);
        
        // Trier par ordre actuel
        List<Episode> episodesToUpdate = episodes.stream()
                .filter(ep -> ep.getOrdre() > deletedOrder)
                .sorted(Comparator.comparingInt(Episode::getOrdre))
                .collect(Collectors.toList());
        
        // Mettre à jour les ordres
        int newOrder = deletedOrder;
        for (Episode episode : episodesToUpdate) {
            episode.setOrdre(newOrder);
            episodeRepository.save(episode);
            newOrder++;
        }
    }

    public EpisodeDTO getEpisodeById(Long id) {
        Episode episode = episodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Épisode non trouvé"));
        
        EpisodeDTO dto = convertToDTO(episode);
        
        // Ajoutez le comptage des séquences si nécessaire
        Integer sequenceCount = sequenceRepository.countByEpisodeId(id);
        dto.setNombreSequences(sequenceCount);
        
        return dto;
    }

    // Dans EpisodeService.java
    public List<RechercheEpisodeDTO> rechercherEpisodes(String query) {
        return episodeRepository.rechercherEpisodes(query.toLowerCase());
    }
        
}

