package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "genres")
@Getter
@Setter
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_genre")
    private Long idGenre;

    @Column(name = "nom_genre", nullable = false)
    private String nomGenre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;
}