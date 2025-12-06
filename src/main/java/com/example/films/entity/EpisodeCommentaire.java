package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "episode_commentaires")
@Getter
@Setter
public class EpisodeCommentaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_episode_commentaire")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_episode", referencedColumnName = "id_episode")
    private Episode episode;

    @ManyToOne
    @JoinColumn(name = "id_commentaire", referencedColumnName = "id_commentaire")
    private Commentaire commentaire;
}