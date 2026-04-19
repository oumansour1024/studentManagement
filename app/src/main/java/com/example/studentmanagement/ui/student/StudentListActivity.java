package com.example.studentmanagement.ui.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.studentmanagement.R;
import com.example.studentmanagement.data.local.entity.Student;
import com.example.studentmanagement.utils.PermissionHelper;
import com.example.studentmanagement.viewmodel.StudentViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.net.Uri;


public class StudentListActivity extends AppCompatActivity {
    private StudentViewModel studentViewModel;
    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    private FloatingActionButton fabAddStudent;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.student_list), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        try {
            initViews();
            setupToolbar();
            setupRecyclerView();
            setupViewModel();
            setupSwipeRefresh();
            
            fabAddStudent.setOnClickListener(v -> {
                Intent intent = new Intent(this, AddEditStudentActivity.class);
                startActivity(intent);
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        fabAddStudent = findViewById(R.id.fabAdd);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        tvEmptyView = findViewById(R.id.tvEmptyView);
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Students");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }
    
    private void setupRecyclerView() {
        adapter = new StudentAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        
        adapter.setOnItemClickListener(this::showStudentOptionsDialog);
    }
    
    private void setupViewModel() {
        studentViewModel = new ViewModelProvider(this).get(StudentViewModel.class);
        studentViewModel.getAllStudents().observe(this, students -> {
            if (students != null) {
                adapter.setStudents(students);
                
                if (students.isEmpty()) {
                    tvEmptyView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvEmptyView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    
    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(() -> {
            // Refresh data - observe again will trigger automatically
            swipeRefresh.setRefreshing(false);
        });
        
        swipeRefresh.setColorSchemeResources(
            android.R.color.holo_blue_dark,
            android.R.color.holo_blue_light,
            android.R.color.holo_green_light
        );
    }

    private void showStudentOptionsDialog(Student student) {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View view = getLayoutInflater().inflate(R.layout.layout_student_options, null);

        TextView tvName = view.findViewById(R.id.tvStudentName);
        tvName.setText(student.getFullName());

        // 1. Modifier
        view.findViewById(R.id.btnEdit).setOnClickListener(v -> {
            bottomSheet.dismiss();
            Intent intent = new Intent(this, AddEditStudentActivity.class);
            intent.putExtra("student_id", student.getId());
            startActivity(intent);
        });

        // 2. Appeler
        view.findViewById(R.id.btnCall).setOnClickListener(v -> {
            bottomSheet.dismiss();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + student.getTelephone()));
            startActivity(intent);
        });

        // 3. E-mail
        view.findViewById(R.id.btnEmail).setOnClickListener(v -> {
            bottomSheet.dismiss();
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + student.getEmail()));
            startActivity(Intent.createChooser(intent, "Envoyer un mail..."));
        });

        // 4. SMS
        view.findViewById(R.id.btnSms).setOnClickListener(v -> {
            bottomSheet.dismiss();
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + student.getTelephone()));
            startActivity(intent);
        });

        // 5. Carte
        view.findViewById(R.id.btnMap).setOnClickListener(v -> {
            bottomSheet.dismiss();
            String uri = "geo:0,0?q=" + student.getLatitude() + "," + student.getLongitude() + "(" + student.getFullName() + ")";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
        });

        // 6. Supprimer
        view.findViewById(R.id.btnDelete).setOnClickListener(v -> {
            bottomSheet.dismiss();
            confirmDeleteStudent(student);
        });

        bottomSheet.setContentView(view);
        bottomSheet.show();
    }

    private void confirmDeleteStudent(Student student) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Student")
                .setMessage("Are you sure you want to delete " + student.getFullName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    studentViewModel.delete(student);
                    Toast.makeText(this, "Student deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            if (searchView != null) {
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }
                    
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (newText == null || newText.isEmpty()) {
                            studentViewModel.getAllStudents().observe(StudentListActivity.this, 
                                students -> {
                                    if (students != null) {
                                        adapter.setStudents(students);
                                    }
                                });
                        } else {
                            studentViewModel.searchStudents(newText).observe(StudentListActivity.this, 
                                students -> {
                                    if (students != null) {
                                        adapter.setStudents(students);
                                        if (students.isEmpty()) {
                                            tvEmptyView.setVisibility(View.VISIBLE);
                                            tvEmptyView.setText("No matching students found");
                                        } else {
                                            tvEmptyView.setVisibility(View.GONE);
                                        }
                                    }
                                });
                        }
                        return true;
                    }
                });
            }
        }
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}