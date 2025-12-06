package com.example.films.service;

import com.example.films.dto.EmailRequest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Base64;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmailWithAttachment(EmailRequest emailRequest) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(emailRequest.getToEmail());
        helper.setSubject(emailRequest.getSubject());
        helper.setText(emailRequest.getMessage(), false); 
        
        // Ajouter la pièce jointe PDF depuis Base64
        if (emailRequest.getPdfData() != null && !emailRequest.getPdfData().isEmpty()) {
            try {
                // Décoder Base64 en bytes
                byte[] pdfBytes = Base64.getDecoder().decode(emailRequest.getPdfData());
                
                ByteArrayResource attachment = new ByteArrayResource(pdfBytes) {
                    @Override
                    public String getFilename() {
                        return emailRequest.getAttachmentName();
                    }
                };
                
                helper.addAttachment(emailRequest.getAttachmentName(), attachment, "application/pdf");
                System.out.println("✅ Pièce jointe PDF ajoutée: " + emailRequest.getAttachmentName() + " (" + pdfBytes.length + " bytes)");
                
            } catch (Exception e) {
                System.err.println("❌ Erreur décodage Base64: " + e.getMessage());
                throw new MessagingException("Erreur lors du décodage du PDF", e);
            }
        } else {
            System.err.println("❌ Aucune donnée PDF reçue");
        }
        
        mailSender.send(message);
        System.out.println("✅ Email envoyé avec succès à: " + emailRequest.getToEmail());
    }
}