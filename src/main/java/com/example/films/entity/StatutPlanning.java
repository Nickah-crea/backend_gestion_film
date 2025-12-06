package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "statuts_planning")
@Getter
@Setter
public class StatutPlanning {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_statut_planning")
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(name = "nom_statut", nullable = false)
    private String nomStatut;

    private String description;

    @Column(name = "ordre_affichage")
    private Integer ordreAffichage;

    @Column(name = "est_actif")
    private Boolean estActif = true;
}
