package com.example.studentmanagement.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.studentmanagement.data.local.entity.Student;
import com.example.studentmanagement.data.repository.StudentRepository;

import java.util.List;

public class StudentViewModel extends AndroidViewModel {
    private final StudentRepository repository;
    private final LiveData<List<Student>> allStudents;
    
    public StudentViewModel(Application application) {
        super(application);
        repository = new StudentRepository(application);
        allStudents = repository.getAllStudents();
    }
    
    public LiveData<List<Student>> getAllStudents() {
        return allStudents;
    }
    
    public LiveData<Student> getStudentById(int id) {
        return repository.getStudentById(id);
    }
    
    public LiveData<List<Student>> searchStudents(String query) {
        return repository.searchStudents(query);
    }
    
    public void insert(Student student) {
        repository.insertStudent(student);
    }
    
    public void update(Student student) {
        repository.updateStudent(student);
    }
    
    public void delete(Student student) {
        repository.deleteStudent(student);
    }
}