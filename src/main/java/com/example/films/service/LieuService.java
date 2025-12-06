package com.example.films.service;

import com.example.films.dto.CreateLieuDTO;
import com.example.films.dto.LieuDTO;
import com.example.films.entity.Lieu;
import com.example.films.entity.Projet;
import com.example.films.repository.LieuRepository;
import com.example.films.repository.ProjetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LieuService {
    private final LieuRepository lieuRepository;
    private final ProjetRepository projetRepository;

    public LieuService(LieuRepository lieuRepository, ProjetRepository projetRepository) {
        this.lieuRepository = lieuRepository;
        this.projetRepository = projetRepository;
    }

    public List<LieuDTO> getLieuxByProjetId(Long projetId) {
        List<Lieu> lieux = lieuRepository.findByProjetId(projetId);
        return lieux.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<LieuDTO> getAllLieux() {
        List<Lieu> lieux = lieuRepository.findAllOrdered();
        return lieux.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public LieuDTO getLieuById(Long id) {
        Lieu lieu = lieuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lieu non trouvé"));
        return convertToDTO(lieu);
    }

    @Transactional
    public LieuDTO createLieu(CreateLieuDTO createLieuDTO) {
        // Vérifier si le lieu existe déjà pour ce projet
        if (lieuRepository.existsByNomLieuAndProjetId(createLieuDTO.getNomLieu(), createLieuDTO.getProjetId())) {
            throw new RuntimeException("Un lieu avec ce nom existe déjà pour ce projet");
        }

        Lieu lieu = new Lieu();
        lieu.setNomLieu(createLieuDTO.getNomLieu());
        lieu.setTypeLieu(createLieuDTO.getTypeLieu());
        lieu.setAdresse(createLieuDTO.getAdresse());

        Projet projet = projetRepository.findById(createLieuDTO.getProjetId())
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));
        lieu.setProjet(projet);

        Lieu savedLieu = lieuRepository.save(lieu);
        return convertToDTO(savedLieu);
    }

    @Transactional
    public LieuDTO updateLieu(Long id, CreateLieuDTO updateLieuDTO) {
        Lieu lieu = lieuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lieu non trouvé"));

        lieu.setNomLieu(updateLieuDTO.getNomLieu());
        lieu.setTypeLieu(updateLieuDTO.getTypeLieu());
        lieu.setAdresse(updateLieuDTO.getAdresse());

        if (!lieu.getProjet().getId().equals(updateLieuDTO.getProjetId())) {
            Projet projet = projetRepository.findById(updateLieuDTO.getProjetId())
                    .orElseThrow(() -> new RuntimeException("Projet non trouvé"));
            lieu.setProjet(projet);
        }

        Lieu updatedLieu = lieuRepository.save(lieu);
        return convertToDTO(updatedLieu);
    }

    @Transactional
    public void deleteLieu(Long id) {
        Lieu lieu = lieuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lieu non trouvé"));
        lieuRepository.delete(lieu);
    }

    private LieuDTO convertToDTO(Lieu lieu) {
        LieuDTO dto = new LieuDTO();
        dto.setId(lieu.getId());
        dto.setProjetId(lieu.getProjet().getId());
        dto.setNomLieu(lieu.getNomLieu());
        dto.setTypeLieu(lieu.getTypeLieu());
        dto.setAdresse(lieu.getAdresse());
        dto.setCreeLe(lieu.getCreeLe());
        dto.setModifieLe(lieu.getModifieLe());
        dto.setProjetTitre(lieu.getProjet().getTitre());
        return dto;
    }
    
}