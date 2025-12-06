package com.example.films.service;

import com.example.films.dto.CreateSceneTournageDTO;
import com.example.films.dto.SceneTournageDTO;
import com.example.films.entity.*;
import com.example.films.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class SceneTournageService {
    private final SceneTournageRepository sceneTournageRepository;
    private final SceneRepository sceneRepository;
    private final LieuRepository lieuRepository;
    private final PlateauRepository plateauRepository;
    private final ComedienRepository comedienRepository;
    private final SceneStatutRepository sceneStatutRepository;
    private final StatutSceneRepository statutSceneRepository;
    private final ConflictVerificationService conflictVerificationService;
    private final PersonnageRepository personnageRepository;


    public SceneTournageService(SceneTournageRepository sceneTournageRepository,
                              SceneRepository sceneRepository,
                              LieuRepository lieuRepository,
                              PlateauRepository plateauRepository,
                              ComedienRepository comedienRepository,
                              SceneStatutRepository sceneStatutRepository,
                               StatutSceneRepository statutSceneRepository,
                               ConflictVerificationService conflictVerificationService,
                               PersonnageRepository personnageRepository) {
        this.sceneTournageRepository = sceneTournageRepository;
        this.sceneRepository = sceneRepository;
        this.lieuRepository = lieuRepository;
        this.plateauRepository = plateauRepository;
        this.comedienRepository = comedienRepository;
        this.sceneStatutRepository = sceneStatutRepository;
        this.statutSceneRepository = statutSceneRepository;
        this.conflictVerificationService = conflictVerificationService;
        this.personnageRepository = personnageRepository;
    }

    public List<SceneTournageDTO> getTournagesByDate(LocalDate date) {
        List<SceneTournage> tournages = sceneTournageRepository.findByDateTournage(date);
        return tournages.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<SceneTournageDTO> getTournagesByDateRange(LocalDate startDate, LocalDate endDate) {
        List<SceneTournage> tournages = sceneTournageRepository.findByDateTournageBetween(startDate, endDate);
        return tournages.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<SceneTournageDTO> getTournagesByStatut(String statut) {
        List<SceneTournage> tournages = sceneTournageRepository.findByStatutTournage(statut);
        return tournages.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<SceneTournageDTO> getTournagesByProjet(Long projetId) {
        List<SceneTournage> tournages = sceneTournageRepository.findByProjetId(projetId);
        return tournages.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

 
    public SceneTournageDTO getTournageBySceneId(Long sceneId) {
        SceneTournage tournage = sceneTournageRepository.findBySceneId(sceneId)
                .orElse(null);
        return tournage != null ? convertToDTO(tournage) : null;
    }

    public SceneTournageDTO getTournageById(Long id) {
        SceneTournage tournage = sceneTournageRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Tournage non trouvé"));
        return convertToDTO(tournage);
    }

    private void synchroniserStatutScene(Long sceneId, String statutTournage) {
            try {
                String nouveauStatutScene = convertirStatutTournageVersScene(statutTournage);
                
                if (nouveauStatutScene != null) {
                    mettreAJourStatutScene(sceneId, nouveauStatutScene);
                    System.out.println("Statut de la scène " + sceneId + " synchronisé vers: " + nouveauStatutScene);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la synchronisation du statut de la scène: " + e.getMessage());
            }
        }

    private String convertirStatutTournageVersScene(String statutTournage) {
            switch (statutTournage) {
                case "planifie":
                    return "planifiee";
                case "en_cours":
                    return "tournage";
                case "termine":
                    return "tournee";
                case "confirme":
                    return "preparee"; // ou garder le statut actuel
                case "reporte":
                    return "a_retourner"; // ou garder le statut actuel
                default:
                    return null; // Pas de synchronisation pour les autres statuts
            }
    }

    @Transactional
    public SceneTournageDTO planifierTournage(CreateSceneTournageDTO createDTO) {
            ConflictVerificationService.ConflictVerificationResult conflits = 
            conflictVerificationService.verifierConflitsComediens(
                createDTO.getSceneId(), 
                createDTO.getDateTournage(), 
                createDTO.getHeureDebut(), 
                createDTO.getHeureFin()
            );
        
        if (conflits.isHasConflicts()) {
            String message = "Conflits détectés pour les comédiens:\n" + 
                String.join("\n", conflits.getConflicts());
            throw new RuntimeException(message);
        }
            
        // Vérifier si la scène est déjà planifiée
        if (sceneTournageRepository.existsBySceneId(createDTO.getSceneId())) {
            throw new RuntimeException("Cette scène est déjà planifiée");
        }

        // Vérifier les conflits de dates pour les comédiens
       // verifierConflitsComediens(createDTO);

        SceneTournage tournage = new SceneTournage();
        
        Scene scene = sceneRepository.findByIdWithDetails(createDTO.getSceneId())
                .orElseThrow(() -> new RuntimeException("Scène non trouvée"));
        tournage.setScene(scene);
        
        tournage.setDateTournage(createDTO.getDateTournage());
        tournage.setHeureDebut(createDTO.getHeureDebut());
        tournage.setHeureFin(createDTO.getHeureFin());
        tournage.setStatutTournage("planifie");
        tournage.setNotes(createDTO.getNotes());

        if (createDTO.getLieuId() != null) {
            Lieu lieu = lieuRepository.findById(createDTO.getLieuId())
                    .orElseThrow(() -> new RuntimeException("Lieu non trouvé"));
            tournage.setLieu(lieu);
        }

        if (createDTO.getPlateauId() != null) {
            Plateau plateau = plateauRepository.findById(createDTO.getPlateauId())
                    .orElseThrow(() -> new RuntimeException("Plateau non trouvé"));
            tournage.setPlateau(plateau);
        }

        SceneTournage saved = sceneTournageRepository.save(tournage);
        
        // SYNCHRONISATION AUTOMATIQUE
        synchroniserStatutScene(saved.getScene().getId(), saved.getStatutTournage());
        
        return convertToDTO(saved);
    }

    @Transactional
    public SceneTournageDTO modifierStatut(Long id, String nouveauStatut) {
        SceneTournage tournage = sceneTournageRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Tournage non trouvé"));

        // Empêcher la modification d'un tournage terminé
        if ("termine".equals(tournage.getStatutTournage()) && !"termine".equals(nouveauStatut)) {
            throw new RuntimeException("Impossible de modifier le statut d'un tournage terminé");
        }

        String ancienStatut = tournage.getStatutTournage();
        tournage.setStatutTournage(nouveauStatut);
        SceneTournage updated = sceneTournageRepository.save(tournage);
        
        // SYNCHRONISATION AUTOMATIQUE
        synchroniserStatutScene(updated.getScene().getId(), nouveauStatut);
        
        return convertToDTO(updated);
    }



    @Transactional
    public SceneTournageDTO modifierTournage(Long id, CreateSceneTournageDTO updateDTO) {
        SceneTournage tournage = sceneTournageRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Tournage non trouvé"));

        // Vérifier les conflits seulement si la date/heure change
        if (!tournage.getDateTournage().equals(updateDTO.getDateTournage()) ||
            !tournage.getHeureDebut().equals(updateDTO.getHeureDebut()) ||
            !tournage.getHeureFin().equals(updateDTO.getHeureFin())) {
            
            ConflictVerificationService.ConflictVerificationResult conflits = 
                conflictVerificationService.verifierConflitsComediens(
                    tournage.getScene().getId(), 
                    updateDTO.getDateTournage(), 
                    updateDTO.getHeureDebut(), 
                    updateDTO.getHeureFin()
                );
            
            if (conflits.isHasConflicts()) {
                String message = "Conflits détectés pour les comédiens:\n" + 
                    String.join("\n", conflits.getConflicts());
                throw new RuntimeException(message);
            }
        }

        // // Empêcher la modification d'un tournage terminé
        // if ("termine".equals(tournage.getStatutTournage()) && 
        //     (updateDTO.getStatutTournage() != null && !"termine".equals(updateDTO.getStatutTournage()))) {
        //     throw new RuntimeException("Impossible de modifier un tournage terminé");
        // }

        String ancienStatut = tournage.getStatutTournage();
        
        // Mise à jour de tous les champs
        tournage.setDateTournage(updateDTO.getDateTournage());
        tournage.setHeureDebut(updateDTO.getHeureDebut());
        tournage.setHeureFin(updateDTO.getHeureFin());
        tournage.setNotes(updateDTO.getNotes());
        
        if (updateDTO.getStatutTournage() != null) {
            tournage.setStatutTournage(updateDTO.getStatutTournage());
        }

        // Mise à jour du lieu et plateau
        if (updateDTO.getLieuId() != null) {
            Lieu lieu = lieuRepository.findById(updateDTO.getLieuId())
                    .orElseThrow(() -> new RuntimeException("Lieu non trouvé"));
            tournage.setLieu(lieu);
        }

        if (updateDTO.getPlateauId() != null) {
            Plateau plateau = plateauRepository.findById(updateDTO.getPlateauId())
                    .orElseThrow(() -> new RuntimeException("Plateau non trouvé"));
            tournage.setPlateau(plateau);
        }

        SceneTournage updated = sceneTournageRepository.save(tournage);
        
        // SYNCHRONISATION AUTOMATIQUE si le statut a changé
        if (updateDTO.getStatutTournage() != null && !updateDTO.getStatutTournage().equals(ancienStatut)) {
            synchroniserStatutScene(updated.getScene().getId(), updated.getStatutTournage());
        }

        return convertToDTO(updated);
    }
    @Transactional
    public void supprimerTournage(Long id) {
        SceneTournage tournage = sceneTournageRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Tournage non trouvé"));
        
        // Empêcher la suppression d'un tournage terminé
        // if ("termine".equals(tournage.getStatutTournage())) {
        //     throw new RuntimeException("Impossible de supprimer un tournage terminé");
        // }
        
        sceneTournageRepository.delete(tournage);
    }

    public List<SceneTournageDTO> getTournagesAConfirmer() {
        LocalDate dateLimite = LocalDate.now().plusDays(2); // 2 jours à l'avance
        List<SceneTournage> tournages = sceneTournageRepository.findTournagesAConfirmer(dateLimite);
        return tournages.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // private void verifierConflitsComediens(CreateSceneTournageDTO dto) {
    //     // Implémentation de la vérification des conflits pour les comédiens
    //     // Cette méthode vérifierait si les comédiens de la scène ont d'autres tournages aux mêmes dates/heures
    // }

    // Méthode pour obtenir les comédiens d'une scène (via les personnages)
    public List<Comedien> getComediensBySceneId(Long sceneId) {
        List<Personnage> personnages = personnageRepository.findPersonnagesBySceneId(sceneId);
        return personnages.stream()
                .filter(p -> p.getComedien() != null)
                .map(Personnage::getComedien)
                .distinct()
                .collect(Collectors.toList());
    }

    private SceneTournageDTO convertToDTO(SceneTournage tournage) {
        SceneTournageDTO dto = new SceneTournageDTO();
        dto.setId(tournage.getId());
        dto.setSceneId(tournage.getScene().getId());
        dto.setSceneTitre(tournage.getScene().getTitre());
        dto.setSequenceId(tournage.getScene().getSequence().getId());
        dto.setSequenceTitre(tournage.getScene().getSequence().getTitre());
        dto.setEpisodeId(tournage.getScene().getSequence().getEpisode().getId());
        dto.setEpisodeTitre(tournage.getScene().getSequence().getEpisode().getTitre());
        dto.setProjetId(tournage.getScene().getSequence().getEpisode().getProjet().getId());
        dto.setProjetTitre(tournage.getScene().getSequence().getEpisode().getProjet().getTitre());
        dto.setDateTournage(tournage.getDateTournage());
        dto.setHeureDebut(tournage.getHeureDebut());
        dto.setHeureFin(tournage.getHeureFin());
        dto.setStatutTournage(tournage.getStatutTournage());
        dto.setNotes(tournage.getNotes());
        dto.setCreeLe(tournage.getCreeLe());
        dto.setModifieLe(tournage.getModifieLe());

        
        dto.setStatutTournageLibelle(getLibelleStatut(tournage.getStatutTournage()));

        if (tournage.getLieu() != null) {
            dto.setLieuId(tournage.getLieu().getId());
            dto.setLieuNom(tournage.getLieu().getNomLieu());
        }

        if (tournage.getPlateau() != null) {
            dto.setPlateauId(tournage.getPlateau().getId());
            dto.setPlateauNom(tournage.getPlateau().getNom());
        }

        
        List<Comedien> comediens = getComediensBySceneId(tournage.getScene().getId());
        dto.setNbComediens(comediens.size());
        dto.setNomsComediens(comediens.stream()
                .map(Comedien::getNom)
                .collect(Collectors.joining(", ")));
        
        // Ajouter les détails des personnages et comédiens
        List<String> detailsPersonnages = new ArrayList<>();
        List<Personnage> personnages = personnageRepository.findPersonnagesBySceneId(tournage.getScene().getId());
        for (Personnage personnage : personnages) {
            String detail = personnage.getNom();
            if (personnage.getComedien() != null) {
                detail += " (" + personnage.getComedien().getNom() + ")";
            }
            detailsPersonnages.add(detail);
        }
        dto.setDetailsPersonnages(detailsPersonnages);

        return dto;
    }

    private String getLibelleStatut(String statut) {
        switch (statut) {
            case "planifie": return "Planifié";
            case "confirme": return "Confirmé";
            case "en_cours": return "En cours";
            case "termine": return "Terminé";
            case "reporte": return "Reporté";
            default: return statut;
        }
    }

    

    public void mettreAJourStatutScene(Long sceneId, String nouveauStatutCode) {
        try {
            // Trouver le statut correspondant
            StatutScene statutScene = statutSceneRepository.findByCode(nouveauStatutCode)
                    .orElseThrow(() -> new RuntimeException("Statut de scène non trouvé: " + nouveauStatutCode));
            
            // Trouver le statut actuel de la scène
            SceneStatut statutActuel = sceneStatutRepository.findLatestStatutBySceneId(sceneId)
                    .orElse(null);
            
            // Vérifier si le statut a changé
            if (statutActuel == null || !statutActuel.getStatut().getCode().equals(nouveauStatutCode)) {
                // Clôturer le statut actuel s'il existe
                if (statutActuel != null) {
                    statutActuel.setDateFin(java.time.LocalDateTime.now());
                    sceneStatutRepository.save(statutActuel);
                }
                
                // Créer un nouveau statut
                SceneStatut nouveauStatut = new SceneStatut();
                nouveauStatut.setScene(sceneRepository.findById(sceneId)
                        .orElseThrow(() -> new RuntimeException("Scène non trouvée")));
                nouveauStatut.setStatut(statutScene);
                nouveauStatut.setDateDebut(java.time.LocalDateTime.now());
                
                sceneStatutRepository.save(nouveauStatut);
                
                System.out.println("Statut de la scène " + sceneId + " mis à jour vers: " + nouveauStatutCode);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du statut de la scène: " + e.getMessage());
            throw new RuntimeException("Erreur de synchronisation du statut de la scène");
        }
    }

    public String getCurrentStatutForScene(Long sceneId) {
        try {
            SceneStatut statutActuel = sceneStatutRepository.findLatestStatutBySceneId(sceneId)
                    .orElse(null);
            return statutActuel != null ? statutActuel.getStatut().getCode() : "non_defini";
        } catch (Exception e) {
            return "erreur";
        }
    }

    public List<SceneTournageDTO> getTournagesBySceneId(Long sceneId) {
        // Utiliser findBySceneId qui retourne Optional et le convertir en List
        Optional<SceneTournage> tournageOpt = sceneTournageRepository.findBySceneId(sceneId);
        List<SceneTournage> tournages = tournageOpt.map(List::of).orElse(List.of());
        
        return tournages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}