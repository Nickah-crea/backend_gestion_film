// StatutEpisodeRepository.java
package com.example.films.repository;

import com.example.films.entity.StatutEpisode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatutEpisodeRepository extends JpaRepository<StatutEpisode, Long> {
    Optional<StatutEpisode> findByCode(String code);
    Optional<StatutEpisode> findByNomStatutsEpisode(String nomStatutsEpisode);
}