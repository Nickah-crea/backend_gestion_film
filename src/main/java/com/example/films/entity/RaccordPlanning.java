package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "raccord_planning")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RaccordPlanning {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_raccord_planning")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_raccord")
    private Raccord raccord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_planning_tournage")
    private PlanningTournage planningTournage;

    @Column(name = "type_liaison", nullable = false)
    private String typeLiaison;

    @Column(name = "cree_le")
    private LocalDateTime creeLe = LocalDateTime.now();
}