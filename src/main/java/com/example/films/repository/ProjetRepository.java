package com.example.films.repository;

import com.example.films.entity.Projet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjetRepository extends JpaRepository<Projet, Long> {
    
    @Query("SELECT p FROM Projet p LEFT JOIN FETCH p.genre")
    List<Projet> findAllWithGenre();
    
    @Query("SELECT p FROM Projet p WHERE p.genre.nomGenre = :genre")
    List<Projet> findByGenreNom(@Param("genre") String genre);
    
    @Query("SELECT p FROM Projet p LEFT JOIN FETCH p.genre WHERE p.id = :id")
    Optional<Projet> findByIdWithGenre(@Param("id") Long id);
}