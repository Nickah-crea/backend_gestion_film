package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sequence_commentaires")
@Getter
@Setter
public class SequenceCommentaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sequence_commentaire")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_sequence", referencedColumnName = "id_sequence")
    private Sequence sequence;

    @ManyToOne
    @JoinColumn(name = "id_commentaire", referencedColumnName = "id_commentaire")
    private Commentaire commentaire;
}