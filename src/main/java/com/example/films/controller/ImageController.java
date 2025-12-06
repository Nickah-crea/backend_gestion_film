package com.example.films.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/images")
@CrossOrigin(origins = "https://backendgestionfilm-production.up.railway.app")
public class ImageController {
    
    private final Path raccordsImagesPath;
    
    public ImageController() {
        // Obtenir le chemin absolu du projet
        String projectRoot = System.getProperty("user.dir");
        System.out.println("📁 Racine du projet: " + projectRoot);
        
        this.raccordsImagesPath = Paths.get(projectRoot, "assets", "raccords");
        System.out.println("📁 Chemin des images: " + raccordsImagesPath.toAbsolutePath());
        
        // Vérifier si le dossier existe
        if (Files.exists(raccordsImagesPath)) {
            System.out.println("✅ Dossier des images trouvé");
        } else {
            System.out.println("❌ Dossier des images NON trouvé: " + raccordsImagesPath.toAbsolutePath());
        }
    }
    
    @GetMapping("/raccord/{filename:.+}")
    public ResponseEntity<Resource> getRaccordImage(@PathVariable String filename) {
        try {
            System.out.println("🔍 Requête reçue pour: " + filename);
            
            // Nettoyer le nom de fichier
            String cleanFilename = filename.replace("..", "").replace("/", "").trim();
            System.out.println("📝 Nom nettoyé: " + cleanFilename);
            
            Path imagePath = raccordsImagesPath.resolve(cleanFilename).normalize();
            System.out.println("📁 Chemin complet: " + imagePath.toAbsolutePath());
            
            // Vérifier si le fichier existe
            if (!Files.exists(imagePath)) {
                System.out.println("❌ Fichier NON trouvé: " + imagePath.getFileName());
                
                // Lister les fichiers disponibles pour debug
                try {
                    System.out.println("📋 Fichiers disponibles dans le dossier:");
                    Files.list(raccordsImagesPath)
                         .forEach(file -> System.out.println("   - " + file.getFileName()));
                } catch (Exception e) {
                    System.out.println("❌ Impossible de lister les fichiers du dossier");
                }
                
                return ResponseEntity.notFound().build();
            }
            
            System.out.println("✅ Fichier trouvé, taille: " + Files.size(imagePath) + " bytes");
            
            Resource resource = new UrlResource(imagePath.toUri());
            
            if (!resource.isReadable()) {
                System.out.println("❌ Fichier non lisible");
                return ResponseEntity.status(403).build();
            }
            
            // Déterminer le type MIME
            String mimeType = Files.probeContentType(imagePath);
            if (mimeType == null) {
                mimeType = "image/jpeg";
                System.out.println("ℹ️ Type MIME par défaut: " + mimeType);
            } else {
                System.out.println("ℹ️ Type MIME détecté: " + mimeType);
            }
            
            System.out.println("✅ Image servie avec succès: " + filename);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + cleanFilename + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            System.err.println("💥 Erreur critique: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/debug")
public ResponseEntity<String> debugInfo() {
    try {
        StringBuilder info = new StringBuilder();
        info.append("Racine du projet: ").append(System.getProperty("user.dir")).append("\n");
        info.append("Chemin images: ").append(raccordsImagesPath.toAbsolutePath()).append("\n");
        info.append("Dossier existe: ").append(Files.exists(raccordsImagesPath)).append("\n");
        
        if (Files.exists(raccordsImagesPath)) {
            info.append("Fichiers dans le dossier:\n");
            Files.list(raccordsImagesPath)
                 .forEach(path -> info.append(" - ").append(path.getFileName()).append("\n"));
        }
        
        return ResponseEntity.ok(info.toString());
    } catch (Exception e) {
        return ResponseEntity.ok("Erreur: " + e.getMessage());
    }
}
}

