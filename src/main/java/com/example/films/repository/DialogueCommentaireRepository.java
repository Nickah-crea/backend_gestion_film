package com.example.films.repository;

import com.example.films.entity.DialogueCommentaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DialogueCommentaireRepository extends JpaRepository<DialogueCommentaire, Long> {
    
    List<DialogueCommentaire> findByDialogueId(Long dialogueId);
    
    Optional<DialogueCommentaire> findByDialogueIdAndCommentaireId(Long dialogueId, Long commentaireId);
    
    @Query("SELECT dc FROM DialogueCommentaire dc " +
           "JOIN FETCH dc.commentaire c " +
           "JOIN FETCH c.utilisateur " +
           "WHERE dc.dialogue.id = :dialogueId " +
           "ORDER BY c.creeLe DESC")
    List<DialogueCommentaire> findByDialogueIdWithCommentaireAndUtilisateur(@Param("dialogueId") Long dialogueId);
    
    @Query("SELECT COUNT(dc) FROM DialogueCommentaire dc WHERE dc.dialogue.id = :dialogueId")
    Integer countByDialogueId(@Param("dialogueId") Long dialogueId);
    
    @Query("SELECT dc FROM DialogueCommentaire dc WHERE dc.commentaire.id = :commentaireId")
    List<DialogueCommentaire> findByCommentaireId(@Param("commentaireId") Long commentaireId);
}