package com.example.films.repository;

import com.example.films.entity.SceneCommentaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SceneCommentaireRepository extends JpaRepository<SceneCommentaire, Long> {
    
    @Query("SELECT sc FROM SceneCommentaire sc " +
           "JOIN FETCH sc.commentaire c " +
           "JOIN FETCH c.utilisateur " +
           "WHERE sc.scene.id = :sceneId " +
           "ORDER BY c.creeLe DESC")
    List<SceneCommentaire> findBySceneIdWithCommentaireAndUtilisateur(@Param("sceneId") Long sceneId);
    
    @Query("SELECT COUNT(sc) FROM SceneCommentaire sc WHERE sc.scene.id = :sceneId")
    Integer countBySceneId(@Param("sceneId") Long sceneId);
    
    @Query("SELECT sc FROM SceneCommentaire sc WHERE sc.commentaire.id = :commentaireId")
    List<SceneCommentaire> findByCommentaireId(@Param("commentaireId") Long commentaireId);
    
    @Query("SELECT sc FROM SceneCommentaire sc WHERE sc.scene.id = :sceneId AND sc.commentaire.id = :commentaireId")
    Optional<SceneCommentaire> findBySceneIdAndCommentaireId(@Param("sceneId") Long sceneId, @Param("commentaireId") Long commentaireId);
}