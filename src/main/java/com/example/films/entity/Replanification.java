package com.example.films.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "replanification")
@Data
public class Replanification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scene_id") 
    private Scene scene;

    @Column(name = "nouvelle_date")
    private LocalDate nouvelleDate;

     @Column(name = "nouvelle_heure_debut")
    private LocalTime nouvelleHeureDebut;

    @Column(name = "nouvelle_heure_fin")
    private LocalTime nouvelleHeureFin;

    @Column(name = "raison")
    private String raison;

    @Column(name = "statut")
    private String statut;

    @Column(name = "cree_le")
    private LocalDateTime creeLe = LocalDateTime.now();

    @Column(name = "raccord_ids", columnDefinition = "TEXT")
    private String raccordIds;
}