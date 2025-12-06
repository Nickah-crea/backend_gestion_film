package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "dialogues")
@Getter
@Setter
public class Dialogue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dialogue")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_scene", referencedColumnName = "id_scene")
    private Scene scene;

    @ManyToOne
    @JoinColumn(name = "id_personnage", referencedColumnName = "id_personnage")
    private Personnage personnage;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String texte;

    @Column(nullable = false)
    private Integer ordre;

    @Column(columnDefinition = "TEXT")
    private String observation;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;

    @UpdateTimestamp
    @Column(name = "modifie_le")
    private LocalDateTime modifieLe;
}

