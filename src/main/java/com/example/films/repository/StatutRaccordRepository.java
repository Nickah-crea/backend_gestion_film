package com.example.films.repository;

import com.example.films.entity.StatutRaccord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StatutRaccordRepository extends JpaRepository<StatutRaccord, Long> {
    Optional<StatutRaccord> findByCode(String code);
}