package com.example.films.repository;

import com.example.films.entity.SceneTournage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SceneTournageRepository extends JpaRepository<SceneTournage, Long> {

    Optional<SceneTournage> findBySceneId(Long sceneId);
    
    List<SceneTournage> findByDateTournage(LocalDate date);
    
    List<SceneTournage> findByDateTournageBetween(LocalDate startDate, LocalDate endDate);
    
    List<SceneTournage> findByStatutTournage(String statut);
    
    @Query("SELECT st FROM SceneTournage st WHERE st.scene.sequence.episode.projet.id = :projetId")
    List<SceneTournage> findByProjetId(@Param("projetId") Long projetId);
    
    @Query("SELECT st FROM SceneTournage st WHERE st.lieu.id = :lieuId")
    List<SceneTournage> findByLieuId(@Param("lieuId") Long lieuId);
    
    @Query("SELECT st FROM SceneTournage st WHERE st.statutTournage IN ('planifie', 'confirme') AND st.dateTournage <= :dateLimite")
    List<SceneTournage> findTournagesAConfirmer(@Param("dateLimite") LocalDate dateLimite);
    
    @Query("SELECT COUNT(st) > 0 FROM SceneTournage st WHERE st.scene.id = :sceneId")
    boolean existsBySceneId(@Param("sceneId") Long sceneId);
    
    @Query("SELECT st FROM SceneTournage st LEFT JOIN FETCH st.scene s LEFT JOIN FETCH s.sequence seq LEFT JOIN FETCH seq.episode e LEFT JOIN FETCH e.projet WHERE st.id = :id")
    Optional<SceneTournage> findByIdWithDetails(@Param("id") Long id);

    // NOUVELLE MÉTHODE : Trouver les statuts distincts par projet
        @Query("SELECT DISTINCT st.statutTournage FROM SceneTournage st " +
            "JOIN st.scene s " +
            "JOIN s.sequence seq " +
            "JOIN seq.episode e " +
            "JOIN e.projet p " +
            "WHERE p.id = :projetId")
        List<String> findStatutsDistinctsByProjetId(@Param("projetId") Long projetId);
        
    // @Query("SELECT st FROM SceneTournage st " +
    //        "JOIN st.scene s " +
    //        "JOIN ComedienScene cs ON s.id = cs.scene.id " +
    //        "WHERE cs.comedien.id = :comedienId " +
    //        "AND st.dateTournage = :date " +
    //        "AND st.statutTournage IN ('planifie', 'confirme', 'en_cours')")
    // List<SceneTournage> findTournagesByComedienAndDate(@Param("comedienId") Long comedienId, 
    //                                                   @Param("date") LocalDate date);
     
     // SceneTournageRepository.java                  
        List<SceneTournage> findByPlateauId(Long plateauId);

    // Dans SceneTournageRepository
    @Query("SELECT st FROM SceneTournage st " +
        "WHERE st.scene.sequence.episode.projet IS NULL " +
        "OR st.scene.sequence IS NULL " +
        "OR st.scene.sequence.episode IS NULL")
    List<SceneTournage> findScenesSansProjet();
}

