package com.example.studentmanagement.data.local.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "matieres",
        indices = {
            @Index(value = "label", unique = true)
        })
public class Matiere {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String label;
    private double coefficient;

    public Matiere(String label, double coefficient) {
        this.label = label;
        this.coefficient = coefficient;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public double getCoefficient() { return coefficient; }
    public void setCoefficient(double coefficient) { this.coefficient = coefficient; }
}