package com.example.films.repository;

import com.example.films.entity.EpisodeScenariste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EpisodeScenaristeRepository extends JpaRepository<EpisodeScenariste, Long> {
    
    List<EpisodeScenariste> findByEpisodeId(Long episodeId);
    
    Optional<EpisodeScenariste> findByEpisodeIdAndScenaristeId(Long episodeId, Long scenaristeId);
    
    @Query("SELECT es FROM EpisodeScenariste es JOIN FETCH es.scenariste s JOIN FETCH s.utilisateur WHERE es.episode.id = :episodeId")
    List<EpisodeScenariste> findByEpisodeIdWithScenariste(@Param("episodeId") Long episodeId);
    List<EpisodeScenariste> findByScenaristeId(Long scenaristeId);

    @Query("SELECT COUNT(es) > 0 FROM EpisodeScenariste es WHERE es.scenariste.id = :scenaristeId AND es.episode.projet.id = :projetId")
    boolean existsByScenaristeIdAndProjetId(@Param("scenaristeId") Long scenaristeId, 
                                          @Param("projetId") Long projetId);
}