package com.example.studentmanagement.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.studentmanagement.data.local.entity.Student;

import java.util.List;

@Dao
public interface StudentDao {
    @Insert
    long insert(Student student);
    
    @Update
    void update(Student student);
    
    @Delete
    void delete(Student student);
    
    @Query("DELETE FROM students")
    void deleteAll();
    
    @Query("SELECT * FROM students ORDER BY nom ASC")
    LiveData<List<Student>> getAllStudents();
    
    @Query("SELECT * FROM students WHERE id = :id")
    LiveData<Student> getStudentById(int id);
    
    @Query("SELECT * FROM students WHERE nom LIKE '%' || :searchQuery || '%' OR prenom LIKE '%' || :searchQuery || '%'")
    LiveData<List<Student>> searchStudents(String searchQuery);
}