package com.example.films.repository;

import com.example.films.entity.HistoriquePlanning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriquePlanningRepository extends JpaRepository<HistoriquePlanning, Long> {
    
    /**
     * Trouver l'historique par ID de scène
     */
    List<HistoriquePlanning> findBySceneIdOrderByDateReplanificationDesc(Long sceneId);
    
    /**
     * Trouver l'historique par type de planning
     */
    List<HistoriquePlanning> findBySceneIdAndTypePlanningOrderByDateReplanificationDesc(
            Long sceneId, String typePlanning);
    
    /**
     * Trouver l'historique avec les détails de la scène et du projet
     */
    @Query("SELECT h FROM HistoriquePlanning h " +
           "LEFT JOIN FETCH h.replanification r " +
           "LEFT JOIN Scene s ON h.sceneId = s.id " +
           "LEFT JOIN Sequence seq ON s.sequence.id = seq.id " +
           "LEFT JOIN Episode e ON seq.episode.id = e.id " +
           "LEFT JOIN Projet p ON e.projet.id = p.id " +
           "WHERE h.sceneId = :sceneId " +
           "ORDER BY h.dateReplanification DESC")
    List<HistoriquePlanning> findBySceneIdWithDetails(@Param("sceneId") Long sceneId);
    
    /**
     * Trouver l'historique par projet
     */
    @Query("SELECT h FROM HistoriquePlanning h " +
           "JOIN Scene s ON h.sceneId = s.id " +
           "JOIN Sequence seq ON s.sequence.id = seq.id " +
           "JOIN Episode e ON seq.episode.id = e.id " +
           "JOIN Projet p ON e.projet.id = p.id " +
           "WHERE p.id = :projetId " +
           "ORDER BY h.dateReplanification DESC")
    List<HistoriquePlanning> findByProjetId(@Param("projetId") Long projetId);
    
    /**
     * Trouver l'historique par période
     */
    @Query("SELECT h FROM HistoriquePlanning h " +
           "WHERE h.dateReplanification BETWEEN :startDate AND :endDate " +
           "ORDER BY h.dateReplanification DESC")
    List<HistoriquePlanning> findByDateReplanificationBetween(
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate);
    
    /**
     * Vérifier s'il existe un historique pour une scène
     */
    boolean existsBySceneId(Long sceneId);
    
    /**
     * Compter le nombre de replanifications pour une scène
     */
    Long countBySceneId(Long sceneId);
    
    /**
     * Trouver le dernier historique pour une scène
     */
    @Query("SELECT h FROM HistoriquePlanning h " +
           "WHERE h.sceneId = :sceneId " +
           "ORDER BY h.dateReplanification DESC " +
           "LIMIT 1")
    HistoriquePlanning findLatestBySceneId(@Param("sceneId") Long sceneId);
}