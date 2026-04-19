package com.example.studentmanagement.data.local.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "students",
        indices = {
            @Index(value = "nom"),
            @Index(value = "prenom"),
            @Index(value = "email", unique = true)
        })
public class Student {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String address;
    private double latitude;
    private double longitude;

    public Student(String nom, String prenom, String telephone, String email, String address, double latitude, double longitude) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    public String getFullName() {
        return (nom != null ? nom.toUpperCase() : "") + " " + capitalize(prenom);
    }


}