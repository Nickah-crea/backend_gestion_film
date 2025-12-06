package com.example.films.dto;

import com.example.films.entity.Utilisateur;

public class LoginResponse {
    private String token;
    private Utilisateur user;
    
    // Constructeurs
    public LoginResponse() {}
    
    public LoginResponse(String token, Utilisateur user) {
        this.token = token;
        this.user = user;
    }
    
    // Getters et Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public Utilisateur getUser() {
        return user;
    }
    
    public void setUser(Utilisateur user) {
        this.user = user;
    }
} 