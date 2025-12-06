package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "projets")
@Getter
@Setter
public class Projet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_projet")
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String synopsis;

    @ManyToOne
    @JoinColumn(name = "id_genre", referencedColumnName = "id_genre")
    private Genre genre;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;

    @UpdateTimestamp
    @Column(name = "modifie_le")
    private LocalDateTime modifieLe;

    
    @Transient
    private String statutNom;
}