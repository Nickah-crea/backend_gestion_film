package com.example.films.controller;

import com.example.films.dto.ComedienSceneDTO;
import com.example.films.dto.CreateComedienSceneDTO;
import com.example.films.service.ComedienSceneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comedien-scene")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ComedienSceneController {
    
    private final ComedienSceneService comedienSceneService;
    
    @GetMapping
    public ResponseEntity<List<ComedienSceneDTO>> getAllLiaisons() {
        return ResponseEntity.ok(comedienSceneService.getAllLiaisons());
    }
    
    @PostMapping
    public ResponseEntity<ComedienSceneDTO> createLiaison(@RequestBody CreateComedienSceneDTO dto) {
        return ResponseEntity.ok(comedienSceneService.createLiaison(dto));
    }

    @GetMapping("/scene/{sceneId}")
    public ResponseEntity<List<ComedienSceneDTO>> getComediensBySceneId(@PathVariable Long sceneId) {
        return ResponseEntity.ok(comedienSceneService.getComediensBySceneId(sceneId));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLiaison(@PathVariable Long id) {
        comedienSceneService.deleteLiaison(id);
        return ResponseEntity.noContent().build();
    }
}