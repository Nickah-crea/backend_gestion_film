
package com.example.films.repository;

import com.example.films.entity.EpisodeStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EpisodeStatutRepository extends JpaRepository<EpisodeStatut, Long> {
    
    @Query("SELECT es FROM EpisodeStatut es WHERE es.episode.id = :episodeId ORDER BY es.dateDebut DESC LIMIT 1")
    Optional<EpisodeStatut> findLatestStatutByEpisodeId(@Param("episodeId") Long episodeId);
    
    List<EpisodeStatut> findByEpisodeId(Long episodeId);
    
    @Query("SELECT es FROM EpisodeStatut es WHERE es.episode.id = :episodeId AND es.dateFin IS NULL")
    Optional<EpisodeStatut> findCurrentStatutByEpisodeId(@Param("episodeId") Long episodeId);
}