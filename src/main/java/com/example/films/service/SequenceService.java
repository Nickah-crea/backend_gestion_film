package com.example.films.service;

import com.example.films.dto.CreateSequenceDTO;
import com.example.films.dto.SequenceDTO;
import com.example.films.entity.Episode;
import com.example.films.entity.Sequence;
import com.example.films.entity.SequenceStatut;
import com.example.films.entity.StatutSequence;
import com.example.films.repository.EpisodeRepository;
import com.example.films.repository.SequenceRepository;
import com.example.films.repository.SequenceStatutRepository;
import com.example.films.repository.StatutSequenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.films.dto.RechercheSequenceDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Comparator;

@Service
public class SequenceService {
    private final SequenceRepository sequenceRepository;
    private final SequenceStatutRepository sequenceStatutRepository;
    private final EpisodeRepository episodeRepository;
    private final StatutSequenceRepository statutSequenceRepository;

    public SequenceService(SequenceRepository sequenceRepository,
                          SequenceStatutRepository sequenceStatutRepository,
                          EpisodeRepository episodeRepository,
                          StatutSequenceRepository statutSequenceRepository) {
        this.sequenceRepository = sequenceRepository;
        this.sequenceStatutRepository = sequenceStatutRepository;
        this.episodeRepository = episodeRepository;
        this.statutSequenceRepository = statutSequenceRepository;
    }

    public List<SequenceDTO> getSequencesByEpisodeId(Long episodeId) {
        List<Sequence> sequences = sequenceRepository.findByEpisodeId(episodeId);
        List<Sequence> sortedSequences = sequences.stream()
                .sorted(Comparator.comparingInt(Sequence::getOrdre))
                .collect(Collectors.toList());
        return sortedSequences.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<SequenceDTO> getSequencesByProjetId(Long projetId) {
        List<Sequence> sequences = sequenceRepository.findByEpisodeProjetId(projetId);
        return sequences.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SequenceDTO createSequence(Long episodeId, CreateSequenceDTO createSequenceDTO) {
        // Vérifier si l'ordre existe déjà pour cet épisode
        List<Sequence> existingSequences = sequenceRepository.findByEpisodeId(episodeId);
        boolean orderExists = existingSequences.stream()
                .anyMatch(seq -> seq.getOrdre().equals(createSequenceDTO.getOrdre()));
        
        if (orderExists) {
            throw new RuntimeException("L'ordre " + createSequenceDTO.getOrdre() + " existe déjà pour cet épisode");
        }

        // Créer la séquence
        Sequence sequence = new Sequence();
        sequence.setTitre(createSequenceDTO.getTitre());
        sequence.setOrdre(createSequenceDTO.getOrdre());
        sequence.setSynopsis(createSequenceDTO.getSynopsis());
        
        // Associer l'épisode
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new RuntimeException("Épisode non trouvé"));
        sequence.setEpisode(episode);
        
        // Sauvegarder la séquence
        Sequence savedSequence = sequenceRepository.save(sequence);
        
        // Créer le statut de la séquence
        SequenceStatut sequenceStatut = new SequenceStatut();
        sequenceStatut.setSequence(savedSequence);
        
        StatutSequence statut = statutSequenceRepository.findById(createSequenceDTO.getStatutId())
                .orElseThrow(() -> new RuntimeException("Statut non trouvé"));
        sequenceStatut.setStatut(statut);
        sequenceStatut.setDateDebut(LocalDateTime.now());
        
        sequenceStatutRepository.save(sequenceStatut);
        
        // Retourner le DTO
        return convertToDTO(savedSequence);
    }

    private SequenceDTO convertToDTO(Sequence sequence) {
        SequenceDTO dto = new SequenceDTO();
        dto.setIdSequence(sequence.getId());
        dto.setEpisodeId(sequence.getEpisode().getId());
        dto.setTitre(sequence.getTitre());
        dto.setOrdre(sequence.getOrdre());
        dto.setSynopsis(sequence.getSynopsis());
        dto.setCreeLe(sequence.getCreeLe());
        dto.setModifieLe(sequence.getModifieLe());
        dto.setEpisodeTitre(sequence.getEpisode().getTitre());
        
        // Récupérer le statut le plus récent
        Optional<SequenceStatut> statutOpt = sequenceStatutRepository.findLatestStatutBySequenceId(sequence.getId());
        if (statutOpt.isPresent()) {
            SequenceStatut statut = statutOpt.get();
            dto.setStatutNom(statut.getStatut().getNomStatutsSequence());
            
            // Si le statut est terminal, utiliser date_fin
            if (statut.getDateFin() != null && 
                ("tourne".equals(statut.getStatut().getCode()) || 
                 "valide".equals(statut.getStatut().getCode()))) {
                dto.setDateFin(statut.getDateFin());
            }
        } else {
            dto.setStatutNom("Non défini");
        }
        
        return dto;
    }

    @Transactional
    public SequenceDTO updateSequence(Long id, CreateSequenceDTO updateSequenceDTO) {
        Sequence sequence = sequenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Séquence non trouvée"));
        
        sequence.setTitre(updateSequenceDTO.getTitre());
        sequence.setOrdre(updateSequenceDTO.getOrdre());
        sequence.setSynopsis(updateSequenceDTO.getSynopsis());
        
        Sequence updatedSequence = sequenceRepository.save(sequence);
        
        // Mettre à jour le statut si nécessaire
        if (updateSequenceDTO.getStatutId() != null) {
            Optional<SequenceStatut> currentStatutOpt = sequenceStatutRepository.findLatestStatutBySequenceId(id);
            
            if (currentStatutOpt.isPresent()) {
                SequenceStatut currentStatut = currentStatutOpt.get();
                // Si le statut a changé, créer une nouvelle entrée
                if (!currentStatut.getStatut().getId().equals(updateSequenceDTO.getStatutId())) {
                    currentStatut.setDateFin(LocalDateTime.now());
                    sequenceStatutRepository.save(currentStatut);
                    
                    SequenceStatut newStatut = new SequenceStatut();
                    newStatut.setSequence(sequence);
                    StatutSequence statut = statutSequenceRepository.findById(updateSequenceDTO.getStatutId())
                            .orElseThrow(() -> new RuntimeException("Statut non trouvé"));
                    newStatut.setStatut(statut);
                    newStatut.setDateDebut(LocalDateTime.now());
                    sequenceStatutRepository.save(newStatut);
                }
            }
        }
        
        return convertToDTO(updatedSequence);
    }

    @Transactional
    public void deleteSequence(Long id) {
        Sequence sequence = sequenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Séquence non trouvée"));
        
        // Sauvegarder l'ordre et l'épisode avant suppression
        Integer deletedOrder = sequence.getOrdre();
        Long episodeId = sequence.getEpisode().getId();
        
        // Supprimer la séquence
        sequenceRepository.delete(sequence);
        
        // Recalculer les ordres des séquences restantes
        recalculerOrdresSequences(episodeId, deletedOrder);
    }

    private void recalculerOrdresSequences(Long episodeId, Integer deletedOrder) {
        List<Sequence> sequences = sequenceRepository.findByEpisodeId(episodeId);
        
        // Trier par ordre actuel
        List<Sequence> sequencesToUpdate = sequences.stream()
                .filter(seq -> seq.getOrdre() > deletedOrder)
                .sorted(Comparator.comparingInt(Sequence::getOrdre))
                .collect(Collectors.toList());
        
        // Mettre à jour les ordres
        int newOrder = deletedOrder;
        for (Sequence sequence : sequencesToUpdate) {
            sequence.setOrdre(newOrder);
            sequenceRepository.save(sequence);
            newOrder++;
        }
}

    public SequenceDTO getSequenceById(Long id) {
        Sequence sequence = sequenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Séquence non trouvée"));
        return convertToDTO(sequence);
    }

    public List<RechercheSequenceDTO> rechercherSequences(String query) {
        return sequenceRepository.rechercherSequences(query.toLowerCase());
    }
}
