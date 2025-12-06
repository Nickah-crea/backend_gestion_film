package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comediens")
@Getter
@Setter
public class Comedien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comedien")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_projet", nullable = false)
    private Projet projet;

    @Column(name = "nom_comedien", nullable = false)
    private String nom;

    private Integer age;

    @Column(unique = true)
    private String email;

    @Column(name = "photo")
    private String photoPath;

    @OneToMany(mappedBy = "comedien", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DisponibiliteComedien> disponibilites = new ArrayList<>();

    @OneToMany(mappedBy = "comedien", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Personnage> personnages = new ArrayList<>();

     @ManyToMany
    @JoinTable(
        name = "comedien_scene",
        joinColumns = @JoinColumn(name = "id_comedien"),
        inverseJoinColumns = @JoinColumn(name = "id_scene")
    )
    private List<Scene> scenes;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;

    @UpdateTimestamp
    @Column(name = "modifie_le")
    private LocalDateTime modifieLe;
}