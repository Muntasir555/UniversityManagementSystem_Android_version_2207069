package com.example.universitymanagement.controllers;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.universitymanagement.R;
import com.example.universitymanagement.database.AdminDatabase;
import com.example.universitymanagement.models.Admin;
import com.example.universitymanagement.util.Session;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private TextView tvError;
    private AdminDatabase adminDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize database
        adminDatabase = new AdminDatabase(this);

        // Initialize Views
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tvError = findViewById(R.id.tvError);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnBack = findViewById(R.id.btnBack);

        // Login Button Listener
        btnLogin.setOnClickListener(v -> handleLogin());

        // Back Button Listener
        btnBack.setOnClickListener(v -> finish());
    }

    private void handleLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            tvError.setText("Please enter username and password.");
            return;
        }

        // Perform login in background thread
        new Thread(() -> {
            Admin admin = adminDatabase.login(username, password);
            
            runOnUiThread(() -> {
                if (admin != null) {
                    Session.clear();
                    Session.currentAdmin = admin;
                    android.widget.Toast.makeText(this, "Login Successful", android.widget.Toast.LENGTH_SHORT).show();
                    android.util.Log.d("AdminLogin", "Login successful for: " + username);
                    navigateToDashboard();
                } else {
                    tvError.setText("Invalid username or password.");
                    android.util.Log.d("AdminLogin", "Login failed for: " + username);
                }
            });
        }).start();
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(AdminLoginActivity.this, AdminDashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
