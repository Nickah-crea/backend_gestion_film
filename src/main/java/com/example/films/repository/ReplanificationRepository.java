package com.example.films.repository;

import com.example.films.entity.Replanification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface ReplanificationRepository extends JpaRepository<Replanification, Long> {
    
    
    List<Replanification> findBySceneId(Long sceneId);
    boolean existsBySceneIdAndStatut(Long sceneId, String statut);
    
    @Query("SELECT r FROM Replanification r LEFT JOIN FETCH r.scene WHERE r.id = :id")
    Optional<Replanification> findByIdWithDetails(@Param("id") Long id);
    
    @Query("SELECT r FROM Replanification r LEFT JOIN FETCH r.scene")
    List<Replanification> findAllWithDetails();
    
    // Méthodes existantes à adapter ou supprimer
    List<Replanification> findByStatut(String statut);
    
    @Query("SELECT r FROM Replanification r LEFT JOIN FETCH r.scene s LEFT JOIN s.sequence seq LEFT JOIN seq.episode e LEFT JOIN e.projet p WHERE p.id = :projetId")
    List<Replanification> findByProjetId(@Param("projetId") Long projetId);
    
    @Query("SELECT r FROM Replanification r LEFT JOIN FETCH r.scene WHERE r.nouvelleDate <= :dateLimite AND r.statut = 'PLANIFIEE'")
    List<Replanification> findReplanificationsAValider(@Param("dateLimite") LocalDate dateLimite);
}