package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "scene_tournage")
@Getter
@Setter
public class SceneTournage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_scene_tournage")
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_scene", referencedColumnName = "id_scene")
    private Scene scene;

    @Column(name = "date_tournage", nullable = false)
    private LocalDate dateTournage;

    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;

    @ManyToOne
    @JoinColumn(name = "id_lieu", referencedColumnName = "id_lieu")
    private Lieu lieu;

    @ManyToOne
    @JoinColumn(name = "id_plateau", referencedColumnName = "id_plateau")
    private Plateau plateau;

    @Column(name = "statut_tournage")
    private String statutTournage = "planifie";

    private String notes;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;

    @UpdateTimestamp
    @Column(name = "modifie_le")
    private LocalDateTime modifieLe;
}