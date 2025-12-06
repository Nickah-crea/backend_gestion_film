package com.example.films.repository;

import com.example.films.entity.SceneStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SceneStatutRepository extends JpaRepository<SceneStatut, Long> {
    
    @Query("SELECT ss FROM SceneStatut ss WHERE ss.scene.id = :sceneId ORDER BY ss.dateDebut DESC LIMIT 1")
    Optional<SceneStatut> findLatestStatutBySceneId(@Param("sceneId") Long sceneId);
    
    List<SceneStatut> findBySceneId(Long sceneId);
    
    @Query("SELECT ss FROM SceneStatut ss WHERE ss.scene.id = :sceneId AND ss.dateFin IS NULL")
    Optional<SceneStatut> findCurrentStatutBySceneId(@Param("sceneId") Long sceneId);
}