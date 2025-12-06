package com.example.films.repository;

import com.example.films.entity.Couleur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouleurRepository extends JpaRepository<Couleur, Long> {
    
    List<Couleur> findAllByOrderByNomAsc();
    
    Optional<Couleur> findByValeurHex(String valeurHex);
    
    List<Couleur> findByEstDefautTrue();
    
    @Query("SELECT c FROM Couleur c WHERE c.nom LIKE %:nom%")
    List<Couleur> findByNomContaining(String nom);
}
