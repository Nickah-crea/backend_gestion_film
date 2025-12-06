package com.example.films.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "statuts_raccord")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatutRaccord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_statut_raccord")
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(name = "nom_statut", nullable = false)
    private String nomStatut;

    @Column(columnDefinition = "TEXT")
    private String description;
}