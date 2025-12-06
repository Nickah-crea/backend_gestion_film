package com.example.films.repository;

import com.example.films.entity.TypeRaccord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TypeRaccordRepository extends JpaRepository<TypeRaccord, Long> {
    Optional<TypeRaccord> findByCode(String code);
    List<TypeRaccord> findAllByOrderByNomType();
}