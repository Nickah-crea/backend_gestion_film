package com.example.films.service;

import com.example.films.entity.StatutProjet;
import com.example.films.repository.StatutProjetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatutProjetService {
    private final StatutProjetRepository statutProjetRepository;

    public StatutProjetService(StatutProjetRepository statutProjetRepository) {
        this.statutProjetRepository = statutProjetRepository;
    }

    public List<StatutProjet> getAllStatuts() {
        return statutProjetRepository.findAll();
    }
}