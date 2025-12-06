package com.example.films.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/export")
@CrossOrigin(origins = "*") // Autorise toutes les origines pour ce contrôleur
public class CorsController {

    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleOptions() {
        return ResponseEntity.ok().build();
    }
}