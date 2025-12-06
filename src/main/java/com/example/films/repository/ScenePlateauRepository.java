package com.example.films.repository;

import com.example.films.entity.ScenePlateau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScenePlateauRepository extends JpaRepository<ScenePlateau, Long> {
    
    @Query("SELECT sp FROM ScenePlateau sp LEFT JOIN FETCH sp.scene LEFT JOIN FETCH sp.plateau WHERE sp.scene.id = :sceneId")
    List<ScenePlateau> findBySceneId(@Param("sceneId") Long sceneId);
    
    @Query("SELECT sp FROM ScenePlateau sp LEFT JOIN FETCH sp.scene LEFT JOIN FETCH sp.plateau WHERE sp.plateau.id = :plateauId")
    List<ScenePlateau> findByPlateauId(@Param("plateauId") Long plateauId);
    
    @Query("SELECT sp FROM ScenePlateau sp LEFT JOIN FETCH sp.scene s LEFT JOIN FETCH s.sequence seq LEFT JOIN FETCH seq.episode e LEFT JOIN FETCH e.projet WHERE e.projet.id = :projetId")
    List<ScenePlateau> findByProjetId(@Param("projetId") Long projetId);
    
    @Query("SELECT sp FROM ScenePlateau sp LEFT JOIN FETCH sp.scene LEFT JOIN FETCH sp.plateau WHERE sp.id = :id")
    Optional<ScenePlateau> findByIdWithDetails(@Param("id") Long id);
    
    boolean existsBySceneIdAndPlateauId(Long sceneId, Long plateauId);
    
    @Query("SELECT COUNT(sp) FROM ScenePlateau sp WHERE sp.scene.id = :sceneId")
    Integer countBySceneId(@Param("sceneId") Long sceneId);
}