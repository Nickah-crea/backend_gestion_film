package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "statuts_scene")
@Getter
@Setter
public class StatutScene {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_statut_scene")
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "nom_statuts_scene", nullable = false)
    private String nomStatutsScene;

    private String description;

    @Column(name = "ordre_affichage", nullable = false)
    private Integer ordreAffichage;

    @Column(name = "est_actif")
    private Boolean estActif = true;
}