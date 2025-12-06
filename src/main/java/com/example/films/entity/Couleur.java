package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "couleurs")
@Getter
@Setter
public class Couleur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_couleur")
    private Long id;

    @Column(name = "nom", nullable = false, length = 50)
    private String nom;

    @Column(name = "valeur_hex", nullable = false, length = 7)
    private String valeurHex;

    @Column(name = "est_defaut")
    private Boolean estDefaut = false;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;

    @UpdateTimestamp
    @Column(name = "modifie_le")
    private LocalDateTime modifieLe;
}
