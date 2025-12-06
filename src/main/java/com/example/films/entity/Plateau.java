package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "plateaux")
@Getter
@Setter
public class Plateau {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plateau")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_lieu", referencedColumnName = "id_lieu")
    private Lieu lieu;

    @Column(nullable = false)
    private String nom;

    @Column(name = "type_plateau", nullable = false)
    private String typePlateau;

    private String description;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;

    @UpdateTimestamp
    @Column(name = "modifie_le")
    private LocalDateTime modifieLe;
}