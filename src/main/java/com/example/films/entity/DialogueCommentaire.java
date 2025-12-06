package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dialogue_commentaires")
@Getter
@Setter
public class DialogueCommentaire {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dialogue_commentaire")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "id_dialogue", referencedColumnName = "id_dialogue")
    private Dialogue dialogue;
    
    @ManyToOne
    @JoinColumn(name = "id_commentaire", referencedColumnName = "id_commentaire")
    private Commentaire commentaire;
}