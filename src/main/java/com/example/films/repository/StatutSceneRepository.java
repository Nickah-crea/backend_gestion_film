package com.example.films.repository;

import com.example.films.entity.StatutScene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatutSceneRepository extends JpaRepository<StatutScene, Long> {
    Optional<StatutScene> findByCode(String code);
    Optional<StatutScene> findByNomStatutsScene(String nomStatutsScene);
}