package com.example.studentmanagement.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.studentmanagement.data.local.entity.Note;
import com.example.studentmanagement.data.repository.StudentRepository;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private final StudentRepository repository;
    
    public NoteViewModel(Application application) {
        super(application);
        repository = new StudentRepository(application);
    }
    
    public LiveData<List<Note>> getNotesByStudent(int studentId) {
        return repository.getNotesByStudent(studentId);
    }
    
    public LiveData<List<Note>> getAllNotes() {
        return repository.getAllNotes();
    }
    
    public LiveData<Note> getNoteById(int id) {
        return repository.getNoteById(id);
    }
    
    public LiveData<Double> getAverageNoteForStudent(int studentId) {
        return repository.getAverageNoteForStudent(studentId);
    }
    
    public void insert(Note note) {
        repository.insertNote(note);
    }
    
    public void update(Note note) {
        repository.updateNote(note);
    }
    
    public void delete(Note note) {
        repository.deleteNote(note);
    }
}