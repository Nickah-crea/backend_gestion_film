package com.example.films.repository;

import com.example.films.entity.Lieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LieuRepository extends JpaRepository<Lieu, Long> {
    
    @Query("SELECT l FROM Lieu l LEFT JOIN FETCH l.projet WHERE l.projet.id = :projetId")
    List<Lieu> findByProjetId(@Param("projetId") Long projetId);
    
    @Query("SELECT l FROM Lieu l LEFT JOIN FETCH l.projet WHERE l.id = :id")
    Optional<Lieu> findByIdWithProjet(@Param("id") Long id);
    
    @Query("SELECT l FROM Lieu l LEFT JOIN FETCH l.projet p ORDER BY p.titre, l.nomLieu")
    List<Lieu> findAllOrdered();
    
    boolean existsByNomLieuAndProjetId(String nomLieu, Long projetId);

    @Query("SELECT l FROM Lieu l LEFT JOIN FETCH l.sceneLieus WHERE l.id = :id")
    Optional<Lieu> findByIdWithSceneLieus(@Param("id") Long id);
}