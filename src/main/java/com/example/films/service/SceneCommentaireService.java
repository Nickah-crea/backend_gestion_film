package com.example.films.service;

import com.example.films.dto.CommentaireDTO;
import com.example.films.dto.CreateSceneCommentaireDTO;
import com.example.films.entity.Commentaire;
import com.example.films.entity.Scene;
import com.example.films.entity.SceneCommentaire;
import com.example.films.entity.Utilisateur;
import com.example.films.repository.CommentaireRepository;
import com.example.films.repository.SceneCommentaireRepository;
import com.example.films.repository.SceneRepository;
import com.example.films.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SceneCommentaireService {
    
    private final CommentaireRepository commentaireRepository;
    private final SceneCommentaireRepository sceneCommentaireRepository;
    private final SceneRepository sceneRepository;
    private final UtilisateurRepository utilisateurRepository;
    
    public SceneCommentaireService(CommentaireRepository commentaireRepository,
                                 SceneCommentaireRepository sceneCommentaireRepository,
                                 SceneRepository sceneRepository,
                                 UtilisateurRepository utilisateurRepository) {
        this.commentaireRepository = commentaireRepository;
        this.sceneCommentaireRepository = sceneCommentaireRepository;
        this.sceneRepository = sceneRepository;
        this.utilisateurRepository = utilisateurRepository;
    }
    
    @Transactional
    public CommentaireDTO createCommentaireForScene(CreateSceneCommentaireDTO createSceneCommentaireDTO, Long utilisateurId) {
        // Créer le commentaire
        Commentaire commentaire = new Commentaire();
        commentaire.setContenu(createSceneCommentaireDTO.getContenu());
        
        // Associer l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        commentaire.setUtilisateur(utilisateur);
        
        // Sauvegarder le commentaire
        Commentaire savedCommentaire = commentaireRepository.save(commentaire);
        
        // Associer le commentaire à la scène
        Scene scene = sceneRepository.findById(createSceneCommentaireDTO.getSceneId())
                .orElseThrow(() -> new RuntimeException("Scène non trouvée"));
        
        SceneCommentaire sceneCommentaire = new SceneCommentaire();
        sceneCommentaire.setScene(scene);
        sceneCommentaire.setCommentaire(savedCommentaire);
        
        sceneCommentaireRepository.save(sceneCommentaire);
        
        return convertToDTO(savedCommentaire);
    }
    
    public List<CommentaireDTO> getCommentairesBySceneId(Long sceneId) {
        List<SceneCommentaire> sceneCommentaires = sceneCommentaireRepository
                .findBySceneIdWithCommentaireAndUtilisateur(sceneId);
        
        return sceneCommentaires.stream()
                .map(sceneCommentaire -> convertToDTO(sceneCommentaire.getCommentaire()))
                .collect(Collectors.toList());
    }
    
    public Integer getNombreCommentairesBySceneId(Long sceneId) {
        return sceneCommentaireRepository.countBySceneId(sceneId);
    }
    
    @Transactional
    public void deleteCommentaire(Long commentaireId) {
        // Supprimer d'abord la liaison scène-commentaire
        List<SceneCommentaire> sceneCommentaires = sceneCommentaireRepository.findByCommentaireId(commentaireId);
        sceneCommentaireRepository.deleteAll(sceneCommentaires);
        
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