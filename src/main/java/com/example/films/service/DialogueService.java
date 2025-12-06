package com.example.films.service;

import com.example.films.dto.CreateDialogueDTO;
import com.example.films.dto.DialogueDTO;
import com.example.films.entity.Dialogue;
import com.example.films.entity.Personnage;
import com.example.films.entity.Scene;
import com.example.films.repository.DialogueRepository;
import com.example.films.repository.PersonnageRepository;
import com.example.films.repository.SceneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DialogueService {
    
    private final DialogueRepository dialogueRepository;
    private final SceneRepository sceneRepository;
    private final PersonnageRepository personnageRepository;
    
    public DialogueService(DialogueRepository dialogueRepository,
                         SceneRepository sceneRepository,
                         PersonnageRepository personnageRepository) {
        this.dialogueRepository = dialogueRepository;
        this.sceneRepository = sceneRepository;
        this.personnageRepository = personnageRepository;
    }
    
    @Transactional
    public DialogueDTO createDialogue(CreateDialogueDTO createDialogueDTO) {
        // Récupérer la scène
        Scene scene = sceneRepository.findById(createDialogueDTO.getSceneId())
                .orElseThrow(() -> new RuntimeException("Scène non trouvée"));
        
        // Récupérer le personnage (peut être null)
        Personnage personnage = null;
        if (createDialogueDTO.getPersonnageId() != null) {
            personnage = personnageRepository.findById(createDialogueDTO.getPersonnageId())
                    .orElseThrow(() -> new RuntimeException("Personnage non trouvé"));
        }
        
        // Déterminer l'ordre si non fourni
        Integer ordre = createDialogueDTO.getOrdre();
        if (ordre == null) {
            Integer maxOrdre = dialogueRepository.findMaxOrdreBySceneId(createDialogueDTO.getSceneId());
            ordre = (maxOrdre != null) ? maxOrdre + 1 : 1;
        }
        
        // Créer le dialogue
        Dialogue dialogue = new Dialogue();
        dialogue.setScene(scene);
        dialogue.setPersonnage(personnage);
        dialogue.setTexte(createDialogueDTO.getTexte());
        dialogue.setOrdre(ordre);
        dialogue.setObservation(createDialogueDTO.getObservation());
        
        Dialogue savedDialogue = dialogueRepository.save(dialogue);
        return convertToDTO(savedDialogue);
    }
    
    public List<DialogueDTO> getAllDialogues() {
        List<Dialogue> dialogues = dialogueRepository.findAllWithDetails();
        return dialogues.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public DialogueDTO getDialogueById(Long id) {
        Dialogue dialogue = dialogueRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Dialogue non trouvé"));
        return convertToDTO(dialogue);
    }
    
    public List<DialogueDTO> getDialoguesByScene(Long sceneId) {
        List<Dialogue> dialogues = dialogueRepository.findBySceneIdOrderByOrdre(sceneId);
        return dialogues.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<DialogueDTO> getDialoguesByPersonnage(Long personnageId) {
        List<Dialogue> dialogues = dialogueRepository.findByPersonnageId(personnageId);
        return dialogues.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public DialogueDTO updateDialogue(Long id, CreateDialogueDTO updateDialogueDTO) {
        Dialogue dialogue = dialogueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dialogue non trouvé"));
        
        // Récupérer la scène
        Scene scene = sceneRepository.findById(updateDialogueDTO.getSceneId())
                .orElseThrow(() -> new RuntimeException("Scène non trouvée"));
        
        // Récupérer le personnage (peut être null)
        Personnage personnage = null;
        if (updateDialogueDTO.getPersonnageId() != null) {
            personnage = personnageRepository.findById(updateDialogueDTO.getPersonnageId())
                    .orElseThrow(() -> new RuntimeException("Personnage non trouvé"));
        }
        
        dialogue.setScene(scene);
        dialogue.setPersonnage(personnage);
        dialogue.setTexte(updateDialogueDTO.getTexte());
        dialogue.setOrdre(updateDialogueDTO.getOrdre());
        dialogue.setObservation(updateDialogueDTO.getObservation());
        
        Dialogue updatedDialogue = dialogueRepository.save(dialogue);
        return convertToDTO(updatedDialogue);
    }
    
    @Transactional
    public void deleteDialogue(Long id) {
        Dialogue dialogue = dialogueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dialogue non trouvé"));
        dialogueRepository.delete(dialogue);
    }
    
    @Transactional
    public void updateDialoguesOrdre(Long sceneId, List<Long> dialogueIdsInOrder) {
        List<Dialogue> dialogues = dialogueRepository.findBySceneId(sceneId);
        
        for (int i = 0; i < dialogueIdsInOrder.size(); i++) {
            Long dialogueId = dialogueIdsInOrder.get(i);
            Dialogue dialogue = dialogues.stream()
                    .filter(d -> d.getId().equals(dialogueId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Dialogue non trouvé: " + dialogueId));
            dialogue.setOrdre(i + 1);
        }
        
        dialogueRepository.saveAll(dialogues);
    }
    
    private DialogueDTO convertToDTO(Dialogue dialogue) {
        DialogueDTO dto = new DialogueDTO();
        dto.setId(dialogue.getId());
        dto.setTexte(dialogue.getTexte());
        dto.setOrdre(dialogue.getOrdre());
        dto.setObservation(dialogue.getObservation());
        dto.setCreeLe(dialogue.getCreeLe());
        dto.setModifieLe(dialogue.getModifieLe());
        
        if (dialogue.getScene() != null) {
            dto.setSceneId(dialogue.getScene().getId());
            dto.setSceneTitre(dialogue.getScene().getTitre());
            
            if (dialogue.getScene().getSequence() != null && 
                dialogue.getScene().getSequence().getEpisode() != null &&
                dialogue.getScene().getSequence().getEpisode().getProjet() != null) {
                dto.setProjetId(dialogue.getScene().getSequence().getEpisode().getProjet().getId());
                dto.setProjetTitre(dialogue.getScene().getSequence().getEpisode().getProjet().getTitre());
            }
        }
        
        if (dialogue.getPersonnage() != null) {
            dto.setPersonnageId(dialogue.getPersonnage().getId());
            dto.setPersonnageNom(dialogue.getPersonnage().getNom());
        }
        
        return dto;
    }
}
