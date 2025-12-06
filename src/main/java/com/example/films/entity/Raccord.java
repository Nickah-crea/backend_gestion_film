package com.example.films.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "raccords")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Raccord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_raccord")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scene_source_id")
    private Scene sceneSource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scene_cible_id")
    private Scene sceneCible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_raccord")
    private TypeRaccord typeRaccord;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "est_critique")
    private Boolean estCritique = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_personnage")
    private Personnage personnage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comedien")
    private Comedien comedien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_statut_raccord")
    private StatutRaccord statutRaccord;

    //  @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "id_planning_tournage")
    // private PlanningTournage planningTournage;

    @Column(name = "est_raccord_replanification")
    private Boolean estRaccordReplanification = false;

    @OneToMany(mappedBy = "raccord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RaccordImage> images = new ArrayList<>();

    @Column(name = "cree_le")
    private LocalDateTime creeLe = LocalDateTime.now();

    @Column(name = "modifie_le")
    private LocalDateTime modifieLe = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.modifieLe = LocalDateTime.now();
    }
}