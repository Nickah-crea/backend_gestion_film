package com.example.films.repository;

import com.example.films.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlanningTournageRepository extends JpaRepository<PlanningTournage, Long> {
    
    @Query("SELECT p FROM PlanningTournage p LEFT JOIN FETCH p.scene s LEFT JOIN FETCH s.sequence seq LEFT JOIN FETCH seq.episode e LEFT JOIN FETCH e.projet WHERE p.dateTournage BETWEEN :startDate AND :endDate")
    List<PlanningTournage> findByDateTournageBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT p FROM PlanningTournage p LEFT JOIN FETCH p.scene s LEFT JOIN FETCH s.sequence seq LEFT JOIN FETCH seq.episode e LEFT JOIN FETCH e.projet WHERE p.scene.id = :sceneId")
    List<PlanningTournage> findBySceneId(@Param("sceneId") Long sceneId);

     @Query("SELECT p FROM PlanningTournage p LEFT JOIN FETCH p.scene s LEFT JOIN FETCH s.sequence seq LEFT JOIN FETCH seq.episode e LEFT JOIN FETCH e.projet WHERE p.scene.id = :sceneId")
    Optional<PlanningTournage> findFirstBySceneId(@Param("sceneId") Long sceneId);
    
    @Query("SELECT p FROM PlanningTournage p LEFT JOIN FETCH p.scene s LEFT JOIN FETCH s.sequence seq LEFT JOIN FETCH seq.episode e LEFT JOIN FETCH e.projet WHERE e.projet.id = :projetId")
    List<PlanningTournage> findByProjetId(@Param("projetId") Long projetId);
    
    @Query("SELECT p FROM PlanningTournage p LEFT JOIN FETCH p.scene s LEFT JOIN FETCH s.sequence seq LEFT JOIN FETCH seq.episode e LEFT JOIN FETCH e.projet WHERE p.statut.id = :statutId")
    List<PlanningTournage> findByStatutId(@Param("statutId") Long statutId);
    
    @Query("SELECT p FROM PlanningTournage p LEFT JOIN FETCH p.scene s LEFT JOIN FETCH s.sequence seq LEFT JOIN FETCH seq.episode e LEFT JOIN FETCH e.projet WHERE p.id = :id")
    java.util.Optional<PlanningTournage> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT p FROM PlanningTournage p WHERE p.dateTournage = :date")
    List<PlanningTournage> findByDateTournage(@Param("date") LocalDate date);

  
}
