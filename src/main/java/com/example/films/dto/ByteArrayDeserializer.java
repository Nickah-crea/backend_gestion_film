package com.example.films.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Base64;


public class ByteArrayDeserializer extends JsonDeserializer<byte[]> {

    @Override
    public byte[] deserialize(JsonParser p, DeserializationContext ctxt) 
            throws IOException, JsonProcessingException {
        
        JsonNode node = p.getCodec().readTree(p);
        
        // Si c'est une chaîne Base64 (approche recommandée)
        if (node.isTextual()) {
            String base64String = node.asText();
            try {
                return Base64.getDecoder().decode(base64String);
            } catch (IllegalArgumentException e) {
                throw new IOException("Invalid Base64 format", e);
            }
        }
        
        // Si c'est un tableau (fallback)
        if (node.isArray()) {
            byte[] result = new byte[node.size()];
            for (int i = 0; i < node.size(); i++) {
                result[i] = (byte) node.get(i).asInt();
            }
            return result;
        }
        
        // Si c'est un objet, essayer d'extraire les données
        if (node.isObject()) {
            // Essayer de trouver un champ data ou pdfData
            JsonNode dataNode = node.get("data");
            if (dataNode != null && dataNode.isTextual()) {
                String base64String = dataNode.asText();
                return Base64.getDecoder().decode(base64String);
            }
        }
        
        throw new IOException("Cannot deserialize byte[] from: " + node.getNodeType());
    }
}