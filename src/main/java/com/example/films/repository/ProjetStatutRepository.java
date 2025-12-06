package com.example.films.repository;

import com.example.films.entity.ProjetStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjetStatutRepository extends JpaRepository<ProjetStatut, Long> {
    
    @Query("SELECT ps FROM ProjetStatut ps WHERE ps.projet.id = :projetId ORDER BY ps.dateDebut DESC LIMIT 1")
    Optional<ProjetStatut> findLatestStatutByProjetId(@Param("projetId") Long projetId);
    
    List<ProjetStatut> findByProjetId(Long projetId);
    
    @Query("SELECT ps FROM ProjetStatut ps WHERE ps.projet.id = :projetId AND ps.dateFin IS NULL")
    Optional<ProjetStatut> findCurrentStatutByProjetId(@Param("projetId") Long projetId);
}