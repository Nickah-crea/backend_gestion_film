package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "statuts_projet")
@Getter
@Setter
public class StatutProjet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_statut_projet")
    private Long idStatutProjet;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "nom_statuts_projet", nullable = false)
    private String nomStatutsProjet;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "ordre_affichage", nullable = false)
    private Integer ordreAffichage;

    @Column(name = "est_actif")
    private Boolean estActif = true;
}