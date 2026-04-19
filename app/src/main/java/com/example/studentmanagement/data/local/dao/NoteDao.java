package com.example.studentmanagement.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.studentmanagement.data.local.entity.Note;

import java.util.List;

@Dao
public interface NoteDao {
    @Insert
    long insert(Note note);
    
    @Update
    void update(Note note);
    
    @Delete
    void delete(Note note);
    
    @Query("SELECT * FROM notes WHERE studentId = :studentId")
    LiveData<List<Note>> getNotesByStudent(int studentId);
    
    @Query("SELECT * FROM notes WHERE matiereId = :matiereId")
    LiveData<List<Note>> getNotesByMatiere(int matiereId);
    
    @Query("SELECT * FROM notes")
    LiveData<List<Note>> getAllNotes();
    
    @Query("SELECT * FROM notes WHERE id = :id")
    LiveData<Note> getNoteById(int id);
    
    @Query("SELECT AVG(note) FROM notes WHERE studentId = :studentId")
    LiveData<Double> getAverageNoteForStudent(int studentId);
    
    @Query("DELETE FROM notes WHERE studentId = :studentId")
    void deleteNotesByStudent(int studentId);
    
    @Query("DELETE FROM notes WHERE matiereId = :matiereId")
    void deleteNotesByMatiere(int matiereId);
}