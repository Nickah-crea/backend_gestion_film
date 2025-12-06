package com.example.films.repository;

import com.example.films.entity.SceneLieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SceneLieuRepository extends JpaRepository<SceneLieu, Long> {
    
    @Query("SELECT sl FROM SceneLieu sl LEFT JOIN FETCH sl.scene LEFT JOIN FETCH sl.lieu WHERE sl.scene.id = :sceneId")
    List<SceneLieu> findBySceneId(@Param("sceneId") Long sceneId);
    
    @Query("SELECT sl FROM SceneLieu sl LEFT JOIN FETCH sl.scene LEFT JOIN FETCH sl.lieu WHERE sl.lieu.id = :lieuId")
    List<SceneLieu> findByLieuId(@Param("lieuId") Long lieuId);
    
    @Query("SELECT sl FROM SceneLieu sl LEFT JOIN FETCH sl.scene LEFT JOIN FETCH sl.lieu WHERE sl.id = :id")
    Optional<SceneLieu> findByIdWithDetails(@Param("id") Long id);
    
    boolean existsBySceneIdAndLieuId(Long sceneId, Long lieuId);
    
    @Query("SELECT sl FROM SceneLieu sl LEFT JOIN FETCH sl.scene s LEFT JOIN FETCH sl.lieu l WHERE s.sequence.episode.projet.id = :projetId")
    List<SceneLieu> findByProjetId(@Param("projetId") Long projetId);

    boolean existsBySceneIdAndLieuIdAndPlateauId(Long sceneId, Long lieuId, Long plateauId);
}