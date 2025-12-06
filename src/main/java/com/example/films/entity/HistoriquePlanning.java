package com.example.films.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_planning")
@Data
public class HistoriquePlanning {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historique")
    private Long id;
    
    @Column(name = "id_scene", nullable = false)
    private Long sceneId;
    
    @Column(name = "type_planning", nullable = false, length = 50)
    private String typePlanning; // 'SCENE_TOURNAGE' ou 'PLANNING_TOURNAGE'
    
    @Column(name = "ancienne_date", nullable = false)
    private LocalDate ancienneDate;
    
    @Column(name = "ancienne_heure_debut", length = 10)
    private String ancienneHeureDebut;
    
    @Column(name = "ancienne_heure_fin", length = 10)
    private String ancienneHeureFin;
    
    @Column(name = "ancien_statut", length = 50)
    private String ancienStatut;
    
    @Column(name = "ancien_lieu_id")
    private Long ancienLieuId;
    
    @Column(name = "ancien_plateau_id")
    private Long ancienPlateauId;
    
    @Column(name = "raison_replanification", columnDefinition = "TEXT")
    private String raisonReplanification;
    
    @Column(name = "date_replanification")
    private LocalDateTime dateReplanification = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_replanification")
    private Replanification replanification;
    
    // Constructeurs
    public HistoriquePlanning() {}
    
    public HistoriquePlanning(Long sceneId, String typePlanning, LocalDate ancienneDate, 
                             String ancienStatut, String raisonReplanification) {
        this.sceneId = sceneId;
        this.typePlanning = typePlanning;
        this.ancienneDate = ancienneDate;
        this.ancienStatut = ancienStatut;
        this.raisonReplanification = raisonReplanification;
        this.dateReplanification = LocalDateTime.now();
    }
    
    // Constructeur complet
    public HistoriquePlanning(Long sceneId, String typePlanning, LocalDate ancienneDate,
                             String ancienneHeureDebut, String ancienneHeureFin,
                             String ancienStatut, Long ancienLieuId, Long ancienPlateauId,
                             String raisonReplanification, Replanification replanification) {
        this.sceneId = sceneId;
        this.typePlanning = typePlanning;
        this.ancienneDate = ancienneDate;
        this.ancienneHeureDebut = ancienneHeureDebut;
        this.ancienneHeureFin = ancienneHeureFin;
        this.ancienStatut = ancienStatut;
        this.ancienLieuId = ancienLieuId;
        this.ancienPlateauId = ancienPlateauId;
        this.raisonReplanification = raisonReplanification;
        this.replanification = replanification;
        this.dateReplanification = LocalDateTime.now();
    }
}