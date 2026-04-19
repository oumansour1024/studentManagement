package com.example.studentmanagement.ui.note;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.studentmanagement.R;
import com.example.studentmanagement.data.local.entity.Matiere;
import com.example.studentmanagement.data.local.entity.Note;
import com.example.studentmanagement.data.local.entity.Student;
import com.example.studentmanagement.viewmodel.MatiereViewModel;
import com.example.studentmanagement.viewmodel.NoteViewModel;
import com.example.studentmanagement.viewmodel.StudentViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddEditNoteActivity extends AppCompatActivity {
    private Spinner spinnerStudent, spinnerMatiere;
    private EditText etNote;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;
    private NoteViewModel noteViewModel;
    private StudentViewModel studentViewModel;
    private MatiereViewModel matiereViewModel;
    private List<Student> students = new ArrayList<>();
    private List<Matiere> matieres = new ArrayList<>();
    private ArrayAdapter<String> studentAdapter;
    private ArrayAdapter<String> matiereAdapter;
    private int noteId = -1;
    private Note currentNote;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_note);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_edit_note), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupToolbar();
        initViewModels();
        setupSpinners();
        
        noteId = getIntent().getIntExtra("note_id", -1);
        if (noteId != -1) {
            setTitle("Edit Grade");
            loadNote();
        } else {
            setTitle("Add Grade");
        }
        
        btnSave.setOnClickListener(v -> saveNote());
        btnCancel.setOnClickListener(v -> finish());
    }
    
    private void initViews() {
        spinnerStudent = findViewById(R.id.spinnerStudent);
        spinnerMatiere = findViewById(R.id.spinnerMatiere);
        etNote = findViewById(R.id.etNote);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);
        toolbar = findViewById(R.id.toolbar);
    }
    
    private void initViewModels() {
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        studentViewModel = new ViewModelProvider(this).get(StudentViewModel.class);
        matiereViewModel = new ViewModelProvider(this).get(MatiereViewModel.class);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        // Activer le bouton de retour
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Gérer l'action du clic
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
    private void setupSpinners() {
        // Setup Student Spinner
        studentAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new ArrayList<>());
        studentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStudent.setAdapter(studentAdapter);
        
        // Setup Matiere Spinner
        matiereAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new ArrayList<>());
        matiereAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMatiere.setAdapter(matiereAdapter);
        
        // Observe students
        studentViewModel.getAllStudents().observe(this, studentList -> {
            students.clear();
            students.addAll(studentList);
            List<String> studentNames = new ArrayList<>();
            for (Student student : students) {
                studentNames.add(student.getFullName());
            }
            studentAdapter.clear();
            studentAdapter.addAll(studentNames);
            studentAdapter.notifyDataSetChanged();
            
            // If editing, select the correct student after data loads
            if (currentNote != null) {
                selectStudentInSpinner(currentNote.getStudentId());
            }
        });
        
        // Observe matieres
        matiereViewModel.getAllMatieres().observe(this, matiereList -> {
            matieres.clear();
            matieres.addAll(matiereList);
            List<String> matiereLabels = new ArrayList<>();
            for (Matiere matiere : matieres) {
                matiereLabels.add(matiere.getLabel() + " (Coef: " + matiere.getCoefficient() + ")");
            }
            matiereAdapter.clear();
            matiereAdapter.addAll(matiereLabels);
            matiereAdapter.notifyDataSetChanged();
            
            // If editing, select the correct subject after data loads
            if (currentNote != null) {
                selectMatiereInSpinner(currentNote.getMatiereId());
            }
        });
    }
    
    private void loadNote() {
        progressBar.setVisibility(View.VISIBLE);
        
        // Since we don't have a direct getNoteById in ViewModel yet,
        // we'll observe all notes and find the one we need
        noteViewModel.getAllNotes().observe(this, notes -> {
            for (Note note : notes) {
                if (note.getId() == noteId) {
                    currentNote = note;
                    etNote.setText(String.valueOf(note.getNote()));
                    
                    // Select student and subject
                    selectStudentInSpinner(note.getStudentId());
                    selectMatiereInSpinner(note.getMatiereId());
                    
                    progressBar.setVisibility(View.GONE);
                    break;
                }
            }
        });
    }
    
    private void selectStudentInSpinner(int studentId) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId() == studentId) {
                spinnerStudent.setSelection(i);
                break;
            }
        }
    }
    
    private void selectMatiereInSpinner(int matiereId) {
        for (int i = 0; i < matieres.size(); i++) {
            if (matieres.get(i).getId() == matiereId) {
                spinnerMatiere.setSelection(i);
                break;
            }
        }
    }
    
    private void saveNote() {
        int studentPosition = spinnerStudent.getSelectedItemPosition();
        int matierePosition = spinnerMatiere.getSelectedItemPosition();
        
        if (studentPosition < 0 || matierePosition < 0) {
            Toast.makeText(this, "Please select student and subject", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (studentPosition >= students.size() || matierePosition >= matieres.size()) {
            Toast.makeText(this, "Invalid selection", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String noteStr = etNote.getText().toString().trim();
        if (noteStr.isEmpty()) {
            etNote.setError("Grade is required");
            return;
        }
        
        double noteValue;
        try {
            noteValue = Double.parseDouble(noteStr);
            if (noteValue < 0 || noteValue > 20) {
                etNote.setError("Grade must be between 0 and 20");
                return;
            }
        } catch (NumberFormatException e) {
            etNote.setError("Invalid grade");
            return;
        }
        
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);
        
        Student selectedStudent = students.get(studentPosition);
        Matiere selectedMatiere = matieres.get(matierePosition);
        
        if (noteId == -1) {
            // Add new note
            Note note = new Note(selectedStudent.getId(), selectedMatiere.getId(), noteValue);
            noteViewModel.insert(note);
            Toast.makeText(this, "Grade added successfully", Toast.LENGTH_SHORT).show();
        } else {
            // Update existing note
            if (currentNote != null) {
                currentNote.setStudentId(selectedStudent.getId());
                currentNote.setMatiereId(selectedMatiere.getId());
                currentNote.setNote(noteValue);
                noteViewModel.update(currentNote);
                Toast.makeText(this, "Grade updated successfully", Toast.LENGTH_SHORT).show();
            }
        }
        
        progressBar.setVisibility(View.GONE);
        btnSave.setEnabled(true);
        finish();
    }
}