package com.example.studentmanagement.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanagement.MainActivity;
import com.example.studentmanagement.R;
import com.example.studentmanagement.data.remote.ApiService;
import com.example.studentmanagement.data.remote.AuthResponse;
import com.example.studentmanagement.data.remote.RetrofitClient;
import com.example.studentmanagement.utils.PreferencesManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText etLogin, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private ApiService apiService;
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        

        
        initViews();
        preferencesManager = new PreferencesManager(this);
        apiService = RetrofitClient.getClient().create(ApiService.class);
        
        // Check if already logged in
        if (preferencesManager.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }
        
        btnLogin.setOnClickListener(v -> performLogin());
    }
    
    private void initViews() {
        etLogin = findViewById(R.id.etUsername);  // Rename in layout to etLogin
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        
        // Update hint text
        etLogin.setHint("Login");
        etPassword.setHint("Password");
    }
    
    private void performLogin() {
        String login = etLogin.getText().toString().trim();
        String passwd = etPassword.getText().toString().trim();
        
        if (login.isEmpty() || passwd.isEmpty()) {
            Toast.makeText(this, "Please enter login and password", Toast.LENGTH_SHORT).show();
            return;
        }
        
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);
        
        Log.d(TAG, "Attempting login with: " + login);
        
        apiService.login(login, passwd).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                
                Log.d(TAG, "Response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    
                    if (authResponse.isSuccessful()) {
                        // Save login info
                        preferencesManager.saveAuthToken(authResponse.getSessionToken());
                        preferencesManager.saveUsername(login);
                        preferencesManager.savePreference("user_id", authResponse.getId());
                        preferencesManager.savePreference("user_nom", authResponse.getNom());
                        preferencesManager.savePreference("user_prenom", authResponse.getPrenom());
                        
                        String welcomeMsg = "Welcome " + authResponse.getFullName();
                        Toast.makeText(LoginActivity.this, welcomeMsg, Toast.LENGTH_LONG).show();
                        
                        Log.d(TAG, "Login successful - Token: " + authResponse.getSessionToken());
                        Log.d(TAG, "User: " + authResponse.getFullName() + " (ID: " + authResponse.getId() + ")");
                        
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        String errorMsg = authResponse.getError();
                        if (errorMsg == null || errorMsg.isEmpty()) {
                            errorMsg = "Authentication failed";
                        }
                        Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Login failed: " + errorMsg);
                    }
                } else {
                    // Handle HTTP error (400, 401, etc.)
                    String errorMsg = "Login failed. Code: " + response.code();
                    
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                            
                            // Parse error message from JSON
                            if (errorBody.contains("error")) {
                                errorMsg = errorBody;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    Log.e(TAG, errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                
                String errorMsg = "Network error: " + t.getMessage();
                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Network failure", t);
            }
        });
    }
}