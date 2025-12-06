package com.example.films.repository;

import com.example.films.entity.SequenceCommentaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SequenceCommentaireRepository extends JpaRepository<SequenceCommentaire, Long> {
    
    @Query("SELECT sc FROM SequenceCommentaire sc " +
           "JOIN FETCH sc.commentaire c " +
           "JOIN FETCH c.utilisateur " +
           "WHERE sc.sequence.id = :sequenceId " +
           "ORDER BY c.creeLe DESC")
    List<SequenceCommentaire> findBySequenceIdWithCommentaireAndUtilisateur(@Param("sequenceId") Long sequenceId);
    
    @Query("SELECT COUNT(sc) FROM SequenceCommentaire sc WHERE sc.sequence.id = :sequenceId")
    Integer countBySequenceId(@Param("sequenceId") Long sequenceId);
    
    List<SequenceCommentaire> findByCommentaireId(Long commentaireId);
    
    void deleteByCommentaireId(Long commentaireId);
}