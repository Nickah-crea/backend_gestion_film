package com.example.films.controller;

import com.example.films.dto.CreationUtilisateurRequest;
import com.example.films.dto.RealisateurDTO;
import com.example.films.dto.ScenaristeDTO;
import com.example.films.dto.UtilisateurCreeDTO;
import com.example.films.entity.Utilisateur;
import com.example.films.service.UtilisateurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/utilisateurs")
@CrossOrigin(origins = "http://localhost:5173")
public class UtilisateurController {
    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurService.getAllUtilisateurs();
    }

  @GetMapping("/scenaristes")
    public List<ScenaristeDTO> getAllScenaristes() {
        return utilisateurService.getAllScenaristesDTO(); 
    }

    @GetMapping("/realisateurs")
    public List<RealisateurDTO> getAllRealisateurs() {
        return utilisateurService.getAllRealisateursDTO(); 
    }
    
    @PostMapping("/creer-scenariste")
    public ResponseEntity<?> creerScenariste(@RequestBody CreationUtilisateurRequest request) {
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

            UtilisateurCreeDTO scenaristeCree = utilisateurService.creerScenariste(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(scenaristeCree);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(creerReponseErreur(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(creerReponseErreur("Erreur interne du serveur"));
        }
    }

    @PostMapping("/creer-realisateur")
    public ResponseEntity<?> creerRealisateur(@RequestBody CreationUtilisateurRequest request) {
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

            UtilisateurCreeDTO realisateurCree = utilisateurService.creerRealisateur(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(realisateurCree);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(creerReponseErreur(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(creerReponseErreur("Erreur interne du serveur"));
        }
    }

    // Endpoint pour vérifier si un email existe
    @GetMapping("/verifier-email")
    public ResponseEntity<?> verifierEmail(@RequestParam String email) {
        try {
            boolean existe = utilisateurService.findByEmail(email).isPresent();
            Map<String, Object> response = new HashMap<>();
            response.put("email", email);
            response.put("existe", existe);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(creerReponseErreur("Erreur lors de la vérification de l'email"));
        }
    }

    @PutMapping("/scenaristes/{id}")
    public ResponseEntity<?> modifierScenariste(@PathVariable Long id, @RequestBody CreationUtilisateurRequest request) {
        try {
            // Validation basique
            if (request.getNom() == null || request.getNom().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(creerReponseErreur("Le nom est obligatoire"));
            }
            
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(creerReponseErreur("L'email est obligatoire"));
            }

            // Ajoutez une méthode dans le service pour modifier un scénariste
            UtilisateurCreeDTO scenaristeModifie = utilisateurService.modifierScenariste(id, request);
            return ResponseEntity.ok(scenaristeModifie);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(creerReponseErreur(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(creerReponseErreur("Erreur interne du serveur lors de la modification"));
        }
    }

    @PutMapping("/realisateurs/{id}")
    public ResponseEntity<?> modifierRealisateur(@PathVariable Long id, @RequestBody CreationUtilisateurRequest request) {
        try {
            // Validation basique
            if (request.getNom() == null || request.getNom().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(creerReponseErreur("Le nom est obligatoire"));
            }
            
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(creerReponseErreur("L'email est obligatoire"));
            }

            UtilisateurCreeDTO realisateurModifie = utilisateurService.modifierRealisateur(id, request);
            return ResponseEntity.ok(realisateurModifie);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(creerReponseErreur(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(creerReponseErreur("Erreur interne du serveur lors de la modification"));
        }
    }

    @DeleteMapping("/scenaristes/{id}")
    public ResponseEntity<?> supprimerScenariste(@PathVariable Long id) {
        try {
            utilisateurService.supprimerScenariste(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Scénariste supprimé avec succès");
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(creerReponseErreur(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(creerReponseErreur("Erreur interne du serveur lors de la suppression"));
        }
    }

    @DeleteMapping("/realisateurs/{id}")
    public ResponseEntity<?> supprimerRealisateur(@PathVariable Long id) {
        try {
            utilisateurService.supprimerRealisateur(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Réalisateur supprimé avec succès");
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(creerReponseErreur(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(creerReponseErreur("Erreur interne du serveur lors de la suppression"));
        }
    }

    // Méthode utilitaire pour créer des réponses d'erreur standardisées
    private Map<String, Object> creerReponseErreur(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    // Endpoint pour la santé de l'API
    @GetMapping("/sante")
    public ResponseEntity<String> sante() {
        return ResponseEntity.ok("API Utilisateurs opérationnelle");
    }

    // Dans UtilisateurController.java
@DeleteMapping("/{id}")
public ResponseEntity<?> supprimerUtilisateur(@PathVariable Long id) {
    try {
        utilisateurService.supprimerUtilisateur(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Utilisateur supprimé avec succès");
        return ResponseEntity.ok(response);
        
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(creerReponseErreur(e.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(creerReponseErreur("Erreur interne du serveur lors de la suppression"));
    }
}

// Endpoint pour créer un admin
@PostMapping("/creer-admin")
public ResponseEntity<?> creerAdmin(@RequestBody CreationUtilisateurRequest request) {
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

        UtilisateurCreeDTO adminCree = utilisateurService.creerAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(adminCree);
        
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(creerReponseErreur(e.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(creerReponseErreur("Erreur interne du serveur"));
    }
}

// Endpoint unifié pour modifier par id_utilisateur
@PutMapping("/{id}")
public ResponseEntity<?> modifierUtilisateur(@PathVariable Long id, @RequestBody CreationUtilisateurRequest request) {
    try {
        // Validation basique
        if (request.getNom() == null || request.getNom().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(creerReponseErreur("Le nom est obligatoire"));
        }
        
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(creerReponseErreur("L'email est obligatoire"));
        }

        UtilisateurCreeDTO utilisateurModifie = utilisateurService.modifierUtilisateur(id, request);
        return ResponseEntity.ok(utilisateurModifie);
        
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(creerReponseErreur(e.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(creerReponseErreur("Erreur interne du serveur lors de la modification"));
    }
}

}