package com.example.films.repository;

import com.example.films.entity.RaccordImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RaccordImageRepository extends JpaRepository<RaccordImage, Long> {
    List<RaccordImage> findByRaccordId(Long raccordId);
    
    @Query("SELECT ri FROM RaccordImage ri WHERE ri.raccord.id = :raccordId AND ri.estImageReference = true")
    List<RaccordImage> findReferenceImagesByRaccordId(@Param("raccordId") Long raccordId);
    long countByRaccordId(Long raccordId);
}