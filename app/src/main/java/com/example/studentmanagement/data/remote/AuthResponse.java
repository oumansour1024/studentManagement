package com.example.studentmanagement.data.remote;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("success")
    private String success;
    
    @SerializedName("error")
    private String error;
    
    @SerializedName("sessionToken")
    private String sessionToken;
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("nom")
    private String nom;
    
    @SerializedName("prenom")
    private String prenom;

    // Getters and Setters
    public String getSuccess() { 
        return success; 
    }
    
    public void setSuccess(String success) { 
        this.success = success; 
    }
    
    public String getError() { 
        return error; 
    }
    
    public void setError(String error) { 
        this.error = error; 
    }
    
    public String getSessionToken() { 
        return sessionToken; 
    }
    
    public void setSessionToken(String sessionToken) { 
        this.sessionToken = sessionToken; 
    }
    
    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }
    
    public String getNom() { 
        return nom; 
    }
    
    public void setNom(String nom) { 
        this.nom = nom; 
    }
    
    public String getPrenom() { 
        return prenom; 
    }
    
    public void setPrenom(String prenom) { 
        this.prenom = prenom; 
    }
    
    // Helper methods
    public boolean isSuccessful() {
        return success != null && !success.isEmpty();
    }
    
    public String getMessage() {
        if (success != null) return success;
        if (error != null) return error;
        return "";
    }
    
    public String getFullName() {
        return prenom + " " + nom;
    }
}