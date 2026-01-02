package com.example.universitymanagement;

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

        Admin admin = AdminDatabase.validateLogin(username, password);

        if (admin != null) {
            Session.clear();
            Session.currentAdmin = admin;
            navigateToDashboard();
        } else {
            tvError.setText("Invalid credentials.");
        }
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(AdminLoginActivity.this, AdminDashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
