package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "scene_statuts")
@Getter
@Setter
public class SceneStatut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_scene_statut")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_scene", referencedColumnName = "id_scene")
    private Scene scene;

    @ManyToOne
    @JoinColumn(name = "id_statut", referencedColumnName = "id_statut_scene")
    private StatutScene statut;

    @Column(name = "date_debut")
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;
}