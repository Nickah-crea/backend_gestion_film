package com.example.films.service;

import com.example.films.dto.StatutPlanningDTO;
import com.example.films.entity.StatutPlanning;
import com.example.films.repository.StatutPlanningRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatutPlanningService {
    private final StatutPlanningRepository statutPlanningRepository;

    public StatutPlanningService(StatutPlanningRepository statutPlanningRepository) {
        this.statutPlanningRepository = statutPlanningRepository;
    }

    public List<StatutPlanningDTO> getAllStatuts() {
        List<StatutPlanning> statuts = statutPlanningRepository.findAllActifs();
        return statuts.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public StatutPlanningDTO getStatutById(Long id) {
        StatutPlanning statut = statutPlanningRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Statut non trouvé"));
        return convertToDTO(statut);
    }

    private StatutPlanningDTO convertToDTO(StatutPlanning statut) {
        StatutPlanningDTO dto = new StatutPlanningDTO();
        dto.setId(statut.getId());
        dto.setCode(statut.getCode());
        dto.setNomStatut(statut.getNomStatut());
        dto.setDescription(statut.getDescription());
        dto.setOrdreAffichage(statut.getOrdreAffichage());
        dto.setEstActif(statut.getEstActif());
        return dto;
    }
}

