
package com.example.films.service;

import com.example.films.entity.StatutEpisode;
import com.example.films.repository.StatutEpisodeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatutEpisodeService {
    private final StatutEpisodeRepository statutEpisodeRepository;

    public StatutEpisodeService(StatutEpisodeRepository statutEpisodeRepository) {
        this.statutEpisodeRepository = statutEpisodeRepository;
    }

    public List<StatutEpisode> getAllStatuts() {
        return statutEpisodeRepository.findAll();
    }
}