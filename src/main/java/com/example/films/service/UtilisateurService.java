package com.example.films.service;

import com.example.films.dto.CreationUtilisateurRequest;
import com.example.films.dto.RealisateurDTO;
import com.example.films.dto.ScenaristeDTO;
import com.example.films.dto.UtilisateurCreeDTO;
import com.example.films.entity.Utilisateur;
import com.example.films.entity.Scenariste;
import com.example.films.entity.Realisateur;
import com.example.films.repository.UtilisateurRepository;
import com.example.films.repository.ScenaristeRepository;
import com.example.films.repository.RealisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UtilisateurService {
    private final UtilisateurRepository utilisateurRepository;
    private final ScenaristeRepository scenaristeRepository;
    private final RealisateurRepository realisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(UtilisateurRepository utilisateurRepository,
                             ScenaristeRepository scenaristeRepository,
                             RealisateurRepository realisateurRepository,
                             PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.scenaristeRepository = scenaristeRepository;
        this.realisateurRepository = realisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    @Transactional
    public UtilisateurCreeDTO creerScenariste(CreationUtilisateurRequest request) {
        // Vérifier si l'email existe déjà
        Optional<Utilisateur> utilisateurExistant = utilisateurRepository.findByEmail(request.getEmail());
        if (utilisateurExistant.isPresent()) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        // Créer l'utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        utilisateur.setRole("SCENARISTE");
        
        Utilisateur utilisateurSauvegarde = utilisateurRepository.save(utilisateur);

        // Créer le scénariste
        Scenariste scenariste = new Scenariste();
        scenariste.setUtilisateur(utilisateurSauvegarde);
        scenariste.setSpecialite(request.getSpecialite());
        scenariste.setBiographie(request.getBiographie());
        
        Scenariste scenaristeSauvegarde = scenaristeRepository.save(scenariste);

        // Retourner le DTO de réponse
        UtilisateurCreeDTO response = new UtilisateurCreeDTO();
        response.setIdUtilisateur(utilisateurSauvegarde.getId());
        response.setIdRole(scenaristeSauvegarde.getId());
        response.setNom(utilisateurSauvegarde.getNom());
        response.setEmail(utilisateurSauvegarde.getEmail());
        response.setRole(utilisateurSauvegarde.getRole());
        response.setSpecialite(scenaristeSauvegarde.getSpecialite());
        response.setBiographie(scenaristeSauvegarde.getBiographie());
        response.setMessage("Scénariste créé avec succès");

        return response;
    }

    @Transactional
    public UtilisateurCreeDTO creerRealisateur(CreationUtilisateurRequest request) {
        // Vérifier si l'email existe déjà
        Optional<Utilisateur> utilisateurExistant = utilisateurRepository.findByEmail(request.getEmail());
        if (utilisateurExistant.isPresent()) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        // Créer l'utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        utilisateur.setRole("REALISATEUR");
        
        Utilisateur utilisateurSauvegarde = utilisateurRepository.save(utilisateur);

        // Créer le réalisateur
        Realisateur realisateur = new Realisateur();
        realisateur.setUtilisateur(utilisateurSauvegarde);
        realisateur.setSpecialite(request.getSpecialite());
        realisateur.setBiographie(request.getBiographie());
        
        Realisateur realisateurSauvegarde = realisateurRepository.save(realisateur);

        // Retourner le DTO de réponse
        UtilisateurCreeDTO response = new UtilisateurCreeDTO();
        response.setIdUtilisateur(utilisateurSauvegarde.getId());
        response.setIdRole(realisateurSauvegarde.getId());
        response.setNom(utilisateurSauvegarde.getNom());
        response.setEmail(utilisateurSauvegarde.getEmail());
        response.setRole(utilisateurSauvegarde.getRole());
        response.setSpecialite(realisateurSauvegarde.getSpecialite());
        response.setBiographie(realisateurSauvegarde.getBiographie());
        response.setMessage("Réalisateur créé avec succès");

        return response;
    }

    public Optional<Utilisateur> findByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    @Transactional
    public UtilisateurCreeDTO modifierScenariste(Long id, CreationUtilisateurRequest request) {
        // Trouver le scénariste existant
        Scenariste scenaristeExistante = scenaristeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scénariste non trouvé avec l'ID: " + id));
        
        Utilisateur utilisateur = scenaristeExistante.getUtilisateur();
        
        // Vérifier si l'email est déjà utilisé par un autre utilisateur
        Optional<Utilisateur> utilisateurAvecEmail = utilisateurRepository.findByEmail(request.getEmail());
        if (utilisateurAvecEmail.isPresent() && !utilisateurAvecEmail.get().getId().equals(utilisateur.getId())) {
            throw new RuntimeException("Un autre utilisateur utilise déjà cet email");
        }
        
        // Mettre à jour l'utilisateur
        utilisateur.setNom(request.getNom());
        utilisateur.setEmail(request.getEmail());
        
        // Mettre à jour le mot de passe seulement si fourni
        if (request.getMotDePasse() != null && !request.getMotDePasse().trim().isEmpty()) {
            utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        }
        
        Utilisateur utilisateurModifie = utilisateurRepository.save(utilisateur);
        
        // Mettre à jour le scénariste
        scenaristeExistante.setSpecialite(request.getSpecialite());
        scenaristeExistante.setBiographie(request.getBiographie());
        
        Scenariste scenaristeModifie = scenaristeRepository.save(scenaristeExistante);
        
        // Retourner le DTO de réponse
        UtilisateurCreeDTO response = new UtilisateurCreeDTO();
        response.setIdUtilisateur(utilisateurModifie.getId());
        response.setIdRole(scenaristeModifie.getId());
        response.setNom(utilisateurModifie.getNom());
        response.setEmail(utilisateurModifie.getEmail());
        response.setRole(utilisateurModifie.getRole());
        response.setSpecialite(scenaristeModifie.getSpecialite());
        response.setBiographie(scenaristeModifie.getBiographie());
        response.setMessage("Scénariste modifié avec succès");
        
        return response;
    }

    @Transactional
    public UtilisateurCreeDTO modifierRealisateur(Long id, CreationUtilisateurRequest request) {
        // Trouver le réalisateur existant
        Realisateur realisateurExistant = realisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réalisateur non trouvé avec l'ID: " + id));
        
        Utilisateur utilisateur = realisateurExistant.getUtilisateur();
        
        // Vérifier si l'email est déjà utilisé par un autre utilisateur
        Optional<Utilisateur> utilisateurAvecEmail = utilisateurRepository.findByEmail(request.getEmail());
        if (utilisateurAvecEmail.isPresent() && !utilisateurAvecEmail.get().getId().equals(utilisateur.getId())) {
            throw new RuntimeException("Un autre utilisateur utilise déjà cet email");
        }
        
        // Mettre à jour l'utilisateur
        utilisateur.setNom(request.getNom());
        utilisateur.setEmail(request.getEmail());
        
        // Mettre à jour le mot de passe seulement si fourni
        if (request.getMotDePasse() != null && !request.getMotDePasse().trim().isEmpty()) {
            utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        }
        
        Utilisateur utilisateurModifie = utilisateurRepository.save(utilisateur);
        
        // Mettre à jour le réalisateur
        realisateurExistant.setSpecialite(request.getSpecialite());
        realisateurExistant.setBiographie(request.getBiographie());
        
        Realisateur realisateurModifie = realisateurRepository.save(realisateurExistant);
        
        // Retourner le DTO de réponse
        UtilisateurCreeDTO response = new UtilisateurCreeDTO();
        response.setIdUtilisateur(utilisateurModifie.getId());
        response.setIdRole(realisateurModifie.getId());
        response.setNom(utilisateurModifie.getNom());
        response.setEmail(utilisateurModifie.getEmail());
        response.setRole(utilisateurModifie.getRole());
        response.setSpecialite(realisateurModifie.getSpecialite());
        response.setBiographie(realisateurModifie.getBiographie());
        response.setMessage("Réalisateur modifié avec succès");
        
        return response;
    }

    @Transactional
    public void supprimerScenariste(Long id) {
        Scenariste scenariste = scenaristeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scénariste non trouvé avec l'ID: " + id));
        
        // Supprimer le scénariste (cela devrait supprimer l'utilisateur via CASCADE)
        scenaristeRepository.delete(scenariste);
    }

    @Transactional
    public void supprimerRealisateur(Long id) {
        Realisateur realisateur = realisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réalisateur non trouvé avec l'ID: " + id));
        
        // Supprimer le réalisateur (cela devrait supprimer l'utilisateur via CASCADE)
        realisateurRepository.delete(realisateur);
    }

    public List<ScenaristeDTO> getAllScenaristesDTO() {
        List<Scenariste> scenaristes = scenaristeRepository.findAllWithUtilisateur();
        return scenaristes.stream()
                .map(this::convertToScenaristeDTO)
                .collect(Collectors.toList());
    }
    
    public List<RealisateurDTO> getAllRealisateursDTO() {
        List<Realisateur> realisateurs = realisateurRepository.findAllWithUtilisateur();
        return realisateurs.stream()
                .map(this::convertToRealisateurDTO)
                .collect(Collectors.toList());
    }

    private ScenaristeDTO convertToScenaristeDTO(Scenariste scenariste) {
        ScenaristeDTO dto = new ScenaristeDTO();
        dto.setIdScenariste(scenariste.getId());
        dto.setIdUtilisateur(scenariste.getUtilisateur().getId());
        dto.setNom(scenariste.getUtilisateur().getNom());
        dto.setEmail(scenariste.getUtilisateur().getEmail());
        dto.setSpecialite(scenariste.getSpecialite());
        dto.setBiographie(scenariste.getBiographie());
        return dto;
    }

    private RealisateurDTO convertToRealisateurDTO(Realisateur realisateur) {
        RealisateurDTO dto = new RealisateurDTO();
        dto.setIdRealisateur(realisateur.getId());
        dto.setIdUtilisateur(realisateur.getUtilisateur().getId());
        dto.setNom(realisateur.getUtilisateur().getNom());
        dto.setEmail(realisateur.getUtilisateur().getEmail());
        dto.setSpecialite(realisateur.getSpecialite());
        dto.setBiographie(realisateur.getBiographie());
        return dto;
    }

    public Long getRoleIdByUtilisateurId(Long utilisateurId, String role) {
        if ("SCENARISTE".equals(role)) {
            return scenaristeRepository.findByUtilisateurId(utilisateurId)
                    .map(Scenariste::getId)
                    .orElseThrow(() -> new RuntimeException("Scénariste non trouvé pour l'utilisateur ID: " + utilisateurId));
        } else if ("REALISATEUR".equals(role)) {
            return realisateurRepository.findByUtilisateurId(utilisateurId)
                    .map(Realisateur::getId)
                    .orElseThrow(() -> new RuntimeException("Réalisateur non trouvé pour l'utilisateur ID: " + utilisateurId));
        }
        throw new RuntimeException("Rôle non supporté: " + role);
    }

    // Dans UtilisateurService.java
@Transactional
public void supprimerUtilisateur(Long utilisateurId) {
    Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + utilisateurId));
    
    String role = utilisateur.getRole();
    
    if ("SCENARISTE".equals(role)) {
        // Trouver et supprimer le scénariste associé
        Scenariste scenariste = scenaristeRepository.findByUtilisateurId(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Scénariste non trouvé pour l'utilisateur ID: " + utilisateurId));
        scenaristeRepository.delete(scenariste);
    } else if ("REALISATEUR".equals(role)) {
        // Trouver et supprimer le réalisateur associé
        Realisateur realisateur = realisateurRepository.findByUtilisateurId(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Réalisateur non trouvé pour l'utilisateur ID: " + utilisateurId));
        realisateurRepository.delete(realisateur);
    } else {
        // Pour les autres rôles (comme ADMIN), supprimer directement l'utilisateur
        utilisateurRepository.delete(utilisateur);
    }
}

// ... (code existant)

@Transactional
public UtilisateurCreeDTO creerAdmin(CreationUtilisateurRequest request) {
    // Vérifier si l'email existe déjà
    Optional<Utilisateur> utilisateurExistant = utilisateurRepository.findByEmail(request.getEmail());
    if (utilisateurExistant.isPresent()) {
        throw new RuntimeException("Un utilisateur avec cet email existe déjà");
    }

    // Créer l'utilisateur
    Utilisateur utilisateur = new Utilisateur();
    utilisateur.setNom(request.getNom());
    utilisateur.setEmail(request.getEmail());
    utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
    utilisateur.setRole("ADMIN");
    
    Utilisateur utilisateurSauvegarde = utilisateurRepository.save(utilisateur);

    // Retourner le DTO de réponse (sans rôle spécifique)
    UtilisateurCreeDTO response = new UtilisateurCreeDTO();
    response.setIdUtilisateur(utilisateurSauvegarde.getId());
    response.setIdRole(null);
    response.setNom(utilisateurSauvegarde.getNom());
    response.setEmail(utilisateurSauvegarde.getEmail());
    response.setRole(utilisateurSauvegarde.getRole());
    response.setSpecialite(null);
    response.setBiographie(null);
    response.setMessage("Administrateur créé avec succès");

    return response;
}

@Transactional
public UtilisateurCreeDTO modifierUtilisateur(Long id, CreationUtilisateurRequest request) {
    Utilisateur utilisateur = utilisateurRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));

    String role = utilisateur.getRole();

    // Vérifier email unique
    Optional<Utilisateur> utilisateurAvecEmail = utilisateurRepository.findByEmail(request.getEmail());
    if (utilisateurAvecEmail.isPresent() && !utilisateurAvecEmail.get().getId().equals(id)) {
        throw new RuntimeException("Un autre utilisateur utilise déjà cet email");
    }

    // Mise à jour commune
    utilisateur.setNom(request.getNom());
    utilisateur.setEmail(request.getEmail());
    if (request.getMotDePasse() != null && !request.getMotDePasse().trim().isEmpty()) {
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
    }

    if ("SCENARISTE".equals(role)) {
        Scenariste scenariste = scenaristeRepository.findByUtilisateurId(id)
                .orElseThrow(() -> new RuntimeException("Scénariste non trouvé"));
        scenariste.setSpecialite(request.getSpecialite());
        scenariste.setBiographie(request.getBiographie());
        scenaristeRepository.save(scenariste);
    } else if ("REALISATEUR".equals(role)) {
        Realisateur realisateur = realisateurRepository.findByUtilisateurId(id)
                .orElseThrow(() -> new RuntimeException("Réalisateur non trouvé"));
        realisateur.setSpecialite(request.getSpecialite());
        realisateur.setBiographie(request.getBiographie());
        realisateurRepository.save(realisateur);
    } // Pour ADMIN, pas de champs supplémentaires

    Utilisateur utilisateurModifie = utilisateurRepository.save(utilisateur);

    // Retourner le DTO
    UtilisateurCreeDTO response = new UtilisateurCreeDTO();
    response.setIdUtilisateur(utilisateurModifie.getId());
    response.setIdRole("SCENARISTE".equals(role) || "REALISATEUR".equals(role) ? getRoleIdByUtilisateurId(id, role) : null);
    response.setNom(utilisateurModifie.getNom());
    response.setEmail(utilisateurModifie.getEmail());
    response.setRole(utilisateurModifie.getRole());
    response.setSpecialite(request.getSpecialite());
    response.setBiographie(request.getBiographie());
    response.setMessage(role + " modifié avec succès");

    return response;
}

    @Transactional
    public UtilisateurCreeDTO creerUtilisateurStandard(CreationUtilisateurRequest request) {
        // Vérifier si l'email existe déjà
        Optional<Utilisateur> utilisateurExistant = utilisateurRepository.findByEmail(request.getEmail());
        if (utilisateurExistant.isPresent()) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        // Créer l'utilisateur standard
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        utilisateur.setRole("UTILISATEUR");
        
        Utilisateur utilisateurSauvegarde = utilisateurRepository.save(utilisateur);

        // Retourner le DTO de réponse
        UtilisateurCreeDTO response = new UtilisateurCreeDTO();
        response.setIdUtilisateur(utilisateurSauvegarde.getId());
        response.setIdRole(null);
        response.setNom(utilisateurSauvegarde.getNom());
        response.setEmail(utilisateurSauvegarde.getEmail());
        response.setRole(utilisateurSauvegarde.getRole());
        response.setSpecialite(null);
        response.setBiographie(null);
        response.setMessage("Utilisateur créé avec succès");

        return response;
    }

}