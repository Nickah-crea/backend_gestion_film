package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "comedien_scene")
@Getter
@Setter
public class ComedienScene {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comedien_scene")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_scene", nullable = false)
    private Scene scene;

    @ManyToOne
    @JoinColumn(name = "id_comedien", nullable = false)
    private Comedien comedien;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;
}