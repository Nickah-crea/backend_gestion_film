package com.example.films.repository;

import com.example.films.entity.Sequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.films.dto.RechercheSequenceDTO;

import java.util.List;
import java.util.Optional;

@Repository
public interface SequenceRepository extends JpaRepository<Sequence, Long> {
    
    @Query("SELECT s FROM Sequence s LEFT JOIN FETCH s.episode WHERE s.episode.id = :episodeId")
    List<Sequence> findByEpisodeId(@Param("episodeId") Long episodeId);
    
    @Query("SELECT s FROM Sequence s LEFT JOIN FETCH s.episode WHERE s.id = :id")
    Optional<Sequence> findByIdWithEpisode(@Param("id") Long id);

    @Query("SELECT COUNT(s) FROM Sequence s WHERE s.episode.id = :episodeId")
    Integer countByEpisodeId(@Param("episodeId") Long episodeId);

    @Query("SELECT new com.example.films.dto.RechercheSequenceDTO(" +
       "s.id, s.titre, s.synopsis, e.titre, p.titre, p.id, e.id, s.ordre) " + // Ajouter p.id, e.id
       "FROM Sequence s " +
       "LEFT JOIN s.episode e " +
       "LEFT JOIN e.projet p " +
       "WHERE LOWER(s.titre) LIKE LOWER(CONCAT('%', :query, '%')) " +
       "OR LOWER(s.synopsis) LIKE LOWER(CONCAT('%', :query, '%')) " +
       "OR LOWER(e.titre) LIKE LOWER(CONCAT('%', :query, '%')) " +
       "OR LOWER(p.titre) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<RechercheSequenceDTO> rechercherSequences(@Param("query") String query);

    @Query("SELECT s FROM Sequence s LEFT JOIN FETCH s.episode e WHERE e.projet.id = :projetId")
    List<Sequence> findByEpisodeProjetId(@Param("projetId") Long projetId);
}


