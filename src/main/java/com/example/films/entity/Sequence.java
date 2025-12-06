package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "sequences")
@Getter
@Setter
public class Sequence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sequence")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_episode", referencedColumnName = "id_episode")
    private Episode episode;

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