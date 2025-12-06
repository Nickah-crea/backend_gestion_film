package com.example.films.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "types_raccord")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeRaccord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type_raccord")
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(name = "nom_type", nullable = false)
    private String nomType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "cree_le")
    private LocalDateTime creeLe = LocalDateTime.now();
}