package com.example.films.repository;

import com.example.films.entity.Scene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.films.dto.RechercheSceneDTO;

import java.util.List;
import java.util.Optional;

@Repository
public interface SceneRepository extends JpaRepository<Scene, Long> {
    
    @Query("SELECT s FROM Scene s LEFT JOIN FETCH s.sequence WHERE s.sequence.id = :sequenceId")
    List<Scene> findBySequenceId(@Param("sequenceId") Long sequenceId);
    
    @Query("SELECT s FROM Scene s LEFT JOIN FETCH s.sequence WHERE s.id = :id")
    Optional<Scene> findByIdWithSequence(@Param("id") Long id);

    @Query("SELECT COUNT(s) FROM Scene s WHERE s.sequence.id = :sequenceId")
    Integer countBySequenceId(@Param("sequenceId") Long sequenceId);
   
    @Query("SELECT s FROM Scene s LEFT JOIN FETCH s.sequence seq ORDER BY s.titre, seq.titre")
    List<Scene> findAllOrdered();
    
    @Query("SELECT s FROM Scene s LEFT JOIN FETCH s.sequence seq LEFT JOIN FETCH seq.episode e LEFT JOIN FETCH e.projet WHERE s.id = :id")
    Optional<Scene> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT s FROM Scene s LEFT JOIN FETCH s.sequence seq LEFT JOIN FETCH seq.episode e LEFT JOIN FETCH e.projet WHERE s.id = :id")
    Optional<Scene> findByIdWithFullDetails(@Param("id") Long id);

   @Query("SELECT new com.example.films.dto.RechercheSceneDTO(" +
       "s.id, s.titre, s.synopsis, seq.titre, e.titre, p.titre, p.id, e.id, seq.id) " + // Ajouter p.id, e.id, seq.id
       "FROM Scene s " +
       "LEFT JOIN s.sequence seq " +
       "LEFT JOIN seq.episode e " +
       "LEFT JOIN e.projet p " +
       "WHERE LOWER(s.titre) LIKE LOWER(CONCAT('%', :query, '%')) " +
       "OR LOWER(s.synopsis) LIKE LOWER(CONCAT('%', :query, '%')) " +
       "OR LOWER(seq.titre) LIKE LOWER(CONCAT('%', :query, '%')) " +
       "OR LOWER(e.titre) LIKE LOWER(CONCAT('%', :query, '%')) " +
       "OR LOWER(p.titre) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<RechercheSceneDTO> rechercherScenes(@Param("query") String query);

    @Query("SELECT s FROM Scene s LEFT JOIN FETCH s.sequence seq LEFT JOIN FETCH seq.episode e WHERE e.projet.id = :projetId")
    List<Scene> findBySequenceEpisodeProjetId(@Param("projetId") Long projetId);

     @Query("SELECT s FROM Scene s " +
           "LEFT JOIN FETCH s.sequence seq " +
           "LEFT JOIN FETCH seq.episode e " +
           "LEFT JOIN FETCH e.projet p " +
           "WHERE p.id = :projetId")
    List<Scene> findByProjetId(@Param("projetId") Long projetId);
    
    @Query("SELECT s FROM Scene s " +
           "LEFT JOIN FETCH s.sequence seq " +
           "LEFT JOIN FETCH seq.episode e " +
           "LEFT JOIN FETCH e.projet p")
    List<Scene> findAllWithDetails();

    // Ajouter ces méthodes dans SceneRepository.java
@Query("SELECT s FROM Scene s " +
       "LEFT JOIN FETCH s.sequence seq " +
       "LEFT JOIN FETCH seq.episode e " +
       "WHERE e.id = :episodeId")
List<Scene> findByEpisodeId(@Param("episodeId") Long episodeId);

}