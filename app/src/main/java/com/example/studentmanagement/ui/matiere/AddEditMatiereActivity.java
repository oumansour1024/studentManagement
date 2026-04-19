package com.example.studentmanagement.ui.matiere;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.studentmanagement.viewmodel.MatiereViewModel;
import com.google.android.material.textfield.TextInputLayout;

public class AddEditMatiereActivity extends AppCompatActivity {
    private TextInputLayout tilLabel, tilCoefficient;
    private EditText etLabel, etCoefficient;
    private Button btnSave, btnCancel;
    private MatiereViewModel matiereViewModel;
    private int matiereId = -1;
    private Matiere currentMatiere;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_matiere);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_edit_matiere), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        initViews();
        setupToolbar();
        initViewModel();
        
        Intent intent = getIntent();
        if (intent.hasExtra("matiere_id")) {
            matiereId = intent.getIntExtra("matiere_id", -1);
            setTitle("Edit Subject");
            loadMatiere();
        } else {
            setTitle("Add Subject");
        }
        
        btnSave.setOnClickListener(v -> saveMatiere());
        btnCancel.setOnClickListener(v -> finish());
    }
    
    private void initViews() {
        tilLabel = findViewById(R.id.tilLabel);
        tilCoefficient = findViewById(R.id.tilCoefficient);
        etLabel = findViewById(R.id.etLabel);
        etCoefficient = findViewById(R.id.etCoefficient);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        toolbar = findViewById(R.id.toolbar);
    }
    
    private void initViewModel() {
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
    private void loadMatiere() {
        matiereViewModel.getMatiereById(matiereId).observe(this, matiere -> {
            if (matiere != null) {
                currentMatiere = matiere;
                etLabel.setText(matiere.getLabel());
                etCoefficient.setText(String.valueOf(matiere.getCoefficient()));
            }
        });
    }
    
    private void saveMatiere() {
        String label = etLabel.getText().toString().trim();
        String coefficientStr = etCoefficient.getText().toString().trim();
        
        if (!validateInput(label, coefficientStr)) {
            return;
        }
        
        double coefficient = Double.parseDouble(coefficientStr);
        
        if (matiereId == -1) {
            // Add new subject
            Matiere matiere = new Matiere(label, coefficient);
            matiereViewModel.insert(matiere);
            Toast.makeText(this, "Subject added successfully", Toast.LENGTH_SHORT).show();
        } else {
            // Update existing subject
            if (currentMatiere != null) {
                currentMatiere.setLabel(label);
                currentMatiere.setCoefficient(coefficient);
                matiereViewModel.update(currentMatiere);
                Toast.makeText(this, "Subject updated successfully", Toast.LENGTH_SHORT).show();
            }
        }
        
        finish();
    }
    
    private boolean validateInput(String label, String coefficientStr) {
        if (label.isEmpty()) {
            tilLabel.setError("Subject name is required");
            return false;
        }
        tilLabel.setError(null);
        
        if (coefficientStr.isEmpty()) {
            tilCoefficient.setError("Coefficient is required");
            return false;
        }
        
        try {
            double coefficient = Double.parseDouble(coefficientStr);
            if (coefficient <= 0) {
                tilCoefficient.setError("Coefficient must be greater than 0");
                return false;
            }
            if (coefficient > 10) {
                tilCoefficient.setError("Coefficient cannot exceed 10");
                return false;
            }
        } catch (NumberFormatException e) {
            tilCoefficient.setError("Invalid coefficient");
            return false;
        }
        
        tilCoefficient.setError(null);
        return true;
    }
}