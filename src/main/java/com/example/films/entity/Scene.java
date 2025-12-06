package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "scenes")
@Getter
@Setter
public class Scene {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_scene")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_sequence", referencedColumnName = "id_sequence")
    private Sequence sequence;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false)
    private Integer ordre;

    private String synopsis;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;

    @UpdateTimestamp
    @Column(name = "modifie_le")
    private LocalDateTime modifieLe;

}