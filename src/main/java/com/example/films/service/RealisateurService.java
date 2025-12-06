package com.example.films.service;

import com.example.films.dto.RealisateurDTO;
import com.example.films.entity.Realisateur;
import com.example.films.repository.RealisateurRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RealisateurService {
    
    private final RealisateurRepository realisateurRepository;
    
    public RealisateurService(RealisateurRepository realisateurRepository) {
        this.realisateurRepository = realisateurRepository;
    }
    
    public List<RealisateurDTO> getAllRealisateurs() {
        List<Realisateur> realisateurs = realisateurRepository.findAllWithUtilisateur();
        return realisateurs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private RealisateurDTO convertToDTO(Realisateur realisateur) {
        RealisateurDTO dto = new RealisateurDTO();
        dto.setIdRealisateur(realisateur.getId());
        dto.setIdUtilisateur(realisateur.getUtilisateur().getId());
        dto.setNom(realisateur.getUtilisateur().getNom());
        dto.setEmail(realisateur.getUtilisateur().getEmail());
        dto.setSpecialite(realisateur.getSpecialite());
        dto.setBiographie(realisateur.getBiographie());
        return dto;
    }
}