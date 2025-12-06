package com.example.films.service;

import com.example.films.dto.RaccordExportDTO;
import com.example.films.dto.RaccordImageDTO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PDFExportService {
    
    private final String imageBasePath = "assets/raccords/";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public byte[] generateRaccordsPDF(List<RaccordExportDTO> raccords, String titre) throws IOException {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            // Page de titre
            PDPage titlePage = new PDPage(PDRectangle.A4);
            document.addPage(titlePage);
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, titlePage)) {
                // Titre principal
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("EXPORT DES RACCORDS");
                contentStream.endText();
                
                // Sous-titre
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 14);
                contentStream.newLineAtOffset(50, 720);
                contentStream.showText(titre);
                contentStream.endText();
                
                // Informations générales
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(50, 680);
                contentStream.showText("Nombre de raccords: " + raccords.size());
                contentStream.endText();
            }
            
            // Pages de contenu
            for (RaccordExportDTO raccord : raccords) {
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                addRaccordToPage(document, page, raccord);
            }
            
            document.save(baos);
            return baos.toByteArray();
        }
    }
    
    private void addRaccordToPage(PDDocument document, PDPage page, RaccordExportDTO raccord) throws IOException {
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            float yPosition = 750;
            float margin = 50;
            
            // Titre du raccord
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Raccord: " + raccord.getTypeRaccordNom());
            contentStream.endText();
            yPosition -= 30;
            
            // Informations de base
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            
            addTextLine(contentStream, "Projet: " + raccord.getProjetTitre(), margin, yPosition);
            yPosition -= 15;
            
            if (raccord.getEpisodeTitre() != null) {
                addTextLine(contentStream, "Épisode: " + raccord.getEpisodeTitre(), margin, yPosition);
                yPosition -= 15;
            }
            
            if (raccord.getSequenceTitre() != null) {
                addTextLine(contentStream, "Séquence: " + raccord.getSequenceTitre(), margin, yPosition);
                yPosition -= 15;
            }
            
            addTextLine(contentStream, "Scène Source: " + raccord.getSceneSourceTitre(), margin, yPosition);
            yPosition -= 15;
            
            addTextLine(contentStream, "Scène Cible: " + raccord.getSceneCibleTitre(), margin, yPosition);
            yPosition -= 15;
            
            // Dates de tournage
            if (raccord.getDateTournageSource() != null) {
                addTextLine(contentStream, "Date Tournage Source: " + 
                    raccord.getDateTournageSource().format(dateFormatter), margin, yPosition);
                yPosition -= 15;
            }
            
            if (raccord.getDateTournageCible() != null) {
                addTextLine(contentStream, "Date Tournage Cible: " + 
                    raccord.getDateTournageCible().format(dateFormatter), margin, yPosition);
                yPosition -= 15;
            }
            
            // Personnage et comédien
            if (raccord.getPersonnageNom() != null) {
                addTextLine(contentStream, "Personnage: " + raccord.getPersonnageNom(), margin, yPosition);
                yPosition -= 15;
            }
            
            if (raccord.getComedienNom() != null) {
                addTextLine(contentStream, "Comédien: " + raccord.getComedienNom(), margin, yPosition);
                yPosition -= 15;
            }
            
            // Statut et critique
            addTextLine(contentStream, "Statut: " + raccord.getStatutRaccordNom(), margin, yPosition);
            yPosition -= 15;
            
            addTextLine(contentStream, "Critique: " + (raccord.getEstCritique() ? "OUI" : "NON"), margin, yPosition);
            yPosition -= 20;
            
            // Description
            if (raccord.getDescription() != null) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                addTextLine(contentStream, "Description:", margin, yPosition);
                yPosition -= 15;
                
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                // Gérer le texte multiligne
                String description = raccord.getDescription();
                String[] lines = splitTextIntoLines(description, 100);
                for (String line : lines) {
                    if (yPosition < 100) break; // Éviter le débordement
                    addTextLine(contentStream, line, margin, yPosition);
                    yPosition -= 12;
                }
                yPosition -= 10;
            }
            
            // Images
            if (raccord.hasImages() && yPosition > 200) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                addTextLine(contentStream, "Images de référence:", margin, yPosition);
                yPosition -= 20;
                
                for (RaccordImageDTO image : raccord.getImages()) {
                    if (yPosition < 150) break; // Espace insuffisant pour une image
                    
                    try {
                        Path imagePath = Paths.get(imageBasePath + image.getCheminFichier());
                        if (Files.exists(imagePath)) {
                            BufferedImage bufferedImage = ImageIO.read(imagePath.toFile());
                            PDImageXObject pdImage = LosslessFactory.createFromImage(document, bufferedImage);
                            
                            // Redimensionner l'image pour s'adapter à la page
                            float scale = 0.2f; // Échelle réduite
                            float imageWidth = pdImage.getWidth() * scale;
                            float imageHeight = pdImage.getHeight() * scale;
                            
                            if (yPosition - imageHeight < 50) break; // Vérifier l'espace
                            
                            contentStream.drawImage(pdImage, margin, yPosition - imageHeight, imageWidth, imageHeight);
                            
                            // Légende
                            contentStream.setFont(PDType1Font.HELVETICA, 8);
                            addTextLine(contentStream, image.getNomFichier(), margin, yPosition - imageHeight - 10);
                            
                            yPosition -= (imageHeight + 20);
                        }
                    } catch (Exception e) {
                        // En cas d'erreur de lecture d'image, continuer avec les suivantes
                        System.err.println("Erreur lecture image: " + e.getMessage());
                    }
                }
            }
        }
    }
    
    private void addTextLine(PDPageContentStream contentStream, String text, float x, float y) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }
    
    private String[] splitTextIntoLines(String text, int maxLineLength) {
        if (text == null) return new String[0];
        
        return text.split("(?<=\\G.{" + maxLineLength + "})");
    }
}