package com.example.films.controller;

import com.example.films.dto.CreationUtilisateurRequest;
import com.example.films.dto.UtilisateurCreeDTO;
import com.example.films.entity.Utilisateur;
import com.example.films.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
    
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody CreationUtilisateurRequest request) {
        try {
            // Validation basique
            if (request.getNom() == null || request.getNom().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(creerReponseErreur("Le nom est obligatoire"));
            }
            
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(creerReponseErreur("L'email est obligatoire"));
            }
            
            if (request.getMotDePasse() == null || request.getMotDePasse().length() < 6) {
                return ResponseEntity.badRequest().body(creerReponseErreur("Le mot de passe doit contenir au moins 6 caractères"));
            }

            // Le rôle n'est plus obligatoire, on utilise "UTILISATEUR" par défaut
            if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
                String roleUpper = request.getRole().toUpperCase();
                if (!roleUpper.equals("SCENARISTE") && !roleUpper.equals("REALISATEUR") && !roleUpper.equals("ADMIN") && !roleUpper.equals("UTILISATEUR")) {
                    return ResponseEntity.badRequest().body(creerReponseErreur("Rôle non valide. Rôles acceptés: SCENARISTE, REALISATEUR, ADMIN, UTILISATEUR"));
                }
            }

            UtilisateurCreeDTO utilisateurCree = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(utilisateurCree);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(creerReponseErreur(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(creerReponseErreur("Erreur interne du serveur"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");

            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(creerReponseErreur("L'email est obligatoire"));
            }

            if (password == null || password.isEmpty()) {
                return ResponseEntity.badRequest().body(creerReponseErreur("Le mot de passe est obligatoire"));
            }

            Map<String, Object> response = authService.login(email, password);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(creerReponseErreur(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(creerReponseErreur("Erreur interne du serveur"));
        }
    }

    private Map<String, Object> creerReponseErreur(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}

