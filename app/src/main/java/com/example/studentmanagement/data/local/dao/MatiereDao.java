package com.example.studentmanagement.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.studentmanagement.data.local.entity.Matiere;

import java.util.List;

@Dao
public interface MatiereDao {
    @Insert
    long insert(Matiere matiere);
    
    @Update
    void update(Matiere matiere);
    
    @Delete
    void delete(Matiere matiere);
    
    @Query("SELECT * FROM matieres ORDER BY label ASC")
    LiveData<List<Matiere>> getAllMatieres();
    
    @Query("SELECT * FROM matieres WHERE id = :id")
    LiveData<Matiere> getMatiereById(int id);
}