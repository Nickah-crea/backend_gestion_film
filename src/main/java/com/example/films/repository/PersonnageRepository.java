package com.example.films.repository;

import com.example.films.entity.*;
import com.example.films.repository.*;
import com.example.films.dto.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonnageRepository extends JpaRepository<Personnage, Long> {
    
    List<Personnage> findByProjetId(Long projetId);
    
    List<Personnage> findByComedienId(Long comedienId);
    
    List<Personnage> findByComedienIsNull();
    
    @Query("SELECT p FROM Personnage p LEFT JOIN FETCH p.projet LEFT JOIN FETCH p.comedien WHERE p.id = :id")
    Optional<Personnage> findByIdWithDetails(@Param("id") Long id);
    
    @Query("SELECT p FROM Personnage p LEFT JOIN FETCH p.projet LEFT JOIN FETCH p.comedien")
    List<Personnage> findAllWithDetails();
    
    boolean existsByNomAndProjetId(String nom, Long projetId);

    // Trouver tous les personnages d'une scène (via les dialogues)
    @Query("SELECT DISTINCT p FROM Personnage p " +
           "JOIN Dialogue d ON d.personnage.id = p.id " +
           "WHERE d.scene.id = :sceneId")
    List<Personnage> findPersonnagesBySceneId(@Param("sceneId") Long sceneId);
    
    // Vérifier si un comédien joue dans une scène
    @Query("SELECT COUNT(p) > 0 FROM Personnage p " +
           "JOIN Dialogue d ON d.personnage.id = p.id " +
           "WHERE p.comedien.id = :comedienId AND d.scene.id = :sceneId")
    boolean existsComedienInScene(@Param("comedienId") Long comedienId, @Param("sceneId") Long sceneId);
    
    // Trouver le nom du personnage qu'un comédien joue dans une scène
    @Query("SELECT p.nom FROM Personnage p " +
           "JOIN Dialogue d ON d.personnage.id = p.id " +
           "WHERE p.comedien.id = :comedienId AND d.scene.id = :sceneId")
    Optional<String> findPersonnageNameByComedienAndScene(@Param("comedienId") Long comedienId, 
                                                         @Param("sceneId") Long sceneId);

        // Méthode pour trouver les personnages par scène
    @Query("SELECT p FROM Personnage p JOIN Dialogue d ON d.personnage = p WHERE d.scene.id = :sceneId")
    List<Personnage> findPersonnagesDialogueBySceneId(@Param("sceneId") Long sceneId);
    
    // NOUVELLE MÉTHODE : Trouver les dialogues d'un personnage
    @Query("SELECT d FROM Dialogue d WHERE d.personnage.id = :personnageId")
    List<Dialogue> findDialoguesByPersonnageId(@Param("personnageId") Long personnageId);
    
}

