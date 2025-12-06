package com.example.films.repository;

import com.example.films.entity.RaccordImagePartage;
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RaccordImagePartageRepository extends JpaRepository<RaccordImagePartage, Long> {
    List<RaccordImagePartage> findByRaccordId(Long raccordId);
    boolean existsByRaccordIdAndRaccordImageId(Long raccordId, Long raccordImageId);
    Optional<RaccordImagePartage> findByRaccordIdAndRaccordImageId(Long raccordId, Long raccordImageId);
    void deleteByRaccordId(Long raccordId); 
}