package com.example.studentmanagement.ui.student;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.studentmanagement.R;
import com.example.studentmanagement.data.local.entity.Student;
import com.example.studentmanagement.utils.LocationHelper;
import com.example.studentmanagement.viewmodel.StudentViewModel;
import com.google.android.material.textfield.TextInputLayout;

public class AddEditStudentActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    
    private TextInputLayout tilNom, tilPrenom, tilTelephone, tilEmail;
    private EditText etNom, etPrenom, etTelephone, etEmail, etAddress;
    private Button btnSave, btnGetLocation;
    private ProgressBar progressBar;
    private StudentViewModel studentViewModel;
    private LocationHelper locationHelper;
    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;
    private int studentId = -1;
    private Student currentStudent;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_student);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_edit_student), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupToolbar();
        initViewModel();
        initLocationHelper();
        
        Intent intent = getIntent();
        if (intent.hasExtra("student_id")) {
            studentId = intent.getIntExtra("student_id", -1);
            setTitle("Edit Student");
            loadStudent();
        } else {
            setTitle("Add Student");
        }
        
        setupClickListeners();
    }
    
    private void initViews() {

        tilNom = findViewById(R.id.tilNom);
        tilPrenom = findViewById(R.id.tilPrenom);
        tilTelephone = findViewById(R.id.tilTelephone);
        tilEmail = findViewById(R.id.tilEmail);
        
        etNom = findViewById(R.id.etNom);
        etPrenom = findViewById(R.id.etPrenom);
        etTelephone = findViewById(R.id.etTelephone);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        
        btnSave = findViewById(R.id.btnSave);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        progressBar = findViewById(R.id.progressBar);

        toolbar = findViewById(R.id.toolbar);
    }
    
    private void initViewModel() {
        studentViewModel = new ViewModelProvider(this).get(StudentViewModel.class);
    }
    
    private void initLocationHelper() {
        locationHelper = new LocationHelper(this);
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



    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveStudent());
        
        btnGetLocation.setOnClickListener(v -> {
            // Check if location services are enabled first
            if (!isLocationEnabled()) {
                promptEnableGPS();
                return;
            }
            
            // Then check permissions
            if (checkLocationPermission()) {
                getLocation();
            } else {
                requestLocationPermission();
            }
        });
    }
    
    /**
     * Check if location services (GPS or Network) are enabled
     */
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        if (locationManager == null) {
            return false;
        }
        
        boolean gpsEnabled = false;
        boolean networkEnabled = false;
        
        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return gpsEnabled || networkEnabled;
    }
    
    /**
     * Prompt user to enable GPS
     */
    private void promptEnableGPS() {
        new AlertDialog.Builder(this)
                .setTitle("Location Services Required")
                .setMessage("GPS or Network location is required to get your current address. Please enable location services.")
                .setPositiveButton("Open Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("Enter Manually", (dialog, which) -> {
                    Toast.makeText(this, "Please enter address manually", Toast.LENGTH_SHORT).show();
                    etAddress.requestFocus();
                })
                .setCancelable(false)
                .show();
    }
    
    /**
     * Check location permission
     */
    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }
    
    /**
     * Request location permission
     */
    private void requestLocationPermission() {
        // Show rationale if needed
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, 
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs location permission to get your current address.")
                    .setPositiveButton("Grant", (dialog, which) -> {
                        ActivityCompat.requestPermissions(this,
                                new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                },
                                LOCATION_PERMISSION_REQUEST_CODE);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    
    /**
     * Get current location
     */
    private void getLocation() {
        // Double-check location services are still enabled
        if (!isLocationEnabled()) {
            promptEnableGPS();
            return;
        }
        
        progressBar.setVisibility(View.VISIBLE);
        btnGetLocation.setEnabled(false);
        btnGetLocation.setText("Getting Location...");
        
        locationHelper.getCurrentLocation(new LocationHelper.LocationHelperCallback() {
            @Override
            public void onLocationReceived(double latitude, double longitude, String address) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnGetLocation.setEnabled(true);
                    btnGetLocation.setText("Get Current Location");
                    currentLatitude = latitude;
                    currentLongitude = longitude;
                    etAddress.setText(address);
                    Toast.makeText(AddEditStudentActivity.this, 
                        "Location captured successfully", Toast.LENGTH_SHORT).show();
                });
            }
            
            @Override
            public void onLocationError(String error) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnGetLocation.setEnabled(true);
                    btnGetLocation.setText("Get Current Location");
                    
                    // Show error and offer manual entry
                    new AlertDialog.Builder(AddEditStudentActivity.this)
                            .setTitle("Location Error")
                            .setMessage(error + "\n\nWould you like to enter address manually?")
                            .setPositiveButton("Enter Manually", (dialog, which) -> {
                                etAddress.requestFocus();
                            })
                            .setNegativeButton("Try Again", (dialog, which) -> {
                                getLocation();
                            })
                            .show();
                });
            }
        });
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted - check location services again
                if (!isLocationEnabled()) {
                    promptEnableGPS();
                } else {
                    Toast.makeText(this, "Permission granted! Click 'Get Location' again", 
                        Toast.LENGTH_LONG).show();
                }
            } else {
                // Permission denied
                new AlertDialog.Builder(this)
                        .setTitle("Permission Denied")
                        .setMessage("Location permission is required for GPS feature. You can enter address manually.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            etAddress.requestFocus();
                        })
                        .show();
            }
        }
        
        // Also pass to location helper
        if (locationHelper != null) {
            locationHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    
    /**
     * Called when returning from settings screen
     */
    @Override
    protected void onResume() {
        super.onResume();
        
        // Check if location was enabled while away
        if (isLocationEnabled() && checkLocationPermission()) {
            btnGetLocation.setEnabled(true);
        }
    }
    
    private void loadStudent() {
        studentViewModel.getStudentById(studentId).observe(this, student -> {
            if (student != null) {
                currentStudent = student;
                etNom.setText(student.getNom());
                etPrenom.setText(student.getPrenom());
                etTelephone.setText(student.getTelephone());
                etEmail.setText(student.getEmail());
                etAddress.setText(student.getAddress());
                currentLatitude = student.getLatitude();
                currentLongitude = student.getLongitude();
            }
        });
    }
    
    private void saveStudent() {
        String nom = etNom.getText().toString().trim();
        String prenom = etPrenom.getText().toString().trim();
        String telephone = etTelephone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        
        if (!validateInput(nom, prenom, telephone, email, address)) {
            return;
        }
        
        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);
        
        if (studentId == -1) {
            // Add new student
            Student student = new Student(nom, prenom, telephone, email, address, 
                    currentLatitude, currentLongitude);
            studentViewModel.insert(student);
            Toast.makeText(this, "Student added successfully", Toast.LENGTH_SHORT).show();
        } else {
            // Update existing student
            if (currentStudent != null) {
                currentStudent.setNom(nom);
                currentStudent.setPrenom(prenom);
                currentStudent.setTelephone(telephone);
                currentStudent.setEmail(email);
                currentStudent.setAddress(address);
                currentStudent.setLatitude(currentLatitude);
                currentStudent.setLongitude(currentLongitude);
                studentViewModel.update(currentStudent);
                Toast.makeText(this, "Student updated successfully", Toast.LENGTH_SHORT).show();
            }
        }
        
        progressBar.setVisibility(View.GONE);
        btnSave.setEnabled(true);
        finish();
    }
    
    private boolean validateInput(String nom, String prenom, String telephone, 
                                  String email, String address) {
        if (nom.isEmpty()) {
            tilNom.setError("Last name is required");
            return false;
        }
        tilNom.setError(null);
        
        if (prenom.isEmpty()) {
            tilPrenom.setError("First name is required");
            return false;
        }
        tilPrenom.setError(null);
        
        if (telephone.isEmpty()) {
            tilTelephone.setError("Phone number is required");
            return false;
        }
        if (!telephone.matches("^[0-9+\\-\\s]{8,15}$")) {
            tilTelephone.setError("Invalid phone number");
            return false;
        }
        tilTelephone.setError(null);
        
        if (email.isEmpty()) {
            tilEmail.setError("Email is required");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Invalid email address");
            return false;
        }
        tilEmail.setError(null);
        
        if (address.isEmpty()) {
            etAddress.setError("Address is required - Use GPS button or enter manually");
            return false;
        }
        etAddress.setError(null);
        
        // Warn if location not captured (lat/lng = 0)
        if (currentLatitude == 0.0 && currentLongitude == 0.0) {
            new AlertDialog.Builder(this)
                    .setTitle("No GPS Location")
                    .setMessage("You haven't captured a GPS location. The address will be saved but map features won't work.")
                    .setPositiveButton("Continue", (dialog, which) -> {})
                    .setNegativeButton("Get Location", (dialog, which) -> {
                        if (!isLocationEnabled()) {
                            promptEnableGPS();
                        } else if (!checkLocationPermission()) {
                            requestLocationPermission();
                        } else {
                            getLocation();
                        }
                    })
                    .show();
            return false; // Prevent save until user confirms
        }
        
        return true;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationHelper != null) {
            locationHelper.cleanup();
        }
    }
}