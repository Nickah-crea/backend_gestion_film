package com.example.films.repository;

import com.example.films.entity.SequenceStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SequenceStatutRepository extends JpaRepository<SequenceStatut, Long> {
    
    @Query("SELECT ss FROM SequenceStatut ss WHERE ss.sequence.id = :sequenceId ORDER BY ss.dateDebut DESC LIMIT 1")
    Optional<SequenceStatut> findLatestStatutBySequenceId(@Param("sequenceId") Long sequenceId);
    
    List<SequenceStatut> findBySequenceId(Long sequenceId);
    
    @Query("SELECT ss FROM SequenceStatut ss WHERE ss.sequence.id = :sequenceId AND ss.dateFin IS NULL")
    Optional<SequenceStatut> findCurrentStatutBySequenceId(@Param("sequenceId") Long sequenceId);
}