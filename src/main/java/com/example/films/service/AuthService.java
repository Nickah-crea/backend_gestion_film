package com.example.films.service;

import com.example.films.dto.CreationUtilisateurRequest;
import com.example.films.dto.UtilisateurCreeDTO;
import com.example.films.entity.Utilisateur;
import com.example.films.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
    
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final UtilisateurService utilisateurService;

    public AuthService(UtilisateurRepository utilisateurRepository, 
                      PasswordEncoder passwordEncoder,
                      UtilisateurService utilisateurService) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.utilisateurService = utilisateurService;
    }

    @Transactional
    public UtilisateurCreeDTO register(CreationUtilisateurRequest request) {
        // Vérifier si l'email existe déjà
        Optional<Utilisateur> utilisateurExistant = utilisateurRepository.findByEmail(request.getEmail());
        if (utilisateurExistant.isPresent()) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        // Déterminer le rôle (par défaut "UTILISATEUR" si non spécifié)
        String role = request.getRole();
        if (role == null || role.trim().isEmpty()) {
            role = "UTILISATEUR";
        }

        // Créer l'utilisateur selon le rôle
        switch (role.toUpperCase()) {
            case "SCENARISTE":
                return utilisateurService.creerScenariste(request);
            case "REALISATEUR":
                return utilisateurService.creerRealisateur(request);
            case "ADMIN":
                return utilisateurService.creerAdmin(request);
            case "UTILISATEUR":
                return utilisateurService.creerUtilisateurStandard(request);
            default:
                throw new RuntimeException("Rôle non valide: " + request.getRole());
        }
    }

    public Map<String, Object> login(String email, String password) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(email);
        
        if (utilisateurOpt.isEmpty() || !passwordEncoder.matches(password, utilisateurOpt.get().getMotDePasse())) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        Utilisateur utilisateur = utilisateurOpt.get();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Connexion réussie");
        
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", utilisateur.getId());
        userData.put("nom", utilisateur.getNom());
        userData.put("email", utilisateur.getEmail());
        userData.put("role", utilisateur.getRole());
        
        response.put("user", userData);
        response.put("token", genererTokenSimule(utilisateur)); // À remplacer par JWT

        return response;
    }

    private String genererTokenSimule(Utilisateur utilisateur) {
        // Pour l'instant, génération d'un token simple
        // À remplacer par une implémentation JWT réelle
        return "token-simule-" + utilisateur.getId() + "-" + System.currentTimeMillis();
    }
}

