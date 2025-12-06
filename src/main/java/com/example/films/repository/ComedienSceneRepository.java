package com.example.films.repository;

import com.example.films.entity.ComedienScene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComedienSceneRepository extends JpaRepository<ComedienScene, Long> {
    
    @Query("SELECT cs FROM ComedienScene cs WHERE cs.scene.id = :sceneId AND cs.comedien.id = :comedienId")
    Optional<ComedienScene> findBySceneAndComedien(@Param("sceneId") Long sceneId, @Param("comedienId") Long comedienId);
    
    @Query("SELECT cs FROM ComedienScene cs WHERE cs.scene.id = :sceneId")
    List<ComedienScene> findBySceneId(@Param("sceneId") Long sceneId);
    
    @Query("SELECT cs FROM ComedienScene cs WHERE cs.comedien.id = :comedienId")
    List<ComedienScene> findByComedienId(@Param("comedienId") Long comedienId);
    
    @Query("SELECT COUNT(cs) > 0 FROM ComedienScene cs WHERE cs.scene.id = :sceneId AND cs.comedien.id = :comedienId")
    boolean existsBySceneIdAndComedienId(@Param("sceneId") Long sceneId, @Param("comedienId") Long comedienId);

}