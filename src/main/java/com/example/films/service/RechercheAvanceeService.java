// RechercheAvanceeService.java - VERSION SIMPLIFIÉE
package com.example.films.service;

import com.example.films.dto.RechercheAvanceeDTO;
import com.example.films.dto.CritereRechercheDTO;
import com.example.films.repository.*;
import com.example.films.entity.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RechercheAvanceeService {
    
    private final ProjetRepository projetRepository;
    private final SceneTournageRepository sceneTournageRepository;
    private final PersonnageRepository personnageRepository;
    private final LieuRepository lieuRepository;
    private final PlateauRepository plateauRepository;
    private final DialogueRepository dialogueRepository;
    private final EpisodeRepository episodeRepository;
    private final SequenceRepository sequenceRepository;
    private final SceneRepository sceneRepository;
    
    public RechercheAvanceeService(ProjetRepository projetRepository,
                                 SceneTournageRepository sceneTournageRepository,
                                 PersonnageRepository personnageRepository,
                                 LieuRepository lieuRepository,
                                 PlateauRepository plateauRepository,
                                 DialogueRepository dialogueRepository,
                                 EpisodeRepository episodeRepository,
                                 SequenceRepository sequenceRepository,
                                 SceneRepository sceneRepository) {
        this.projetRepository = projetRepository;
        this.sceneTournageRepository = sceneTournageRepository;
        this.personnageRepository = personnageRepository;
        this.lieuRepository = lieuRepository;
        this.plateauRepository = plateauRepository;
        this.dialogueRepository = dialogueRepository;
        this.episodeRepository = episodeRepository;
        this.sequenceRepository = sequenceRepository;
        this.sceneRepository = sceneRepository;
    }
    
    public List<RechercheAvanceeDTO> rechercherAvance(CritereRechercheDTO criteres) {
        System.out.println("Début de la recherche avec critères: " + criteres);
        
        List<RechercheAvanceeDTO> resultats = new ArrayList<>();
        
        try {
            // Vérifier quels types doivent être recherchés
            List<String> typesRecherche = criteres.getTypesRecherche();
            if (typesRecherche == null || typesRecherche.isEmpty()) {
                // Par défaut, rechercher dans tous les types
                typesRecherche = Arrays.asList("scenes", "personnages", "lieux", "plateaux");
            }
            
            System.out.println("Types à rechercher: " + typesRecherche);
            
            for (String type : typesRecherche) {
                switch (type) {
                    case "scenes":
                        System.out.println("Recherche de scènes...");
                        resultats.addAll(rechercherScenes(criteres));
                        break;
                    case "personnages":
                        System.out.println("Recherche de personnages...");
                        resultats.addAll(rechercherPersonnages(criteres));
                        break;
                    case "lieux":
                        System.out.println("Recherche de lieux...");
                        resultats.addAll(rechercherLieux(criteres));
                        break;
                    case "plateaux":
                        System.out.println("Recherche de plateaux...");
                        resultats.addAll(rechercherPlateaux(criteres));
                        break;
                    case "episodes":
                        System.out.println("Recherche d'épisodes...");
                        resultats.addAll(rechercherEpisodes(criteres));
                        break;
                    case "sequences":
                        System.out.println("Recherche de séquences...");
                        resultats.addAll(rechercherSequences(criteres));
                        break;
                }
            }
            
            System.out.println("Nombre de résultats trouvés: " + resultats.size());
            
            // Appliquer le regroupement si demandé
            if (criteres.getRegroupement() != null && !criteres.getRegroupement().isEmpty()) {
                resultats = regrouperResultats(resultats, criteres.getRegroupement());
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche: " + e.getMessage());
            e.printStackTrace();
        }
        
        return resultats;
    }

    // RECHERCHE DES SCÈNES
    private List<RechercheAvanceeDTO> rechercherScenes(CritereRechercheDTO criteres) {
        List<RechercheAvanceeDTO> resultats = new ArrayList<>();
        
        try {
            List<SceneTournage> tournages = sceneTournageRepository.findAll();
            System.out.println("Nombre total de scènes de tournage: " + tournages.size());
            
            for (SceneTournage tournage : tournages) {
                if (filtreSceneTournage(tournage, criteres)) {
                    resultats.add(convertirSceneTournageEnDTO(tournage));
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur recherche scènes: " + e.getMessage());
        }
        
        return resultats;
    }
    
    private boolean filtreSceneTournage(SceneTournage tournage, CritereRechercheDTO criteres) {
        // Vérifier la scène
        if (tournage == null || tournage.getScene() == null) {
            return false;
        }
        
        // Filtrer par projet
        if (criteres.getProjetId() != null) {
            Scene scene = tournage.getScene();
            boolean appartientAuProjet = false;
            
            // Vérifier la hiérarchie : Scène -> Séquence -> Épisode -> Projet
            if (scene.getSequence() != null && 
                scene.getSequence().getEpisode() != null && 
                scene.getSequence().getEpisode().getProjet() != null) {
                
                appartientAuProjet = scene.getSequence().getEpisode().getProjet().getId().equals(criteres.getProjetId());
            }
            
            if (!appartientAuProjet) {
                return false;
            }
        }
        
        // Filtrer par dates
        if (criteres.getDateDebut() != null || criteres.getDateFin() != null) {
            if (tournage.getDateTournage() == null) {
                return false;
            }
            
            LocalDate dateTournage = tournage.getDateTournage();
            
            if (criteres.getDateDebut() != null && dateTournage.isBefore(criteres.getDateDebut())) {
                return false;
            }
            
            if (criteres.getDateFin() != null && dateTournage.isAfter(criteres.getDateFin())) {
                return false;
            }
        }
        
        // Filtrer par statuts
        if (criteres.getStatuts() != null && !criteres.getStatuts().isEmpty()) {
            if (tournage.getStatutTournage() == null || 
                !criteres.getStatuts().contains(tournage.getStatutTournage())) {
                return false;
            }
        }
        
        // Filtrer par terme de recherche
        String termeRecherche = criteres.getTermeRecherche();
        if (termeRecherche != null && !termeRecherche.trim().isEmpty()) {
            String terme = termeRecherche.toLowerCase();
            
            // Vérifier dans le titre
            if (tournage.getScene().getTitre().toLowerCase().contains(terme)) {
                return true;
            }
            
            // Vérifier dans le synopsis
            if (tournage.getScene().getSynopsis() != null && 
                tournage.getScene().getSynopsis().toLowerCase().contains(terme)) {
                return true;
            }
            
            // Vérifier dans les dialogues
            List<Dialogue> dialogues = dialogueRepository.findBySceneId(tournage.getScene().getId());
            for (Dialogue dialogue : dialogues) {
                if (dialogue.getTexte().toLowerCase().contains(terme)) {
                    return true;
                }
            }
            
            // Vérifier dans le nom du lieu
            if (tournage.getLieu() != null && 
                tournage.getLieu().getNomLieu().toLowerCase().contains(terme)) {
                return true;
            }
            
            // Vérifier dans le nom du plateau
            if (tournage.getPlateau() != null && 
                tournage.getPlateau().getNom().toLowerCase().contains(terme)) {
                return true;
            }
            
            // Aucune correspondance trouvée
            return false;
        }
        
        return true;
    }

    // RECHERCHE DES PERSONNAGES
    private List<RechercheAvanceeDTO> rechercherPersonnages(CritereRechercheDTO criteres) {
        List<RechercheAvanceeDTO> resultats = new ArrayList<>();
        
        try {
            List<Personnage> personnages;
            
            if (criteres.getProjetId() != null) {
                personnages = personnageRepository.findByProjetId(criteres.getProjetId());
            } else {
                personnages = personnageRepository.findAll();
            }
            
            System.out.println("Nombre de personnages à filtrer: " + personnages.size());
            
            for (Personnage personnage : personnages) {
                if (filtrePersonnage(personnage, criteres)) {
                    resultats.add(convertirPersonnageEnDTO(personnage));
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur recherche personnages: " + e.getMessage());
        }
        
        return resultats;
    }
    
    private boolean filtrePersonnage(Personnage personnage, CritereRechercheDTO criteres) {
        // Filtrer par terme de recherche
        String termeRecherche = criteres.getTermeRecherche();
        if (termeRecherche != null && !termeRecherche.trim().isEmpty()) {
            String terme = termeRecherche.toLowerCase();
            
            // Vérifier dans le nom
            if (personnage.getNom().toLowerCase().contains(terme)) {
                return true;
            }
            
            // Vérifier dans la description
            if (personnage.getDescription() != null && 
                personnage.getDescription().toLowerCase().contains(terme)) {
                return true;
            }
            
            // Vérifier dans le nom du comédien
            if (personnage.getComedien() != null && 
                personnage.getComedien().getNom().toLowerCase().contains(terme)) {
                return true;
            }
            
            // Vérifier dans les dialogues
            List<Dialogue> dialogues = dialogueRepository.findByPersonnageId(personnage.getId());
            for (Dialogue dialogue : dialogues) {
                if (dialogue.getTexte().toLowerCase().contains(terme)) {
                    return true;
                }
            }
            
            // Aucune correspondance trouvée
            return false;
        }
        
        return true;
    }

    // RECHERCHE DES LIEUX
    private List<RechercheAvanceeDTO> rechercherLieux(CritereRechercheDTO criteres) {
        List<RechercheAvanceeDTO> resultats = new ArrayList<>();
        
        try {
            List<Lieu> lieux;
            
            if (criteres.getProjetId() != null) {
                lieux = lieuRepository.findByProjetId(criteres.getProjetId());
            } else {
                lieux = lieuRepository.findAll();
            }
            
            System.out.println("Nombre de lieux à filtrer: " + lieux.size());
            
            for (Lieu lieu : lieux) {
                if (filtreLieu(lieu, criteres)) {
                    resultats.add(convertirLieuEnDTO(lieu));
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur recherche lieux: " + e.getMessage());
        }
        
        return resultats;
    }
    
    private boolean filtreLieu(Lieu lieu, CritereRechercheDTO criteres) {
        // Filtrer par terme de recherche
        String termeRecherche = criteres.getTermeRecherche();
        if (termeRecherche != null && !termeRecherche.trim().isEmpty()) {
            String terme = termeRecherche.toLowerCase();
            
            // Vérifier dans le nom
            if (lieu.getNomLieu().toLowerCase().contains(terme)) {
                return true;
            }
            
            // Vérifier dans le type
            if (lieu.getTypeLieu().toLowerCase().contains(terme)) {
                return true;
            }
            
            // Vérifier dans l'adresse
            if (lieu.getAdresse() != null && 
                lieu.getAdresse().toLowerCase().contains(terme)) {
                return true;
            }
            
            // Vérifier dans les scènes tournées à ce lieu
            List<SceneTournage> tournages = sceneTournageRepository.findByLieuId(lieu.getId());
            for (SceneTournage tournage : tournages) {
                List<Dialogue> dialogues = dialogueRepository.findBySceneId(tournage.getScene().getId());
                for (Dialogue dialogue : dialogues) {
                    if (dialogue.getTexte().toLowerCase().contains(terme)) {
                        return true;
                    }
                }
            }
            
            // Aucune correspondance trouvée
            return false;
        }
        
        return true;
    }

    // RECHERCHE DES PLATEAUX
    private List<RechercheAvanceeDTO> rechercherPlateaux(CritereRechercheDTO criteres) {
        List<RechercheAvanceeDTO> resultats = new ArrayList<>();
        
        try {
            List<Plateau> plateaux;
            
            if (criteres.getProjetId() != null) {
                // Utiliser une méthode de repository qui filtre par projet
                plateaux = plateauRepository.findAll().stream()
                    .filter(p -> p.getLieu() != null && 
                                 p.getLieu().getProjet() != null && 
                                 p.getLieu().getProjet().getId().equals(criteres.getProjetId()))
                    .collect(Collectors.toList());
            } else {
                plateaux = plateauRepository.findAll();
            }
            
            System.out.println("Nombre de plateaux à filtrer: " + plateaux.size());
            
            for (Plateau plateau : plateaux) {
                if (filtrePlateau(plateau, criteres)) {
                    resultats.add(convertirPlateauEnDTO(plateau));
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur recherche plateaux: " + e.getMessage());
        }
        
        return resultats;
    }
    
    private boolean filtrePlateau(Plateau plateau, CritereRechercheDTO criteres) {
        // Filtrer par terme de recherche
        String termeRecherche = criteres.getTermeRecherche();
        if (termeRecherche != null && !termeRecherche.trim().isEmpty()) {
            String terme = termeRecherche.toLowerCase();
            
            // Vérifier dans le nom
            if (plateau.getNom().toLowerCase().contains(terme)) {
                return true;
            }
            
            // Vérifier dans le type
            if (plateau.getTypePlateau().toLowerCase().contains(terme)) {
                return true;
            }
            
            // Vérifier dans la description
            if (plateau.getDescription() != null && 
                plateau.getDescription().toLowerCase().contains(terme)) {
                return true;
            }
            
            // Vérifier dans le nom du lieu
            if (plateau.getLieu() != null && 
                plateau.getLieu().getNomLieu().toLowerCase().contains(terme)) {
                return true;
            }
            
            // Vérifier dans les scènes tournées sur ce plateau
            List<SceneTournage> tournages = sceneTournageRepository.findByPlateauId(plateau.getId());
            for (SceneTournage tournage : tournages) {
                List<Dialogue> dialogues = dialogueRepository.findBySceneId(tournage.getScene().getId());
                for (Dialogue dialogue : dialogues) {
                    if (dialogue.getTexte().toLowerCase().contains(terme)) {
                        return true;
                    }
                }
            }
            
            // Aucune correspondance trouvée
            return false;
        }
        
        return true;
    }

    // RECHERCHE DES ÉPISODES
    private List<RechercheAvanceeDTO> rechercherEpisodes(CritereRechercheDTO criteres) {
        List<RechercheAvanceeDTO> resultats = new ArrayList<>();
        
        try {
            List<Episode> episodes;
            
            if (criteres.getProjetId() != null) {
                episodes = episodeRepository.findByProjetId(criteres.getProjetId());
            } else {
                episodes = episodeRepository.findAll();
            }
            
            for (Episode episode : episodes) {
                if (filtreEpisode(episode, criteres)) {
                    resultats.add(convertirEpisodeEnDTO(episode));
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur recherche episodes: " + e.getMessage());
        }
        
        return resultats;
    }
    
    private boolean filtreEpisode(Episode episode, CritereRechercheDTO criteres) {
        // Filtrer par terme de recherche
        String termeRecherche = criteres.getTermeRecherche();
        if (termeRecherche != null && !termeRecherche.trim().isEmpty()) {
            String terme = termeRecherche.toLowerCase();
            
            // Vérifier dans le titre
            if (episode.getTitre().toLowerCase().contains(terme)) {
                return true;
            }
            
            // Vérifier dans le synopsis
            if (episode.getSynopsis() != null && 
                episode.getSynopsis().toLowerCase().contains(terme)) {
                return true;
            }
            
            // Récupérer les dialogues de l'épisode
            List<Sequence> sequences = sequenceRepository.findByEpisodeId(episode.getId());
            for (Sequence sequence : sequences) {
                List<Scene> scenes = sceneRepository.findBySequenceId(sequence.getId());
                for (Scene scene : scenes) {
                    List<Dialogue> dialogues = dialogueRepository.findBySceneId(scene.getId());
                    for (Dialogue dialogue : dialogues) {
                        if (dialogue.getTexte().toLowerCase().contains(terme)) {
                            return true;
                        }
                    }
                }
            }
            
            // Aucune correspondance trouvée
            return false;
        }
        
        return true;
    }

    // RECHERCHE DES SÉQUENCES
    private List<RechercheAvanceeDTO> rechercherSequences(CritereRechercheDTO criteres) {
        List<RechercheAvanceeDTO> resultats = new ArrayList<>();
        
        try {
            List<Sequence> sequences;
            
            if (criteres.getEpisodeId() != null) {
                sequences = sequenceRepository.findByEpisodeId(criteres.getEpisodeId());
            } else if (criteres.getProjetId() != null) {
                sequences = sequenceRepository.findByEpisodeProjetId(criteres.getProjetId());
            } else {
                sequences = sequenceRepository.findAll();
            }
            
            for (Sequence sequence : sequences) {
                if (filtreSequence(sequence, criteres)) {
                    resultats.add(convertirSequenceEnDTO(sequence));
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur recherche sequences: " + e.getMessage());
        }
        
        return resultats;
    }
    
    private boolean filtreSequence(Sequence sequence, CritereRechercheDTO criteres) {
        // Filtrer par terme de recherche
        String termeRecherche = criteres.getTermeRecherche();
        if (termeRecherche != null && !termeRecherche.trim().isEmpty()) {
            String terme = termeRecherche.toLowerCase();
            
            // Vérifier dans le titre
            if (sequence.getTitre().toLowerCase().contains(terme)) {
                return true;
            }
            
            // Vérifier dans le synopsis
            if (sequence.getSynopsis() != null && 
                sequence.getSynopsis().toLowerCase().contains(terme)) {
                return true;
            }
            
            // Récupérer les dialogues de la séquence
            List<Scene> scenes = sceneRepository.findBySequenceId(sequence.getId());
            for (Scene scene : scenes) {
                List<Dialogue> dialogues = dialogueRepository.findBySceneId(scene.getId());
                for (Dialogue dialogue : dialogues) {
                    if (dialogue.getTexte().toLowerCase().contains(terme)) {
                        return true;
                    }
                }
            }
            
            // Aucune correspondance trouvée
            return false;
        }
        
        return true;
    }

    // MÉTHODES DE CONVERSION
    private RechercheAvanceeDTO convertirSceneTournageEnDTO(SceneTournage tournage) {
        RechercheAvanceeDTO dto = new RechercheAvanceeDTO();
        dto.setId(tournage.getId());
        dto.setType("scene");
        dto.setTitre(tournage.getScene().getTitre());
        dto.setDescription(tournage.getScene().getSynopsis());
        dto.setDateTournage(tournage.getDateTournage());
        
        if (tournage.getHeureDebut() != null) {
            dto.setHeureDebut(tournage.getHeureDebut().toString());
        }
        if (tournage.getHeureFin() != null) {
            dto.setHeureFin(tournage.getHeureFin().toString());
        }
        
        dto.setStatut(tournage.getStatutTournage());
        
        if (tournage.getLieu() != null) {
            dto.setLieuNom(tournage.getLieu().getNomLieu());
        }
        
        if (tournage.getPlateau() != null) {
            dto.setPlateauNom(tournage.getPlateau().getNom());
        }
        
        // Récupérer les personnages de la scène
        List<Personnage> personnages = personnageRepository.findPersonnagesBySceneId(tournage.getScene().getId());
        if (!personnages.isEmpty()) {
            dto.setPersonnageNom(personnages.get(0).getNom());
            if (personnages.get(0).getComedien() != null) {
                dto.setComedienNom(personnages.get(0).getComedien().getNom());
            }
        }
        
        // Récupérer les dialogues de la scène
        List<Dialogue> dialogues = dialogueRepository.findBySceneId(tournage.getScene().getId());
        List<String> textesDialogues = dialogues.stream()
            .map(Dialogue::getTexte)
            .collect(Collectors.toList());
        dto.setDialogues(textesDialogues);
        
        // Hiérarchie du projet
        if (tournage.getScene().getSequence() != null) {
            Sequence sequence = tournage.getScene().getSequence();
            dto.setSequenceTitre(sequence.getTitre());
            
            if (sequence.getEpisode() != null) {
                Episode episode = sequence.getEpisode();
                dto.setEpisodeTitre(episode.getTitre());
                
                if (episode.getProjet() != null) {
                    dto.setProjetTitre(episode.getProjet().getTitre());
                    dto.setProjetId(episode.getProjet().getId());
                }
            }
        }
        
        dto.setModifieLe(tournage.getModifieLe());
        
        return dto;
    }
    
    private RechercheAvanceeDTO convertirPersonnageEnDTO(Personnage personnage) {
        RechercheAvanceeDTO dto = new RechercheAvanceeDTO();
        dto.setId(personnage.getId());
        dto.setType("personnage");
        dto.setTitre(personnage.getNom());
        dto.setDescription(personnage.getDescription());
        dto.setPersonnageNom(personnage.getNom());
        
        if (personnage.getComedien() != null) {
            dto.setComedienNom(personnage.getComedien().getNom());
        }
        
        if (personnage.getProjet() != null) {
            dto.setProjetTitre(personnage.getProjet().getTitre());
            dto.setProjetId(personnage.getProjet().getId());
        }
        
        // Récupérer tous les dialogues du personnage
        List<Dialogue> dialogues = dialogueRepository.findByPersonnageId(personnage.getId());
        List<String> textesDialogues = dialogues.stream()
            .map(Dialogue::getTexte)
            .limit(10) // Limiter à 10 dialogues pour éviter des résultats trop longs
            .collect(Collectors.toList());
        dto.setDialogues(textesDialogues);
        
        dto.setModifieLe(personnage.getModifieLe());
        
        return dto;
    }
    
    private RechercheAvanceeDTO convertirLieuEnDTO(Lieu lieu) {
        RechercheAvanceeDTO dto = new RechercheAvanceeDTO();
        dto.setId(lieu.getId());
        dto.setType("lieu");
        dto.setTitre(lieu.getNomLieu());
        dto.setDescription(lieu.getTypeLieu() + " - " + (lieu.getAdresse() != null ? lieu.getAdresse() : ""));
        dto.setLieuNom(lieu.getNomLieu());
        
        if (lieu.getProjet() != null) {
            dto.setProjetTitre(lieu.getProjet().getTitre());
            dto.setProjetId(lieu.getProjet().getId());
        }
        
        // Récupérer les dialogues des scènes tournées à ce lieu
        List<SceneTournage> tournages = sceneTournageRepository.findByLieuId(lieu.getId());
        List<String> dialogues = new ArrayList<>();
        for (SceneTournage tournage : tournages) {
            List<Dialogue> sceneDialogues = dialogueRepository.findBySceneId(tournage.getScene().getId());
            dialogues.addAll(sceneDialogues.stream()
                .map(Dialogue::getTexte)
                .limit(3) // Limiter à 3 dialogues par scène
                .collect(Collectors.toList()));
        }
        dto.setDialogues(dialogues.stream().limit(10).collect(Collectors.toList())); // Limiter au total
        
        dto.setModifieLe(lieu.getModifieLe());
        
        return dto;
    }
    
    private RechercheAvanceeDTO convertirPlateauEnDTO(Plateau plateau) {
        RechercheAvanceeDTO dto = new RechercheAvanceeDTO();
        dto.setId(plateau.getId());
        dto.setType("plateau");
        dto.setTitre(plateau.getNom());
        dto.setDescription(plateau.getTypePlateau() + " - " + (plateau.getDescription() != null ? plateau.getDescription() : ""));
        dto.setPlateauNom(plateau.getNom());
        
        if (plateau.getLieu() != null) {
            dto.setLieuNom(plateau.getLieu().getNomLieu());
            if (plateau.getLieu().getProjet() != null) {
                dto.setProjetTitre(plateau.getLieu().getProjet().getTitre());
                dto.setProjetId(plateau.getLieu().getProjet().getId());
            }
        }
        
        // Récupérer les dialogues des scènes tournées sur ce plateau
        List<SceneTournage> tournages = sceneTournageRepository.findByPlateauId(plateau.getId());
        List<String> dialogues = new ArrayList<>();
        for (SceneTournage tournage : tournages) {
            List<Dialogue> sceneDialogues = dialogueRepository.findBySceneId(tournage.getScene().getId());
            dialogues.addAll(sceneDialogues.stream()
                .map(Dialogue::getTexte)
                .limit(3) // Limiter à 3 dialogues par scène
                .collect(Collectors.toList()));
        }
        dto.setDialogues(dialogues.stream().limit(10).collect(Collectors.toList())); // Limiter au total
        
        dto.setModifieLe(plateau.getModifieLe());
        
        return dto;
    }
    
    private RechercheAvanceeDTO convertirEpisodeEnDTO(Episode episode) {
        RechercheAvanceeDTO dto = new RechercheAvanceeDTO();
        dto.setId(episode.getId());
        dto.setType("episode");
        dto.setTitre(episode.getTitre());
        dto.setDescription(episode.getSynopsis());
        dto.setEpisodeTitre(episode.getTitre());
        
        if (episode.getProjet() != null) {
            dto.setProjetTitre(episode.getProjet().getTitre());
            dto.setProjetId(episode.getProjet().getId());
        }
        
        // Récupérer des dialogues de l'épisode
        List<String> dialogues = recupererDialoguesEpisode(episode.getId());
        dto.setDialogues(dialogues.stream().limit(10).collect(Collectors.toList())); // Limiter
        
        dto.setModifieLe(episode.getModifieLe());
        
        return dto;
    }
    
    private RechercheAvanceeDTO convertirSequenceEnDTO(Sequence sequence) {
        RechercheAvanceeDTO dto = new RechercheAvanceeDTO();
        dto.setId(sequence.getId());
        dto.setType("sequence");
        dto.setTitre(sequence.getTitre());
        dto.setDescription(sequence.getSynopsis());
        dto.setSequenceTitre(sequence.getTitre());
        
        if (sequence.getEpisode() != null) {
            dto.setEpisodeTitre(sequence.getEpisode().getTitre());
            dto.setEpisodeId(sequence.getEpisode().getId());
            
            if (sequence.getEpisode().getProjet() != null) {
                dto.setProjetTitre(sequence.getEpisode().getProjet().getTitre());
                dto.setProjetId(sequence.getEpisode().getProjet().getId());
            }
        }
        
        // Récupérer des dialogues de la séquence
        List<String> dialogues = recupererDialoguesSequence(sequence.getId());
        dto.setDialogues(dialogues.stream().limit(10).collect(Collectors.toList())); // Limiter
        
        dto.setModifieLe(sequence.getModifieLe());
        
        return dto;
    }

    // MÉTHODES UTILITAIRES
    private List<String> recupererDialoguesEpisode(Long episodeId) {
        List<String> dialogues = new ArrayList<>();
        
        try {
            List<Sequence> sequences = sequenceRepository.findByEpisodeId(episodeId);
            for (Sequence sequence : sequences) {
                dialogues.addAll(recupererDialoguesSequence(sequence.getId()));
            }
        } catch (Exception e) {
            System.err.println("Erreur récupération dialogues épisode: " + e.getMessage());
        }
        
        return dialogues;
    }
    
    private List<String> recupererDialoguesSequence(Long sequenceId) {
        List<String> dialogues = new ArrayList<>();
        
        try {
            List<Scene> scenes = sceneRepository.findBySequenceId(sequenceId);
            for (Scene scene : scenes) {
                List<Dialogue> sceneDialogues = dialogueRepository.findBySceneId(scene.getId());
                dialogues.addAll(sceneDialogues.stream()
                    .map(Dialogue::getTexte)
                    .limit(3) // Limiter à 3 dialogues par scène
                    .collect(Collectors.toList()));
            }
        } catch (Exception e) {
            System.err.println("Erreur récupération dialogues séquence: " + e.getMessage());
        }
        
        return dialogues;
    }
    
    private List<RechercheAvanceeDTO> regrouperResultats(List<RechercheAvanceeDTO> resultats, String regroupement) {
        Map<String, List<RechercheAvanceeDTO>> groupes = new HashMap<>();
        
        for (RechercheAvanceeDTO resultat : resultats) {
            String cleGroupe = obtenirCleGroupe(resultat, regroupement);
            groupes.computeIfAbsent(cleGroupe, k -> new ArrayList<>()).add(resultat);
        }
        
        List<RechercheAvanceeDTO> resultatsGroupes = new ArrayList<>();
        for (Map.Entry<String, List<RechercheAvanceeDTO>> entry : groupes.entrySet()) {
            RechercheAvanceeDTO enTeteGroupe = new RechercheAvanceeDTO();
            enTeteGroupe.setType("groupe");
            enTeteGroupe.setGroupeKey(regroupement);
            enTeteGroupe.setGroupeValeur(entry.getKey());
            enTeteGroupe.setTitre("Groupe: " + entry.getKey());
            resultatsGroupes.add(enTeteGroupe);
            
            resultatsGroupes.addAll(entry.getValue());
        }
        
        return resultatsGroupes;
    }
    
    private String obtenirCleGroupe(RechercheAvanceeDTO resultat, String regroupement) {
        switch (regroupement.toLowerCase()) {
            case "plateau":
                return resultat.getPlateauNom() != null ? resultat.getPlateauNom() : "Non spécifié";
            case "lieu":
                return resultat.getLieuNom() != null ? resultat.getLieuNom() : "Non spécifié";
            case "personnage":
                return resultat.getPersonnageNom() != null ? resultat.getPersonnageNom() : "Non spécifié";
            case "statut":
                return resultat.getStatut() != null ? resultat.getStatut() : "Non spécifié";
            case "episode":
                return resultat.getEpisodeTitre() != null ? resultat.getEpisodeTitre() : "Non spécifié";
            case "sequence":
                return resultat.getSequenceTitre() != null ? resultat.getSequenceTitre() : "Non spécifié";
            default:
                return "Autre";
        }
    }
    
    public RechercheAvanceeDTO getDetailsParTypeEtId(String type, Long id) {
        if (type == null || id == null) {
            throw new IllegalArgumentException("Type et ID ne peuvent pas être null");
        }
        
        switch (type.toLowerCase()) {
            case "personnage":
                Personnage personnage = personnageRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Personnage non trouvé avec l'ID: " + id));
                return convertirPersonnageEnDTO(personnage);
            case "scene":
                SceneTournage tournage = sceneTournageRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Scène de tournage non trouvée avec l'ID: " + id));
                return convertirSceneTournageEnDTO(tournage);
            case "lieu":
                Lieu lieu = lieuRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Lieu non trouvé avec l'ID: " + id));
                return convertirLieuEnDTO(lieu);
            case "plateau":
                Plateau plateau = plateauRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Plateau non trouvé avec l'ID: " + id));
                return convertirPlateauEnDTO(plateau);
            case "episode":
                Episode episode = episodeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Épisode non trouvé avec l'ID: " + id));
                return convertirEpisodeEnDTO(episode);
            case "sequence":
                Sequence sequence = sequenceRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Séquence non trouvée avec l'ID: " + id));
                return convertirSequenceEnDTO(sequence);
            default:
                throw new RuntimeException("Type non supporté: " + type);
        }
    }
}

