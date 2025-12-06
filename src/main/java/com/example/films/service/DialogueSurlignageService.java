package com.example.films.service;

import com.example.films.dto.DialogueSurlignageDTO;
import com.example.films.entity.DialogueSurlignage;
import com.example.films.entity.Dialogue;
import com.example.films.entity.Couleur;
import com.example.films.repository.DialogueSurlignageRepository;
import com.example.films.repository.DialogueRepository;
import com.example.films.repository.CouleurRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DialogueSurlignageService {
    
    private final DialogueSurlignageRepository surlignageRepository;
    private final CouleurRepository couleurRepository;
    private final DialogueRepository dialogueRepository;
    
    public DialogueSurlignageService(DialogueSurlignageRepository surlignageRepository, 
                                   CouleurRepository couleurRepository,
                                   DialogueRepository dialogueRepository) {
        this.surlignageRepository = surlignageRepository;
        this.couleurRepository = couleurRepository;
        this.dialogueRepository = dialogueRepository;
    }
    
    public List<DialogueSurlignageDTO> getSurlignagesByDialogue(Long dialogueId) {
        List<DialogueSurlignage> surlignages = surlignageRepository.findByDialogueId(dialogueId);
        return surlignages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public DialogueSurlignageDTO addSurlignage(DialogueSurlignageDTO surlignageDTO) {
        // Récupérer le dialogue
        Dialogue dialogue = dialogueRepository.findById(surlignageDTO.getDialogueId())
                .orElseThrow(() -> new RuntimeException("Dialogue non trouvé avec l'ID: " + surlignageDTO.getDialogueId()));
        
        // Récupérer la couleur
        Couleur couleur = couleurRepository.findById(surlignageDTO.getCouleur().getId())
                .orElseThrow(() -> new RuntimeException("Couleur non trouvée avec l'ID: " + surlignageDTO.getCouleur().getId()));
        
        DialogueSurlignage surlignage = new DialogueSurlignage();
        surlignage.setDialogue(dialogue);
        surlignage.setCouleur(couleur);
        surlignage.setUtilisateurId(surlignageDTO.getUtilisateurId());
        surlignage.setTexteSurligne(surlignageDTO.getTexteSurligne());
        surlignage.setDebutIndex(surlignageDTO.getDebutIndex());
        surlignage.setFinIndex(surlignageDTO.getFinIndex());
        
        DialogueSurlignage saved = surlignageRepository.save(surlignage);
        return convertToDTO(saved);
    }
    
    public void removeSurlignage(Long surlignageId) {
        surlignageRepository.deleteById(surlignageId);
    }
    
    public void removeSurlignagesByDialogueAndUser(Long dialogueId, Long utilisateurId) {
        List<DialogueSurlignage> surlignages = surlignageRepository.findByDialogueIdAndUtilisateurId(dialogueId, utilisateurId);
        surlignageRepository.deleteAll(surlignages);
    }
    
    private DialogueSurlignageDTO convertToDTO(DialogueSurlignage surlignage) {
        DialogueSurlignageDTO dto = new DialogueSurlignageDTO();
        dto.setId(surlignage.getId());
        dto.setDialogueId(surlignage.getDialogue().getId());
        dto.setCouleur(convertCouleurToDTO(surlignage.getCouleur()));
        dto.setUtilisateurId(surlignage.getUtilisateurId());
        dto.setTexteSurligne(surlignage.getTexteSurligne());
        dto.setDebutIndex(surlignage.getDebutIndex());
        dto.setFinIndex(surlignage.getFinIndex());
        dto.setCreeLe(surlignage.getCreeLe());
        return dto;
    }
    
    private com.example.films.dto.CouleurDTO convertCouleurToDTO(Couleur couleur) {
        com.example.films.dto.CouleurDTO dto = new com.example.films.dto.CouleurDTO();
        dto.setId(couleur.getId());
        dto.setNom(couleur.getNom());
        dto.setValeurHex(couleur.getValeurHex());
        dto.setEstDefaut(couleur.getEstDefaut());
        dto.setCreeLe(couleur.getCreeLe());
        dto.setModifieLe(couleur.getModifieLe());
        return dto;
    }
}
