package com.example.films.repository;

import com.example.films.entity.Plateau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlateauRepository extends JpaRepository<Plateau, Long> {
    
    @Query("SELECT p FROM Plateau p LEFT JOIN FETCH p.lieu WHERE p.lieu.id = :lieuId")
    List<Plateau> findByLieuId(@Param("lieuId") Long lieuId);
    
    // RETIRER cette méthode - elle référence 'scene' qui n'existe plus
    // @Query("SELECT p FROM Plateau p LEFT JOIN FETCH p.lieu LEFT JOIN FETCH p.scene WHERE p.scene.id = :sceneId")
    // List<Plateau> findBySceneId(@Param("sceneId") Long sceneId);
    
    @Query("SELECT p FROM Plateau p LEFT JOIN FETCH p.lieu l WHERE l.projet.id = :projetId")
    List<Plateau> findByProjetId(@Param("projetId") Long projetId);
    
    @Query("SELECT p FROM Plateau p LEFT JOIN FETCH p.lieu WHERE p.id = :id")
    Optional<Plateau> findByIdWithDetails(@Param("id") Long id);
    
    @Query("SELECT p FROM Plateau p LEFT JOIN FETCH p.lieu ORDER BY p.nom")
    List<Plateau> findAllOrdered();
    
    boolean existsByNomAndLieuId(String nom, Long lieuId);

    @Query("SELECT p FROM Plateau p WHERE p.lieu.projet.id = :projetId")
    List<Plateau> findByLieuProjetId(@Param("projetId") Long projetId);
}