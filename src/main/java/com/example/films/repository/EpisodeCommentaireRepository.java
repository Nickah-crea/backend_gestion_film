package com.example.films.repository;

import com.example.films.entity.EpisodeCommentaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EpisodeCommentaireRepository extends JpaRepository<EpisodeCommentaire, Long> {
    
    @Query("SELECT ec FROM EpisodeCommentaire ec JOIN FETCH ec.commentaire c JOIN FETCH c.utilisateur WHERE ec.episode.id = :episodeId ORDER BY c.creeLe DESC")
    List<EpisodeCommentaire> findByEpisodeIdWithCommentaireAndUtilisateur(@Param("episodeId") Long episodeId);
    
    @Query("SELECT COUNT(ec) FROM EpisodeCommentaire ec WHERE ec.episode.id = :episodeId")
    Integer countByEpisodeId(@Param("episodeId") Long episodeId);
    
    List<EpisodeCommentaire> findByEpisodeId(Long episodeId);

    @Query("SELECT ec FROM EpisodeCommentaire ec WHERE ec.commentaire.id = :commentaireId")
    List<EpisodeCommentaire> findByEpisodeCommentaireId(@Param("commentaireId") Long commentaireId);
}
