
package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "statuts_episode")
@Getter
@Setter
public class StatutEpisode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_statut_episode")
    private Long idStatutEpisode;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "nom_statuts_episode", nullable = false)
    private String nomStatutsEpisode;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "ordre_affichage", nullable = false)
    private Integer ordreAffichage;

    @Column(name = "est_actif")
    private Boolean estActif = true;
}