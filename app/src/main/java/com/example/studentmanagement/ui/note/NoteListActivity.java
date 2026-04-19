package com.example.studentmanagement.ui.note;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.studentmanagement.R;
import com.example.studentmanagement.data.local.entity.Matiere;
import com.example.studentmanagement.data.local.entity.Note;
import com.example.studentmanagement.data.local.entity.Student;
import com.example.studentmanagement.viewmodel.MatiereViewModel;
import com.example.studentmanagement.viewmodel.NoteViewModel;
import com.example.studentmanagement.viewmodel.StudentViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class NoteListActivity extends AppCompatActivity {
    private NoteViewModel noteViewModel;
    private StudentViewModel studentViewModel;
    private MatiereViewModel matiereViewModel;
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private FloatingActionButton fabAddNote;
    private Spinner spinnerStudent;
    private TextView tvAverage, tvEmptyView;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefresh;
    private List<Student> students = new ArrayList<>();
    private List<Matiere> matieres = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.note_list), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        setupToolbar();
        setupRecyclerView();
        setupViewModels();
        setupSpinner();
        setupSwipeRefresh();

        fabAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditNoteActivity.class);
            startActivity(intent);
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        fabAddNote = findViewById(R.id.fabAdd);
        spinnerStudent = findViewById(R.id.spinnerStudent);
        tvAverage = findViewById(R.id.tvAverage);
        tvEmptyView = findViewById(R.id.tvEmptyView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Grades");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        noteAdapter = new NoteAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(noteAdapter);
        
        // Set Edit button click listener
        noteAdapter.setOnEditClickListener(note -> {
            editNote(note);
        });
        
        // Set Delete button click listener
        noteAdapter.setOnDeleteClickListener(note -> {
            String studentName = getStudentName(note.getStudentId());
            String matiereName = getMatiereName(note.getMatiereId());
            confirmDeleteNote(note, studentName, matiereName);
        });
        
        // Optional: Item click for view details
        noteAdapter.setOnItemClickListener(note -> {
            String studentName = getStudentName(note.getStudentId());
            String matiereName = getMatiereName(note.getMatiereId());
            showNoteDetails(note, studentName, matiereName);
        });
    }

    private void setupViewModels() {
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        studentViewModel = new ViewModelProvider(this).get(StudentViewModel.class);
        matiereViewModel = new ViewModelProvider(this).get(MatiereViewModel.class);

        studentViewModel.getAllStudents().observe(this, studentList -> {
            students = studentList;
            setupSpinnerData();
        });

        matiereViewModel.getAllMatieres().observe(this, matiereList -> {
            matieres = matiereList;
        });

        noteViewModel.getAllNotes().observe(this, notes -> {
            noteAdapter.setNotes(notes, students, matieres);

            if (notes == null || notes.isEmpty()) {
                tvEmptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                tvEmptyView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupSpinner() {
        spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStudent.setAdapter(spinnerAdapter);

        spinnerStudent.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    noteViewModel.getAllNotes().observe(NoteListActivity.this, notes -> {
                        noteAdapter.setNotes(notes, students, matieres);
                        tvAverage.setText("Overall Average: --");
                    });
                } else if (position > 0 && position <= students.size()) {
                    Student selectedStudent = students.get(position - 1);
                    noteViewModel.getNotesByStudent(selectedStudent.getId()).observe(NoteListActivity.this, notes -> {
                        noteAdapter.setNotes(notes, students, matieres);
                    });
                    noteViewModel.getAverageNoteForStudent(selectedStudent.getId()).observe(NoteListActivity.this, average -> {
                        if (average != null) {
                            tvAverage.setText(String.format("Average: %.2f / 20", average));
                        } else {
                            tvAverage.setText("Average: --");
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
    }

    private void setupSpinnerData() {
        List<String> studentNames = new ArrayList<>();
        studentNames.add("All Students");
        for (Student student : students) {
            studentNames.add(student.getFullName());
        }

        spinnerAdapter.clear();
        spinnerAdapter.addAll(studentNames);
        spinnerAdapter.notifyDataSetChanged();
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(() -> {
            noteViewModel.getAllNotes().observe(this, notes -> {
                noteAdapter.setNotes(notes, students, matieres);
                swipeRefresh.setRefreshing(false);
            });
        });

        swipeRefresh.setColorSchemeResources(
                R.color.primary_light,
                R.color.primary_dark,
                R.color.accent_light
        );
    }

    private void showNoteOptionsDialog(Note note) {
        String studentName = getStudentName(note.getStudentId());
        String matiereName = getMatiereName(note.getMatiereId());

        String[] options = {"Edit", "Delete", "View Details"};

        new AlertDialog.Builder(this)
                .setTitle("Grade: " + String.format("%.2f / 20", note.getNote()))
                .setMessage("Student: " + studentName + "\nSubject: " + matiereName)
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Edit
                            editNote(note);
                            break;
                        case 1: // Delete
                            confirmDeleteNote(note, studentName, matiereName);
                            break;
                        case 2: // View Details
                            showNoteDetails(note, studentName, matiereName);
                            break;
                    }
                })
                .show();
    }

    private void editNote(Note note) {
        Intent intent = new Intent(NoteListActivity.this, AddEditNoteActivity.class);
        intent.putExtra("note_id", note.getId());
        startActivity(intent);
    }

    private void confirmDeleteNote(Note note, String studentName, String matiereName) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Grade")
                .setMessage("Are you sure you want to delete this grade?\n\n" +
                        "Student: " + studentName + "\n" +
                        "Subject: " + matiereName + "\n" +
                        "Grade: " + String.format("%.2f / 20", note.getNote()))
                .setPositiveButton("Delete", (dialog, which) -> {
                    noteViewModel.delete(note);
                    Toast.makeText(this, "Grade deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showNoteDetails(Note note, String studentName, String matiereName) {
        String gradeStatus;
        if (note.getNote() >= 16) {
            gradeStatus = "Excellent";
        } else if (note.getNote() >= 14) {
            gradeStatus = "Very Good";
        } else if (note.getNote() >= 12) {
            gradeStatus = "Good";
        } else if (note.getNote() >= 10) {
            gradeStatus = "Satisfactory";
        } else {
            gradeStatus = "Insufficient";
        }

        new AlertDialog.Builder(this)
                .setTitle("Grade Details")
                .setMessage("Student: " + studentName + "\n" +
                        "Subject: " + matiereName + "\n" +
                        "Grade: " + String.format("%.2f / 20", note.getNote()) + "\n" +
                        "Status: " + gradeStatus)
                .setPositiveButton("OK", null)
                .setNeutralButton("Edit", (dialog, which) -> editNote(note))
                .show();
    }

    private String getStudentName(int studentId) {
        for (Student student : students) {
            if (student.getId() == studentId) {
                return student.getFullName();
            }
        }
        return "Unknown Student";
    }

    private String getMatiereName(int matiereId) {
        for (Matiere matiere : matieres) {
            if (matiere.getId() == matiereId) {
                return matiere.getLabel();
            }
        }
        return "Unknown Subject";
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}