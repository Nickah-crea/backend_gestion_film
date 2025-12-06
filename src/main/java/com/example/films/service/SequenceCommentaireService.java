package com.example.films.service;

import com.example.films.dto.CommentaireDTO;
import com.example.films.dto.CreateSequenceCommentaireDTO;
import com.example.films.entity.*;
import com.example.films.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SequenceCommentaireService {
    private final CommentaireRepository commentaireRepository;
    private final SequenceCommentaireRepository sequenceCommentaireRepository;
    private final SequenceRepository sequenceRepository;
    private final UtilisateurRepository utilisateurRepository;

    public SequenceCommentaireService(CommentaireRepository commentaireRepository,
                                    SequenceCommentaireRepository sequenceCommentaireRepository,
                                    SequenceRepository sequenceRepository,
                                    UtilisateurRepository utilisateurRepository) {
        this.commentaireRepository = commentaireRepository;
        this.sequenceCommentaireRepository = sequenceCommentaireRepository;
        this.sequenceRepository = sequenceRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Transactional
    public CommentaireDTO createCommentaireForSequence(CreateSequenceCommentaireDTO createCommentaireDTO, Long utilisateurId) {
        // Créer le commentaire
        Commentaire commentaire = new Commentaire();
        commentaire.setContenu(createCommentaireDTO.getContenu());
        
        // Associer l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        commentaire.setUtilisateur(utilisateur);
        
        // Sauvegarder le commentaire
        Commentaire savedCommentaire = commentaireRepository.save(commentaire);
        
        // Associer le commentaire à la séquence
        Sequence sequence = sequenceRepository.findById(createCommentaireDTO.getSequenceId())
                .orElseThrow(() -> new RuntimeException("Séquence non trouvée"));
        
        SequenceCommentaire sequenceCommentaire = new SequenceCommentaire();
        sequenceCommentaire.setSequence(sequence);
        sequenceCommentaire.setCommentaire(savedCommentaire);
        
        sequenceCommentaireRepository.save(sequenceCommentaire);
        
        return convertToDTO(savedCommentaire);
    }

    public List<CommentaireDTO> getCommentairesBySequenceId(Long sequenceId) {
        List<SequenceCommentaire> sequenceCommentaires = sequenceCommentaireRepository
                .findBySequenceIdWithCommentaireAndUtilisateur(sequenceId);
        
        return sequenceCommentaires.stream()
                .map(sequenceCommentaire -> convertToDTO(sequenceCommentaire.getCommentaire()))
                .collect(Collectors.toList());
    }

    public Integer getNombreCommentairesBySequenceId(Long sequenceId) {
        return sequenceCommentaireRepository.countBySequenceId(sequenceId);
    }

    @Transactional
    public void deleteCommentaire(Long commentaireId) {
        // Supprimer d'abord la liaison séquence-commentaire
        List<SequenceCommentaire> sequenceCommentaires = sequenceCommentaireRepository.findByCommentaireId(commentaireId);
        sequenceCommentaireRepository.deleteAll(sequenceCommentaires);
        
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
}