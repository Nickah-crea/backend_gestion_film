package com.example.films.controller;

import com.example.films.entity.StatutSequence;
import com.example.films.service.StatutSequenceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/statuts-sequence")
public class StatutSequenceController {
    private final StatutSequenceService statutSequenceService;

    public StatutSequenceController(StatutSequenceService statutSequenceService) {
        this.statutSequenceService = statutSequenceService;
    }

    @GetMapping
    public List<StatutSequence> getAllStatuts() {
        return statutSequenceService.getAllStatuts();
    }
}