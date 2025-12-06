package com.example.films.repository;

import com.example.films.entity.StatutSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatutSequenceRepository extends JpaRepository<StatutSequence, Long> {
    Optional<StatutSequence> findByCode(String code);
    Optional<StatutSequence> findByNomStatutsSequence(String nomStatutsSequence);
}