package com.example.films.service;

import com.example.films.dto.CommentaireDTO;
import com.example.films.dto.CreateDialogueCommentaireDTO;
import com.example.films.entity.Commentaire;
import com.example.films.entity.Dialogue;
import com.example.films.entity.DialogueCommentaire;
import com.example.films.entity.Utilisateur;
import com.example.films.repository.CommentaireRepository;
import com.example.films.repository.DialogueCommentaireRepository;
import com.example.films.repository.DialogueRepository;
import com.example.films.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DialogueCommentaireService {
    
    private final DialogueCommentaireRepository dialogueCommentaireRepository;
    private final CommentaireRepository commentaireRepository;
    private final DialogueRepository dialogueRepository;
    private final UtilisateurRepository utilisateurRepository;
    
    public DialogueCommentaireService(DialogueCommentaireRepository dialogueCommentaireRepository,
                                    CommentaireRepository commentaireRepository,
                                    DialogueRepository dialogueRepository,
                                    UtilisateurRepository utilisateurRepository) {
        this.dialogueCommentaireRepository = dialogueCommentaireRepository;
        this.commentaireRepository = commentaireRepository;
        this.dialogueRepository = dialogueRepository;
        this.utilisateurRepository = utilisateurRepository;
    }
    
    @Transactional
    public CommentaireDTO createCommentaireForDialogue(CreateDialogueCommentaireDTO createDTO, Long utilisateurId) {
        // Créer le commentaire
        Commentaire commentaire = new Commentaire();
        commentaire.setContenu(createDTO.getContenu());
        
        // Associer l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        commentaire.setUtilisateur(utilisateur);
        
        // Sauvegarder le commentaire
        Commentaire savedCommentaire = commentaireRepository.save(commentaire);
        
        // Associer le commentaire au dialogue
        Dialogue dialogue = dialogueRepository.findById(createDTO.getDialogueId())
                .orElseThrow(() -> new RuntimeException("Dialogue non trouvé"));
        
        DialogueCommentaire dialogueCommentaire = new DialogueCommentaire();
        dialogueCommentaire.setDialogue(dialogue);
        dialogueCommentaire.setCommentaire(savedCommentaire);
        
        dialogueCommentaireRepository.save(dialogueCommentaire);
        
        return convertToDTO(savedCommentaire);
    }
    
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
    
    @Transactional
    public void deleteCommentaire(Long commentaireId) {
        // Supprimer d'abord la liaison dialogue-commentaire
        List<DialogueCommentaire> dialogueCommentaires = dialogueCommentaireRepository.findByCommentaireId(commentaireId);
        dialogueCommentaireRepository.deleteAll(dialogueCommentaires);
        
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