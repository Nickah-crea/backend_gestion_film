package com.example.films.repository;

import com.example.films.entity.Dialogue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DialogueRepository extends JpaRepository<Dialogue, Long> {
    
    List<Dialogue> findBySceneId(Long sceneId);
    
    List<Dialogue> findByPersonnageId(Long personnageId);
    
    @Query("SELECT d FROM Dialogue d LEFT JOIN FETCH d.scene LEFT JOIN FETCH d.personnage WHERE d.id = :id")
    Optional<Dialogue> findByIdWithDetails(@Param("id") Long id);
    
    @Query("SELECT d FROM Dialogue d LEFT JOIN FETCH d.scene LEFT JOIN FETCH d.personnage")
    List<Dialogue> findAllWithDetails();
    
    @Query("SELECT d FROM Dialogue d LEFT JOIN FETCH d.scene s LEFT JOIN FETCH d.personnage WHERE s.id = :sceneId ORDER BY d.ordre")
    List<Dialogue> findBySceneIdOrderByOrdre(@Param("sceneId") Long sceneId);
    
    @Query("SELECT MAX(d.ordre) FROM Dialogue d WHERE d.scene.id = :sceneId")
    Integer findMaxOrdreBySceneId(@Param("sceneId") Long sceneId);

    @Query("SELECT COUNT(d) FROM Dialogue d WHERE d.scene.sequence.episode.projet.id = :projetId")
    long countBySceneSequenceEpisodeProjetId(@Param("projetId") Long projetId);

    long countByPersonnageId(Long personnageId);
    
}