package com.example.films.service;

import com.example.films.entity.StatutSequence;
import com.example.films.repository.StatutSequenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatutSequenceService {
    private final StatutSequenceRepository statutSequenceRepository;

    public StatutSequenceService(StatutSequenceRepository statutSequenceRepository) {
        this.statutSequenceRepository = statutSequenceRepository;
    }

    public List<StatutSequence> getAllStatuts() {
        return statutSequenceRepository.findAll();
    }
}