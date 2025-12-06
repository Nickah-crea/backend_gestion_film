
package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "episode_statuts")
@Getter
@Setter
public class EpisodeStatut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_episode_statut")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_episode", referencedColumnName = "id_episode")
    private Episode episode;

    @ManyToOne
    @JoinColumn(name = "id_statut", referencedColumnName = "id_statut_episode")
    private StatutEpisode statut;

    @Column(name = "date_debut")
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;
}