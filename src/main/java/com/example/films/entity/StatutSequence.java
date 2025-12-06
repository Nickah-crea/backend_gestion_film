package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "statuts_sequence")
@Getter
@Setter
public class StatutSequence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_statut_sequence")
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "nom_statuts_sequence", nullable = false)
    private String nomStatutsSequence;

    private String description;

    @Column(name = "ordre_affichage", nullable = false)
    private Integer ordreAffichage;

    @Column(name = "est_actif")
    private Boolean estActif = true;
}