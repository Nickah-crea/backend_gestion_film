package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "episode_scenaristes")
@Getter
@Setter
public class EpisodeScenariste {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_episode_scenariste")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_episode", referencedColumnName = "id_episode")
    private Episode episode;

    @ManyToOne
    @JoinColumn(name = "id_scenariste", referencedColumnName = "id_scenariste")
    private Scenariste scenariste;

    @Column(name = "role_scenariste")
    private String roleScenariste;

    @Column(name = "pourcentage_contribution")
    private Integer pourcentageContribution;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;
}