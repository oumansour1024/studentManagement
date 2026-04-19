package com.example.studentmanagement.ui.matiere;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.example.studentmanagement.viewmodel.MatiereViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MatiereListActivity extends AppCompatActivity {
    private MatiereViewModel matiereViewModel;
    private RecyclerView recyclerView;
    private MatiereAdapter adapter;
    private FloatingActionButton fabAddMatiere;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_matiere_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.matiere_list), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        setupToolbar();
        setupRecyclerView();
        setupViewModel();
        setupSwipeRefresh();
        
        fabAddMatiere.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditMatiereActivity.class);
            startActivity(intent);
        });
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        fabAddMatiere = findViewById(R.id.fabAdd);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        tvEmptyView = findViewById(R.id.tvEmptyView);
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Subjects");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }
    
    private void setupRecyclerView() {
        adapter = new MatiereAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        
        // Set Edit button click listener
        adapter.setOnEditClickListener(this::editMatiere);
        
        // Set Delete button click listener
        adapter.setOnDeleteClickListener(this::confirmDeleteMatiere);
        
        // Optional: Item click
        adapter.setOnItemClickListener(this::showMatiereOptionsDialog);
    }
    
    private void setupViewModel() {
        matiereViewModel = new ViewModelProvider(this).get(MatiereViewModel.class);
        matiereViewModel.getAllMatieres().observe(this, matieres -> {
            adapter.setMatieres(matieres);
            
            if (matieres == null || matieres.isEmpty()) {
                tvEmptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                tvEmptyView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }
    
    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(() -> {
            matiereViewModel.getAllMatieres().observe(this, matieres -> {
                adapter.setMatieres(matieres);
                swipeRefresh.setRefreshing(false);
            });
        });
        
        swipeRefresh.setColorSchemeResources(
            R.color.primary_light,
            R.color.primary_dark,
            R.color.accent_light

        );
    }
    
    private void showMatiereOptionsDialog(Matiere matiere) {
        String[] options = {"Edit", "Delete"};
        
        new AlertDialog.Builder(this)
                .setTitle(matiere.getLabel())
                .setMessage("Coefficient: " + matiere.getCoefficient())
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Edit
                            editMatiere(matiere);
                            break;
                        case 1: // Delete
                            confirmDeleteMatiere(matiere);
                            break;
                    }
                })
                .show();
    }
    
    private void editMatiere(Matiere matiere) {
        Intent intent = new Intent(MatiereListActivity.this, AddEditMatiereActivity.class);
        intent.putExtra("matiere_id", matiere.getId());
        startActivity(intent);
    }
    
    private void confirmDeleteMatiere(Matiere matiere) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Subject")
                .setMessage("Are you sure you want to delete \"" + matiere.getLabel() + "\"?\n\nThis will also delete all grades associated with this subject.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    matiereViewModel.delete(matiere);
                    Toast.makeText(this, "Subject deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}