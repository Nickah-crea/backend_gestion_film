package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "realisateurs")
@Getter
@Setter
public class Realisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_realisateur")
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_utilisateur", referencedColumnName = "id_utilisateur")
    private Utilisateur utilisateur;

    private String specialite;
    
    @Column(columnDefinition = "TEXT")
    private String biographie;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;

    @UpdateTimestamp
    @Column(name = "modifie_le")
    private LocalDateTime modifieLe;

    @OneToMany(mappedBy = "realisateur", cascade = CascadeType.ALL)
    private List<EpisodeRealisateur> episodeRealisateurs;
}