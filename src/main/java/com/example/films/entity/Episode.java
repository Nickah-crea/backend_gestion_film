// Episode.java
package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "episodes")
@Getter
@Setter
public class Episode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_episode")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_projet", referencedColumnName = "id_projet")
    private Projet projet;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false)
    private Integer ordre;

    @Column(columnDefinition = "TEXT")
    private String synopsis;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;

    @UpdateTimestamp
    @Column(name = "modifie_le")
    private LocalDateTime modifieLe;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @OneToMany(mappedBy = "episode", cascade = CascadeType.ALL)
    private List<EpisodeStatut> statuts;

    @Transient
    private String statutNom;
}