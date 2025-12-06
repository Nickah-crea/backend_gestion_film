package com.example.films.repository;

import com.example.films.entity.Realisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RealisateurRepository extends JpaRepository<Realisateur, Long> {
    
    @Query("SELECT r FROM Realisateur r JOIN FETCH r.utilisateur")
    List<Realisateur> findAllWithUtilisateur();
    
    Optional<Realisateur> findByUtilisateurId(Long utilisateurId);
    
    @Query("SELECT r FROM Realisateur r JOIN FETCH r.utilisateur WHERE r.id = :id")
    Optional<Realisateur> findByIdWithUtilisateur(@Param("id") Long id);
}