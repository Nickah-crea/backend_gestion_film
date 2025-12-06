package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "scene_lieu")
@Getter
@Setter
public class SceneLieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_scene_lieu")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_scene", referencedColumnName = "id_scene")
    private Scene scene;

    @ManyToOne
    @JoinColumn(name = "id_lieu", referencedColumnName = "id_lieu")
    private Lieu lieu;

     @ManyToOne
    @JoinColumn(name = "id_plateau", referencedColumnName = "id_plateau")
    private Plateau plateau;

    @Column(name = "description_utilisation")
    private String descriptionUtilisation;
}