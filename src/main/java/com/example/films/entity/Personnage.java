package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "personnages")
@Getter
@Setter
public class Personnage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_personnage")
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "id_projet", referencedColumnName = "id_projet")
    private Projet projet;

    @ManyToOne
    @JoinColumn(name = "id_comedien", referencedColumnName = "id_comedien")
    private Comedien comedien;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;

    @UpdateTimestamp
    @Column(name = "modifie_le")
    private LocalDateTime modifieLe;
}