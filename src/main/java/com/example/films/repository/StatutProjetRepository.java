package com.example.films.repository;

import com.example.films.entity.StatutProjet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatutProjetRepository extends JpaRepository<StatutProjet, Long> {
    Optional<StatutProjet> findByCode(String code);
    Optional<StatutProjet> findByNomStatutsProjet(String nomStatutsProjet);
}