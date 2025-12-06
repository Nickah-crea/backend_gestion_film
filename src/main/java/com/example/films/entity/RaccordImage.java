package com.example.films.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "raccord_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RaccordImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_raccord_image")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_raccord")
    private Raccord raccord;

    @Column(name = "nom_fichier", nullable = false)
    private String nomFichier;

    @Column(name = "chemin_fichier", nullable = false)
    private String cheminFichier;

    @Column(name = "description_image", columnDefinition = "TEXT")
    private String descriptionImage;

    @Column(name = "est_image_reference")
    private Boolean estImageReference = false;

    @Column(name = "cree_le")
    private LocalDateTime creeLe = LocalDateTime.now();

    public String getNomFichier() {
        return nomFichier;
    }
    
    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }
    
    public String getCheminFichier() {
        return cheminFichier;
    }
    
    public void setCheminFichier(String cheminFichier) {
        this.cheminFichier = cheminFichier;
    }
}