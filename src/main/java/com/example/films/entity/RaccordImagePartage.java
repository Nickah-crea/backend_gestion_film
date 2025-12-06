package com.example.films.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "raccord_images_partages")
public class RaccordImagePartage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_raccord_image_partage")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "id_raccord")
    private Raccord raccord;
    
    @ManyToOne
    @JoinColumn(name = "id_raccord_image")
    private RaccordImage raccordImage;
    
    @Column(name = "cree_le")
    private LocalDateTime creeLe = LocalDateTime.now();
    
}