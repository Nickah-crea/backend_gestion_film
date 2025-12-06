package com.example.films.service;

import com.example.films.dto.CouleurDTO;
import com.example.films.entity.Couleur;
import com.example.films.repository.CouleurRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouleurService {
    
    private final CouleurRepository couleurRepository;
    
    public CouleurService(CouleurRepository couleurRepository) {
        this.couleurRepository = couleurRepository;
    }
    
    public List<CouleurDTO> getAllCouleurs() {
        List<Couleur> couleurs = couleurRepository.findAllByOrderByNomAsc();
        return couleurs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public CouleurDTO getCouleurById(Long id) {
        Couleur couleur = couleurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Couleur non trouvée"));
        return convertToDTO(couleur);
    }
    
    public CouleurDTO getCouleurByHex(String valeurHex) {
        Couleur couleur = couleurRepository.findByValeurHex(valeurHex)
                .orElseThrow(() -> new RuntimeException("Couleur non trouvée"));
        return convertToDTO(couleur);
    }
    
    public List<CouleurDTO> getCouleursDefaut() {
        List<Couleur> couleurs = couleurRepository.findByEstDefautTrue();
        return couleurs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private CouleurDTO convertToDTO(Couleur couleur) {
        CouleurDTO dto = new CouleurDTO();
        dto.setId(couleur.getId());
        dto.setNom(couleur.getNom());
        dto.setValeurHex(couleur.getValeurHex());
        dto.setEstDefaut(couleur.getEstDefaut());
        dto.setCreeLe(couleur.getCreeLe());
        dto.setModifieLe(couleur.getModifieLe());
        return dto;
    }
}