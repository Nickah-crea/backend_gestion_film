package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "planning_tournage")
@Getter
@Setter
public class PlanningTournage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_planning_tournage")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_scene", referencedColumnName = "id_scene")
    private Scene scene;

    @Column(name = "date_tournage", nullable = false)
    private LocalDate dateTournage;

    @Column(name = "heure_debut")
    private String heureDebut;

    @Column(name = "heure_fin")
    private String heureFin;

    @ManyToOne
    @JoinColumn(name = "id_statut_planning", referencedColumnName = "id_statut_planning")
    private StatutPlanning statut;

    private String description;

    @ManyToOne
    @JoinColumn(name = "id_lieu", referencedColumnName = "id_lieu")
    private Lieu lieu;

    @ManyToOne
    @JoinColumn(name = "id_plateau", referencedColumnName = "id_plateau")
    private Plateau plateau;


    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;

    @UpdateTimestamp
    @Column(name = "modifie_le")
    private LocalDateTime modifieLe;
    
}

