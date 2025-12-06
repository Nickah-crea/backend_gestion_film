package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "scene_plateau")
@Getter
@Setter
public class ScenePlateau {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_scene_plateau")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_scene", referencedColumnName = "id_scene")
    private Scene scene;

    @ManyToOne
    @JoinColumn(name = "id_plateau", referencedColumnName = "id_plateau")
    private Plateau plateau;

    @Column(name = "description_utilisation")
    private String descriptionUtilisation;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;
}