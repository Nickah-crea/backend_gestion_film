package com.example.films.repository;

import com.example.films.entity.DialogueSurlignage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DialogueSurlignageRepository extends JpaRepository<DialogueSurlignage, Long> {
    
    List<DialogueSurlignage> findByDialogueId(Long dialogueId);
    
    @Query("SELECT ds FROM DialogueSurlignage ds WHERE ds.dialogue.id = :dialogueId AND ds.utilisateurId = :utilisateurId")
    List<DialogueSurlignage> findByDialogueIdAndUtilisateurId(Long dialogueId, Long utilisateurId);
    
    // Méthode pour supprimer par dialogue et utilisateur
    void deleteByDialogueIdAndUtilisateurId(Long dialogueId, Long utilisateurId);
}

