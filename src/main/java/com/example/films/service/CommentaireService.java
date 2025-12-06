package com.example.films.service;

import com.example.films.dto.CommentaireDTO;
import com.example.films.dto.CreateCommentaireDTO;
import com.example.films.entity.Commentaire;
import com.example.films.entity.DialogueCommentaire;
import com.example.films.entity.Episode;
import com.example.films.entity.EpisodeCommentaire;
import com.example.films.entity.Utilisateur;
import com.example.films.repository.CommentaireRepository;
import com.example.films.repository.DialogueCommentaireRepository;
import com.example.films.repository.EpisodeCommentaireRepository;
import com.example.films.repository.EpisodeRepository;
import com.example.films.repository.UtilisateurRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentaireService {
    private final CommentaireRepository commentaireRepository;
    private final EpisodeCommentaireRepository episodeCommentaireRepository;
    private final EpisodeRepository episodeRepository;
    private final UtilisateurRepository utilisateurRepository;

    public CommentaireService(CommentaireRepository commentaireRepository,
                            EpisodeCommentaireRepository episodeCommentaireRepository,
                            EpisodeRepository episodeRepository,
                            UtilisateurRepository utilisateurRepository) {
        this.commentaireRepository = commentaireRepository;
        this.episodeCommentaireRepository = episodeCommentaireRepository;
        this.episodeRepository = episodeRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Transactional
    public CommentaireDTO createCommentaireForEpisode(CreateCommentaireDTO createCommentaireDTO, Long utilisateurId) {
        // Créer le commentaire
        Commentaire commentaire = new Commentaire();
        commentaire.setContenu(createCommentaireDTO.getContenu());
        
        // Associer l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        commentaire.setUtilisateur(utilisateur);
        
        // Sauvegarder le commentaire
        Commentaire savedCommentaire = commentaireRepository.save(commentaire);
        
        // Associer le commentaire à l'épisode
        Episode episode = episodeRepository.findById(createCommentaireDTO.getEpisodeId())
                .orElseThrow(() -> new RuntimeException("Épisode non trouvé"));
        
        EpisodeCommentaire episodeCommentaire = new EpisodeCommentaire();
        episodeCommentaire.setEpisode(episode);
        episodeCommentaire.setCommentaire(savedCommentaire);
        
        episodeCommentaireRepository.save(episodeCommentaire);
        
        return convertToDTO(savedCommentaire);
    }

    public List<CommentaireDTO> getCommentairesByEpisodeId(Long episodeId) {
        List<EpisodeCommentaire> episodeCommentaires = episodeCommentaireRepository
                .findByEpisodeIdWithCommentaireAndUtilisateur(episodeId);
        
        return episodeCommentaires.stream()
                .map(episodeCommentaire -> convertToDTO(episodeCommentaire.getCommentaire()))
                .collect(Collectors.toList());
    }

    public Integer getNombreCommentairesByEpisodeId(Long episodeId) {
        return episodeCommentaireRepository.countByEpisodeId(episodeId);
    }

    @Transactional
    public void deleteCommentaire(Long commentaireId) {
        // Supprimer d'abord la liaison épisode-commentaire
        List<EpisodeCommentaire> episodeCommentaires = episodeCommentaireRepository.findByEpisodeCommentaireId(commentaireId);
        episodeCommentaireRepository.deleteAll(episodeCommentaires);
        
        // Puis supprimer le commentaire
        commentaireRepository.deleteById(commentaireId);
    }

    private CommentaireDTO convertToDTO(Commentaire commentaire) {
        CommentaireDTO dto = new CommentaireDTO();
        dto.setId(commentaire.getId());
        dto.setContenu(commentaire.getContenu());
        dto.setCreeLe(commentaire.getCreeLe());
        dto.setModifieLe(commentaire.getModifieLe());
        
        if (commentaire.getUtilisateur() != null) {
            dto.setUtilisateurId(commentaire.getUtilisateur().getId());
            dto.setUtilisateurNom(commentaire.getUtilisateur().getNom());
            dto.setUtilisateurEmail(commentaire.getUtilisateur().getEmail());
        }
        
        return dto;
    }

    @Autowired
    private DialogueCommentaireRepository dialogueCommentaireRepository;

    public List<CommentaireDTO> getCommentairesByDialogueId(Long dialogueId) {
        List<DialogueCommentaire> dialogueCommentaires = dialogueCommentaireRepository
                .findByDialogueIdWithCommentaireAndUtilisateur(dialogueId);
        
        return dialogueCommentaires.stream()
                .map(dialogueCommentaire -> convertToDTO(dialogueCommentaire.getCommentaire()))
                .collect(Collectors.toList());
    }

    public Integer getNombreCommentairesByDialogueId(Long dialogueId) {
        return dialogueCommentaireRepository.countByDialogueId(dialogueId);
    }
}