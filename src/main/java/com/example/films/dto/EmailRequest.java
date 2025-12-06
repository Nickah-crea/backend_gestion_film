package com.example.films.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailRequest {
    private String toEmail;
    private String subject;
    private String message;
    private String attachmentName;

    private String pdfData; 
    
    // Constructeurs
    public EmailRequest() {}
    
    public EmailRequest(String toEmail, String subject, String message, String attachmentName, String pdfData) {
        this.toEmail = toEmail;
        this.subject = subject;
        this.message = message;
        this.attachmentName = attachmentName;
        this.pdfData = pdfData;
    }
    
    // Getters et Setters
    public String getToEmail() { return toEmail; }
    public void setToEmail(String toEmail) { this.toEmail = toEmail; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getAttachmentName() { return attachmentName; }
    public void setAttachmentName(String attachmentName) { this.attachmentName = attachmentName; }
    
    public String getPdfData() { return pdfData; }
    public void setPdfData(String pdfData) { this.pdfData = pdfData; }
    
    @Override
    public String toString() {
        return "EmailRequest{" +
                "toEmail='" + toEmail + '\'' +
                ", subject='" + subject + '\'' +
                ", message='" + message + '\'' +
                ", attachmentName='" + attachmentName + '\'' +
                ", pdfData=" + (pdfData != null ? "base64[" + pdfData.length() + " chars]" : "null") +
                '}';
    }
}