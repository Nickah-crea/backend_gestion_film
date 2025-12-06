package com.example.films.repository;

import com.example.films.entity.EpisodeRealisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EpisodeRealisateurRepository extends JpaRepository<EpisodeRealisateur, Long> {
    
    List<EpisodeRealisateur> findByEpisodeId(Long episodeId);
    
    
    @Query("SELECT er FROM EpisodeRealisateur er JOIN FETCH er.realisateur r JOIN FETCH r.utilisateur WHERE er.episode.id = :episodeId")
    List<EpisodeRealisateur> findByEpisodeIdWithRealisateur(@Param("episodeId") Long episodeId);
    List<EpisodeRealisateur> findByRealisateurId(Long realisateurId);

    @Query("SELECT er FROM EpisodeRealisateur er WHERE er.episode.id = :episodeId AND er.realisateur.id = :realisateurId")
    Optional<EpisodeRealisateur> findByEpisodeIdAndRealisateurId(@Param("episodeId") Long episodeId, 
                                                               @Param("realisateurId") Long realisateurId);
    
    @Query("SELECT COUNT(er) > 0 FROM EpisodeRealisateur er WHERE er.realisateur.id = :realisateurId AND er.episode.projet.id = :projetId")
    boolean existsByRealisateurIdAndProjetId(@Param("realisateurId") Long realisateurId, 
                                           @Param("projetId") Long projetId);

}