package com.example.films.repository;

import com.example.films.entity.Raccord;
import com.example.films.entity.Scene;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RaccordRepository extends JpaRepository<Raccord, Long> {
    
    @Query("SELECT r FROM Raccord r " +
           "LEFT JOIN FETCH r.sceneSource ss " +
           "LEFT JOIN FETCH r.sceneCible sc " +
           "LEFT JOIN FETCH r.typeRaccord " +
           "LEFT JOIN FETCH r.statutRaccord " +
           "LEFT JOIN FETCH r.images " +
           "WHERE r.sceneSource.id = :sceneId OR r.sceneCible.id = :sceneId")
    List<Raccord> findBySceneId(@Param("sceneId") Long sceneId);
    
    @Query("SELECT r FROM Raccord r " +
           "LEFT JOIN FETCH r.sceneSource ss " +
           "LEFT JOIN FETCH r.sceneCible sc " +
           "LEFT JOIN FETCH r.typeRaccord " +
           "LEFT JOIN FETCH r.statutRaccord " +
           "LEFT JOIN FETCH r.images " +
           "WHERE r.sceneSource.id = :sceneSourceId AND r.sceneCible.id = :sceneCibleId")
    List<Raccord> findBySceneSourceAndCible(@Param("sceneSourceId") Long sceneSourceId, 
                                          @Param("sceneCibleId") Long sceneCibleId);
    
    @Query("SELECT r FROM Raccord r " +
           "LEFT JOIN FETCH r.sceneSource ss " +
           "LEFT JOIN FETCH r.sceneCible sc " +
           "LEFT JOIN FETCH r.typeRaccord " +
           "LEFT JOIN FETCH r.statutRaccord " +
           "LEFT JOIN FETCH r.images " +
           "WHERE r.id = :id")
    Optional<Raccord> findByIdWithDetails(@Param("id") Long id);
    
    @Query("SELECT COUNT(r) > 0 FROM Raccord r " +
           "WHERE r.sceneSource.id = :sceneSourceId " +
           "AND r.sceneCible.id = :sceneCibleId " +
           "AND r.typeRaccord.id = :typeRaccordId")
    boolean existsByScenesAndType(@Param("sceneSourceId") Long sceneSourceId,
                                 @Param("sceneCibleId") Long sceneCibleId,
                                 @Param("typeRaccordId") Long typeRaccordId);

//      @Query("SELECT r FROM Raccord r " +
//               "LEFT JOIN FETCH r.sceneSource ss " +
//               "LEFT JOIN FETCH r.sceneCible sc " +
//               "LEFT JOIN FETCH r.typeRaccord " +
//               "LEFT JOIN FETCH r.statutRaccord " +
//               "LEFT JOIN FETCH r.images")
//        List<Raccord> findAllWithDetails();


        @Query("SELECT r FROM Raccord r " +
           "LEFT JOIN FETCH r.sceneSource ss " +
           "LEFT JOIN FETCH r.sceneCible sc " +
           "LEFT JOIN FETCH r.typeRaccord " +
           "LEFT JOIN FETCH r.statutRaccord " +
           "LEFT JOIN FETCH r.images " +
           "LEFT JOIN FETCH r.personnage " +
           "LEFT JOIN FETCH r.comedien " +
           "WHERE r.personnage.id = :personnageId")
    List<Raccord> findByPersonnageId(@Param("personnageId") Long personnageId);

     @Query("SELECT r FROM Raccord r " +
           "LEFT JOIN FETCH r.sceneSource ss " +
           "LEFT JOIN FETCH r.sceneCible sc " +
           "LEFT JOIN FETCH r.typeRaccord " +
           "LEFT JOIN FETCH r.statutRaccord " +
           "LEFT JOIN FETCH r.images " +
           "LEFT JOIN FETCH r.personnage " +
           "LEFT JOIN FETCH r.comedien " +
           "WHERE r.comedien.id = :comedienId")
    List<Raccord> findByComedienId(@Param("comedienId") Long comedienId);

    @Query("SELECT r FROM Raccord r " +
           "LEFT JOIN FETCH r.sceneSource ss " +
           "LEFT JOIN FETCH r.sceneCible sc " +
           "LEFT JOIN FETCH r.typeRaccord " +
           "LEFT JOIN FETCH r.statutRaccord " +
           "LEFT JOIN FETCH r.images " +
           "LEFT JOIN FETCH r.personnage " +
           "LEFT JOIN FETCH r.comedien " +
           "WHERE r.personnage IS NULL")
    List<Raccord> findByPersonnageIsNull();

    @Query("SELECT r FROM Raccord r " +
           "LEFT JOIN FETCH r.sceneSource ss " +
           "LEFT JOIN FETCH r.sceneCible sc " +
           "LEFT JOIN FETCH r.typeRaccord " +
           "LEFT JOIN FETCH r.statutRaccord " +
           "LEFT JOIN FETCH r.images " +
           "LEFT JOIN FETCH r.personnage " +
           "LEFT JOIN FETCH r.comedien")
    List<Raccord> findAllWithDetails();

    // vérifier les raccords sur la même scène
    @Query("SELECT COUNT(r) > 0 FROM Raccord r WHERE " +
           "r.sceneSource.id = :sceneId AND " +
           "r.sceneCible.id = :sceneId AND " +
           "r.typeRaccord.id = :typeRaccordId")
    boolean existsBySameSceneAndType(@Param("sceneId") Long sceneId,
                                    @Param("typeRaccordId") Long typeRaccordId);




//        @Query("SELECT r FROM Raccord r " +
//        "LEFT JOIN FETCH r.sceneSource ss " +
//        "LEFT JOIN FETCH r.sceneCible sc " +
//        "LEFT JOIN FETCH r.typeRaccord " +
//        "LEFT JOIN FETCH r.statutRaccord " +
//        "LEFT JOIN FETCH r.images " +
//        "LEFT JOIN FETCH r.personnage " +
//        "LEFT JOIN FETCH r.comedien " +
//        "WHERE r.statutRaccord.code = 'A_VERIFIER' " +
//        "AND EXISTS (SELECT st FROM SceneTournage st WHERE st.scene.id = ss.id AND st.dateTournage = :dateTournage) " +
//        "OR EXISTS (SELECT st FROM SceneTournage st WHERE st.scene.id = sc.id AND st.dateTournage = :dateTournage)")
// List<Raccord> findRaccordsAVerifierPourDate(@Param("dateTournage") LocalDate dateTournage);


@Query("SELECT r FROM Raccord r " +
       "LEFT JOIN FETCH r.sceneSource ss " +
       "LEFT JOIN FETCH r.sceneCible sc " +
       "LEFT JOIN FETCH r.typeRaccord " +
       "LEFT JOIN FETCH r.statutRaccord " +
       "LEFT JOIN FETCH r.images " +
       "LEFT JOIN FETCH r.personnage " +
       "LEFT JOIN FETCH r.comedien " +
       "WHERE r.statutRaccord.code = 'A_VERIFIER' " +
       "AND EXISTS (SELECT pt FROM PlanningTournage pt WHERE pt.scene.id = ss.id AND pt.dateTournage = :dateTournage) " +
       "OR EXISTS (SELECT pt FROM PlanningTournage pt WHERE pt.scene.id = sc.id AND pt.dateTournage = :dateTournage)")
List<Raccord> findRaccordsAVerifierPourDate(@Param("dateTournage") LocalDate dateTournage);


// @Query("SELECT r FROM Raccord r " +
//        "LEFT JOIN FETCH r.sceneSource ss " +
//        "LEFT JOIN FETCH r.sceneCible sc " +
//        "LEFT JOIN FETCH r.typeRaccord " +
//        "LEFT JOIN FETCH r.statutRaccord " +
//        "WHERE r.estCritique = true " +
//        "AND r.statutRaccord.code != 'VALIDE' " +
//        "AND (EXISTS (SELECT st FROM SceneTournage st WHERE st.scene.id = ss.id AND st.dateTournage <= :dateLimite) " +
//        "OR EXISTS (SELECT st FROM SceneTournage st WHERE st.scene.id = sc.id AND st.dateTournage <= :dateLimite))")
// List<Raccord> findRaccordsCritiquesNonValides(@Param("dateLimite") LocalDate dateLimite);

@Query("SELECT r FROM Raccord r " +
       "LEFT JOIN FETCH r.sceneSource ss " +
       "LEFT JOIN FETCH r.sceneCible sc " +
       "LEFT JOIN FETCH r.typeRaccord " +
       "LEFT JOIN FETCH r.statutRaccord " +
       "WHERE r.estCritique = true " +
       "AND r.statutRaccord.code != 'VALIDE' " +
       "AND (EXISTS (SELECT pt FROM PlanningTournage pt WHERE pt.scene.id = ss.id AND pt.dateTournage <= :dateLimite) " +
       "OR EXISTS (SELECT pt FROM PlanningTournage pt WHERE pt.scene.id = sc.id AND pt.dateTournage <= :dateLimite))")
List<Raccord> findRaccordsCritiquesNonValides(@Param("dateLimite") LocalDate dateLimite);

// @Query("SELECT r FROM Raccord r " +
//        "LEFT JOIN FETCH r.sceneSource ss " +
//        "LEFT JOIN FETCH r.sceneCible sc " +
//        "LEFT JOIN FETCH r.typeRaccord " +
//        "LEFT JOIN FETCH r.statutRaccord " +
//        "LEFT JOIN FETCH r.images " +
//        "LEFT JOIN FETCH r.personnage " +
//        "LEFT JOIN FETCH r.comedien " +
//        "WHERE (ss.id IN (SELECT st.scene.id FROM SceneTournage st WHERE st.dateTournage = :date) " +
//        "OR sc.id IN (SELECT st.scene.id FROM SceneTournage st WHERE st.dateTournage = :date))")
// List<Raccord> findRaccordsParDateTournage(@Param("date") LocalDate date);

@Query("SELECT r FROM Raccord r " +
       "LEFT JOIN FETCH r.sceneSource ss " +
       "LEFT JOIN FETCH r.sceneCible sc " +
       "LEFT JOIN FETCH r.typeRaccord " +
       "LEFT JOIN FETCH r.statutRaccord " +
       "LEFT JOIN FETCH r.images " +
       "LEFT JOIN FETCH r.personnage " +
       "LEFT JOIN FETCH r.comedien " +
       "WHERE (ss.id IN (SELECT pt.scene.id FROM PlanningTournage pt WHERE pt.dateTournage = :date) " +
       "OR sc.id IN (SELECT pt.scene.id FROM PlanningTournage pt WHERE pt.dateTournage = :date))")
List<Raccord> findRaccordsParDateTournage(@Param("date") LocalDate date);

@Query("SELECT r FROM Raccord r " +
       "LEFT JOIN FETCH r.sceneSource " +
       "LEFT JOIN FETCH r.sceneCible " +
       "LEFT JOIN FETCH r.typeRaccord " +
       "LEFT JOIN FETCH r.statutRaccord " +
       "LEFT JOIN FETCH r.images " +
       "WHERE r.sceneSource = :sceneSource AND r.sceneCible = :sceneCible " +
       "ORDER BY r.id DESC")
Optional<Raccord> findTopBySceneSourceAndSceneCibleOrderByIdDesc(
    @Param("sceneSource") Scene sceneSource, 
    @Param("sceneCible") Scene sceneCible);
    

    @Query("SELECT r FROM Raccord r " +
       "LEFT JOIN FETCH r.sceneSource ss " +
       "LEFT JOIN FETCH ss.sequence seq " +
       "LEFT JOIN FETCH seq.episode e " +
       "LEFT JOIN FETCH e.projet p " +
       "LEFT JOIN FETCH r.sceneCible sc " +
       "LEFT JOIN FETCH r.typeRaccord " +
       "LEFT JOIN FETCH r.statutRaccord " +
       "LEFT JOIN FETCH r.personnage " +
       "LEFT JOIN FETCH r.comedien " +
       "LEFT JOIN FETCH r.images " +
       "WHERE r.typeRaccord.code IN ('ACCESSOIRE', 'VETEMENTS', 'COIFFURE') " +
       "AND r.comedien.id = :comedienId")
List<Raccord> findRaccordsAccessoiresByComedien(@Param("comedienId") Long comedienId);

@Query("SELECT r FROM Raccord r " +
       "LEFT JOIN FETCH r.sceneSource ss " +
       "LEFT JOIN FETCH ss.sequence seqs " +
       "LEFT JOIN FETCH seqs.episode eps " +
       "LEFT JOIN FETCH r.sceneCible sc " +
       "LEFT JOIN FETCH sc.sequence seqc " +
       "LEFT JOIN FETCH seqc.episode epc " +
       "LEFT JOIN FETCH r.typeRaccord " +
       "LEFT JOIN FETCH r.statutRaccord " +
       "LEFT JOIN FETCH r.images " +
       "LEFT JOIN FETCH r.personnage " +
       "LEFT JOIN FETCH r.comedien " +
       "WHERE eps.projet.id = :projetId OR epc.projet.id = :projetId")
List<Raccord> findByProjetId(@Param("projetId") Long projetId);

}

