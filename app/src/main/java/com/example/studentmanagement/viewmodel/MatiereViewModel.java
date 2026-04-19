package com.example.studentmanagement.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.studentmanagement.data.local.entity.Matiere;
import com.example.studentmanagement.data.repository.StudentRepository;

import java.util.List;

public class MatiereViewModel extends AndroidViewModel {
    private final StudentRepository repository;
    private final LiveData<List<Matiere>> allMatieres;
    
    public MatiereViewModel(Application application) {
        super(application);
        repository = new StudentRepository(application);
        allMatieres = repository.getAllMatieres();
    }
    
    public LiveData<List<Matiere>> getAllMatieres() {
        return allMatieres;
    }
    
    public LiveData<Matiere> getMatiereById(int id) {
        return repository.getMatiereById(id);
    }
    
    public void insert(Matiere matiere) {
        repository.insertMatiere(matiere);
    }
    
    public void update(Matiere matiere) {
        repository.updateMatiere(matiere);
    }
    
    public void delete(Matiere matiere) {
        repository.deleteMatiere(matiere);
    }
}