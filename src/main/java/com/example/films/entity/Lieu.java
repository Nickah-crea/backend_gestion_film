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
@Table(name = "lieux")
@Getter
@Setter
public class Lieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lieu")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_projet", referencedColumnName = "id_projet")
    private Projet projet;

    @Column(name = "nom_lieu", nullable = false)
    private String nomLieu;

    @Column(name = "type_lieu", nullable = false)
    private String typeLieu;

    private String adresse;

    @OneToMany(mappedBy = "lieu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SceneLieu> sceneLieus = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;

    @UpdateTimestamp
    @Column(name = "modifie_le")
    private LocalDateTime modifieLe;
}