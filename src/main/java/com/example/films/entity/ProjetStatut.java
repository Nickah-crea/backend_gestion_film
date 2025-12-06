package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "projet_statuts")
@Getter
@Setter
public class ProjetStatut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_projet_statut")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_projet", referencedColumnName = "id_projet")
    private Projet projet;

    @ManyToOne
    @JoinColumn(name = "id_statut", referencedColumnName = "id_statut_projet")
    private StatutProjet statut;

    @Column(name = "date_debut")
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;
}