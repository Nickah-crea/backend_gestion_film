package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "commentaires")
@Getter
@Setter
public class Commentaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_commentaire")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_utilisateur", referencedColumnName = "id_utilisateur")
    private Utilisateur utilisateur;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @CreationTimestamp
    @Column(name = "cree_le", updatable = false)
    private LocalDateTime creeLe;

    @UpdateTimestamp
    @Column(name = "modifie_le")
    private LocalDateTime modifieLe;
}