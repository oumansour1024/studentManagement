package com.example.studentmanagement.data.local.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes",
        foreignKeys = {
            @ForeignKey(entity = Student.class,
                    parentColumns = "id",
                    childColumns = "studentId",
                    onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = Matiere.class,
                    parentColumns = "id",
                    childColumns = "matiereId",
                    onDelete = ForeignKey.CASCADE)
        },
        indices = {
            @Index(value = "studentId"),
            @Index(value = "matiereId"),
            @Index(value = {"studentId", "matiereId"}, unique = true)
        })
public class Note {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int studentId;
    private int matiereId;
    private double note;

    public Note(int studentId, int matiereId, double note) {
        this.studentId = studentId;
        this.matiereId = matiereId;
        this.note = note;
    }

    // Getters and Setters
    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }
    
    public int getStudentId() { 
        return studentId; 
    }
    
    public void setStudentId(int studentId) { 
        this.studentId = studentId; 
    }
    
    public int getMatiereId() { 
        return matiereId; 
    }
    
    public void setMatiereId(int matiereId) { 
        this.matiereId = matiereId; 
    }
    
    public double getNote() { 
        return note; 
    }
    
    public void setNote(double note) { 
        this.note = note; 
    }
}