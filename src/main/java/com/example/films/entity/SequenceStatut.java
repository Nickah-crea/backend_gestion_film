package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "sequence_statuts")
@Getter
@Setter
public class SequenceStatut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sequence_statut")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_sequence", referencedColumnName = "id_sequence")
    private Sequence sequence;

    @ManyToOne
    @JoinColumn(name = "id_statut", referencedColumnName = "id_statut_sequence")
    private StatutSequence statut;

    @Column(name = "date_debut")
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;
}