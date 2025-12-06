package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "episode_realisateurs")
@Getter
@Setter
public class EpisodeRealisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_episode_realisateur")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_episode", referencedColumnName = "id_episode")
    private Episode episode;

    @ManyToOne
    @JoinColumn(name = "id_realisateur", referencedColumnName = "id_realisateur")
    private Realisateur realisateur;

    @Column(name = "role_realisateur")
    private String roleRealisateur;

    @Column(name = "pourcentage_contribution")
    private Integer pourcentageContribution;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;
}