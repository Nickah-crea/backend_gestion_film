package com.example.films.repository;

import com.example.films.entity.Scenariste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScenaristeRepository extends JpaRepository<Scenariste, Long> {
    
    @Query("SELECT s FROM Scenariste s JOIN FETCH s.utilisateur")
    List<Scenariste> findAllWithUtilisateur();
    
    Optional<Scenariste> findByUtilisateurId(Long utilisateurId);
    
    @Query("SELECT s FROM Scenariste s JOIN FETCH s.utilisateur WHERE s.id = :id")
    Optional<Scenariste> findByIdWithUtilisateur(@Param("id") Long id);
}