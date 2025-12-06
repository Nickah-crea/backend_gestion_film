package com.example.films.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "disponibilites_comediens")
@Getter
@Setter
public class DisponibiliteComedien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_disponibilite_comedien")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_comedien", nullable = false)
    private Comedien comedien;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String statut;

    // Constructeurs
    public DisponibiliteComedien() {}

    public DisponibiliteComedien(Comedien comedien, LocalDate date, String statut) {
        this.comedien = comedien;
        this.date = date;
        this.statut = statut;
    }

    // Getters et setters si Lombok ne fonctionne pas
    public Comedien getComedien() {
        return comedien;
    }

    public void setComedien(Comedien comedien) {
        this.comedien = comedien;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}