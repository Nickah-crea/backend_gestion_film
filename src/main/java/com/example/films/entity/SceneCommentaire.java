package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "scene_commentaires")
@Getter
@Setter
public class SceneCommentaire {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_scene_commentaire")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "id_scene", referencedColumnName = "id_scene")
    private Scene scene;
    
    @ManyToOne
    @JoinColumn(name = "id_commentaire", referencedColumnName = "id_commentaire")
    private Commentaire commentaire;
}