package com.example.films.service;

import com.example.films.dto.CreateProjetDTO;
import com.example.films.dto.ProjetDTO;
import com.example.films.entity.Genre;
import com.example.films.entity.Projet;
import com.example.films.entity.ProjetStatut;
import com.example.films.entity.StatutProjet;
import com.example.films.repository.GenreRepository;
import com.example.films.repository.ProjetRepository;
import com.example.films.repository.ProjetStatutRepository;
import com.example.films.repository.StatutProjetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjetService {
    private final ProjetRepository projetRepository;
    private final ProjetStatutRepository projetStatutRepository;
    private final GenreRepository genreRepository;
    private final StatutProjetRepository statutProjetRepository;

    public ProjetService(ProjetRepository projetRepository, 
                        ProjetStatutRepository projetStatutRepository,
                        GenreRepository genreRepository,
                        StatutProjetRepository statutProjetRepository) {
        this.projetRepository = projetRepository;
        this.projetStatutRepository = projetStatutRepository;
        this.genreRepository = genreRepository;
        this.statutProjetRepository = statutProjetRepository;
    }

    public List<ProjetDTO> getAllProjetsWithDetails() {
        List<Projet> projets = projetRepository.findAllWithGenre();
        
        return projets.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public ProjetDTO createProjet(CreateProjetDTO createProjetDTO) {
        // Créer le projet
        Projet projet = new Projet();
        projet.setTitre(createProjetDTO.getTitre());
        projet.setSynopsis(createProjetDTO.getSynopsis());
        projet.setDateDebut(createProjetDTO.getDateDebut());
        projet.setDateFin(createProjetDTO.getDateFin());
        
        // Associer le genre
        Genre genre = genreRepository.findById(createProjetDTO.getGenreId())
                .orElseThrow(() -> new RuntimeException("Genre non trouvé"));
        projet.setGenre(genre);
        
        // Sauvegarder le projet
        Projet savedProjet = projetRepository.save(projet);
        
        // Créer le statut du projet
        ProjetStatut projetStatut = new ProjetStatut();
        projetStatut.setProjet(savedProjet);
        
        StatutProjet statut = statutProjetRepository.findById(createProjetDTO.getStatutId())
                .orElseThrow(() -> new RuntimeException("Statut non trouvé"));
        projetStatut.setStatut(statut);
        projetStatut.setDateDebut(LocalDateTime.now());
        
        projetStatutRepository.save(projetStatut);
        
        // Retourner le DTO
        return convertToDTO(savedProjet);
    }

    private ProjetDTO convertToDTO(Projet projet) {
        ProjetDTO dto = new ProjetDTO();
        dto.setId(projet.getId());
        dto.setTitre(projet.getTitre());
        dto.setSynopsis(projet.getSynopsis());
        dto.setDateDebut(projet.getDateDebut());
        dto.setDateFin(projet.getDateFin());
        dto.setCreeLe(projet.getCreeLe());
        dto.setModifieLe(projet.getModifieLe());
        
        if (projet.getGenre() != null) {
            dto.setGenreId(projet.getGenre().getIdGenre());
            dto.setGenreNom(projet.getGenre().getNomGenre());
        }
        
        // Récupérer le statut le plus récent
        Optional<ProjetStatut> statutOpt = projetStatutRepository.findLatestStatutByProjetId(projet.getId());
            if (statutOpt.isPresent()) {
                dto.setStatutNom(statutOpt.get().getStatut().getNomStatutsProjet());
            } else {
                dto.setStatutNom("Non défini");
            }
            
            return dto;
    }
    
    // Méthode pour mettre à jour un projet
    @Transactional
    public ProjetDTO updateProjet(Long id, CreateProjetDTO updateProjetDTO) {
        Projet projet = projetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));
        
        projet.setTitre(updateProjetDTO.getTitre());
        projet.setSynopsis(updateProjetDTO.getSynopsis());
        projet.setDateDebut(updateProjetDTO.getDateDebut());
        projet.setDateFin(updateProjetDTO.getDateFin());
        
        if (updateProjetDTO.getGenreId() != null) {
            Genre genre = genreRepository.findById(updateProjetDTO.getGenreId())
                    .orElseThrow(() -> new RuntimeException("Genre non trouvé"));
            projet.setGenre(genre);
        }
        
        Projet updatedProjet = projetRepository.save(projet);
        
        // Mettre à jour le statut si nécessaire
        if (updateProjetDTO.getStatutId() != null) {
            Optional<ProjetStatut> currentStatutOpt = projetStatutRepository.findLatestStatutByProjetId(id);
            
            if (currentStatutOpt.isPresent()) {
                ProjetStatut currentStatut = currentStatutOpt.get();
                // Si le statut a changé, créer une nouvelle entrée
                if (!currentStatut.getStatut().getIdStatutProjet().equals(updateProjetDTO.getStatutId())) {
                    currentStatut.setDateFin(LocalDateTime.now());
                    projetStatutRepository.save(currentStatut);
                    
                    ProjetStatut newStatut = new ProjetStatut();
                    newStatut.setProjet(projet);
                    StatutProjet statut = statutProjetRepository.findById(updateProjetDTO.getStatutId())
                            .orElseThrow(() -> new RuntimeException("Statut non trouvé"));
                    newStatut.setStatut(statut);
                    newStatut.setDateDebut(LocalDateTime.now());
                    projetStatutRepository.save(newStatut);
                }
            }
        }
        
        return convertToDTO(updatedProjet);
    }

    // Méthode pour supprimer un projet
    @Transactional
    public void deleteProjet(Long id) {
        Projet projet = projetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));
        projetRepository.delete(projet);
    }

    // Méthode pour récupérer un projet par ID
    public ProjetDTO getProjetById(Long id) {
        Projet projet = projetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));
        return convertToDTO(projet);
    }
}