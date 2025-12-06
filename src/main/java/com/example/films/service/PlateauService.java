package com.example.films.service;

import com.example.films.dto.CreatePlateauDTO;
import com.example.films.dto.PlateauDTO;
import com.example.films.entity.Plateau;
import com.example.films.entity.Lieu;
import com.example.films.repository.PlateauRepository;
import com.example.films.repository.LieuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlateauService {
    private final PlateauRepository plateauRepository;
    private final LieuRepository lieuRepository;

    public PlateauService(PlateauRepository plateauRepository, 
                        LieuRepository lieuRepository) {
        this.plateauRepository = plateauRepository;
        this.lieuRepository = lieuRepository;
    }

    public List<PlateauDTO> getPlateauxByLieuId(Long lieuId) {
        List<Plateau> plateaux = plateauRepository.findByLieuId(lieuId);
        return plateaux.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // RETIRER cette méthode - elle n'est plus utilisée
    // public List<PlateauDTO> getPlateauxBySceneId(Long sceneId) {
    //     List<Plateau> plateaux = plateauRepository.findBySceneId(sceneId);
    //     return plateaux.stream().map(this::convertToDTO).collect(Collectors.toList());
    // }

    public List<PlateauDTO> getPlateauxByProjetId(Long projetId) {
        List<Plateau> plateaux = plateauRepository.findByProjetId(projetId);
        return plateaux.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<PlateauDTO> getAllPlateaux() {
        List<Plateau> plateaux = plateauRepository.findAllOrdered();
        return plateaux.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public PlateauDTO getPlateauById(Long id) {
        Plateau plateau = plateauRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Plateau non trouvé"));
        return convertToDTO(plateau);
    }

    @Transactional
    public PlateauDTO createPlateau(CreatePlateauDTO createPlateauDTO) {
        // Vérifier si le plateau existe déjà pour ce lieu
        if (plateauRepository.existsByNomAndLieuId(createPlateauDTO.getNom(), createPlateauDTO.getLieuId())) {
            throw new RuntimeException("Un plateau avec ce nom existe déjà pour ce lieu");
        }

        Plateau plateau = new Plateau();
        plateau.setNom(createPlateauDTO.getNom());
        plateau.setTypePlateau(createPlateauDTO.getTypePlateau());
        plateau.setDescription(createPlateauDTO.getDescription());

        // Associer le lieu
        Lieu lieu = lieuRepository.findById(createPlateauDTO.getLieuId())
                .orElseThrow(() -> new RuntimeException("Lieu non trouvé"));
        plateau.setLieu(lieu);

        Plateau savedPlateau = plateauRepository.save(plateau);
        return convertToDTO(savedPlateau);
    }

    @Transactional
    public PlateauDTO updatePlateau(Long id, CreatePlateauDTO updatePlateauDTO) {
        Plateau plateau = plateauRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Plateau non trouvé"));

        plateau.setNom(updatePlateauDTO.getNom());
        plateau.setTypePlateau(updatePlateauDTO.getTypePlateau());
        plateau.setDescription(updatePlateauDTO.getDescription());

        // Mettre à jour le lieu si nécessaire
        if (!plateau.getLieu().getId().equals(updatePlateauDTO.getLieuId())) {
            Lieu lieu = lieuRepository.findById(updatePlateauDTO.getLieuId())
                    .orElseThrow(() -> new RuntimeException("Lieu non trouvé"));
            plateau.setLieu(lieu);
        }

        Plateau updatedPlateau = plateauRepository.save(plateau);
        return convertToDTO(updatedPlateau);
    }

    @Transactional
    public void deletePlateau(Long id) {
        Plateau plateau = plateauRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Plateau non trouvé"));
        plateauRepository.delete(plateau);
    }

    private PlateauDTO convertToDTO(Plateau plateau) {
        PlateauDTO dto = new PlateauDTO();
        dto.setId(plateau.getId());
        dto.setLieuId(plateau.getLieu().getId());
        dto.setNom(plateau.getNom());
        dto.setTypePlateau(plateau.getTypePlateau());
        dto.setDescription(plateau.getDescription());
        dto.setCreeLe(plateau.getCreeLe());
        dto.setModifieLe(plateau.getModifieLe());
        
        // Informations du lieu
        dto.setLieuNom(plateau.getLieu().getNomLieu());
        
        // Informations du projet via le lieu
        if (plateau.getLieu().getProjet() != null) {
            dto.setProjetTitre(plateau.getLieu().getProjet().getTitre());
        }
        
        return dto;
    }
}