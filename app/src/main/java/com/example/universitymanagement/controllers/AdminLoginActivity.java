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

        // Ensure default admin exists
        new AdminDatabase().checkAndCreateDefaultAdmin();

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

        AdminDatabase adminDatabase = new AdminDatabase();
        adminDatabase.getAdminByUsername(username).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                com.google.firebase.firestore.QuerySnapshot snapshot = task.getResult();
                if (snapshot != null && !snapshot.isEmpty()) {
                    // Check if any document actually matches
                    boolean found = false;
                    for (com.google.firebase.firestore.DocumentSnapshot doc : snapshot.getDocuments()) {
                        Admin admin = doc.toObject(Admin.class);
                        if (admin != null && username.equals(admin.getUsername())) {
                            if (password.equals(admin.getPassword())) {
                                Session.clear();
                                Session.currentAdmin = admin;
                                android.widget.Toast
                                        .makeText(this, "Login Successful", android.widget.Toast.LENGTH_SHORT)
                                        .show();
                                navigateToDashboard();
                                found = true;
                                break;
                            } else {
                                tvError.setText("Invalid password.");
                                found = true; // Username found, but password incorrect
                                break;
                            }
                        }
                    }
                    if (!found) {
                        // technically shouldn't reach here if query worked correctly for username,
                        // unless case sensitivity or other data issues
                        tvError.setText("User not found.");
                    }
                } else {
                    tvError.setText("User not found.");
                }
            } else {
                tvError.setText("Login error: "
                        + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                android.util.Log.e("AdminLogin", "Error logging in", task.getException());
            }
        });
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(AdminLoginActivity.this, AdminDashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
