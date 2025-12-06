package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "dialogue_surlignages")
@Getter
@Setter
public class DialogueSurlignage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dialogue_surlignages")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dialogue_id", nullable = false)
    private Dialogue dialogue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "couleur_id", nullable = false)
    private Couleur couleur;

    @Column(name = "utilisateur_id", nullable = false)
    private Long utilisateurId;

    @Column(name = "texte_surligne", nullable = false, length = 1000)
    private String texteSurligne;

    @Column(name = "debut_index", nullable = false)
    private Integer debutIndex;

    @Column(name = "fin_index", nullable = false)
    private Integer finIndex;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;
}

