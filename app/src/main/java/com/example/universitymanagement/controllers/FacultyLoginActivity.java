package com.example.universitymanagement.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.universitymanagement.R;
import com.example.universitymanagement.database.FacultyDatabase;
import com.example.universitymanagement.models.Faculty;
import com.example.universitymanagement.util.Session;

import java.util.List;

public class FacultyLoginActivity extends AppCompatActivity {

    private Spinner spDepartment;
    private EditText etEmail, etPassword;
    private TextView tvError;
    private Button btnLogin, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_faculty_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Views
        spDepartment = findViewById(R.id.spDepartment);
        etEmail = findViewById(R.id.etFacultyEmail);
        etPassword = findViewById(R.id.etFacultyPassword);
        tvError = findViewById(R.id.tvFacultyError);
        btnLogin = findViewById(R.id.btnFacultyLogin);
        btnBack = findViewById(R.id.btnBackStart);

        // Setup Spinner
        String[] departments = { "CSE", "EEE", "Civil", "ME", "Physics", "Math", "Chemistry" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                departments);
        spDepartment.setAdapter(adapter);

        // Listeners
        btnLogin.setOnClickListener(v -> handleLogin());
        btnBack.setOnClickListener(v -> finish());
    }

    private void handleLogin() {
        String department = spDepartment.getSelectedItem().toString();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            tvError.setText("Please enter all details.");
            return;
        }

        FacultyDatabase db = new FacultyDatabase();
        // Use getFacultyByEmail to avoid composite query issues
        db.getFacultyByEmail(email).addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                // We found a faculty with this email. Now check password and department.
                Faculty faculty = queryDocumentSnapshots.getDocuments().get(0).toObject(Faculty.class);

                if (faculty != null) {
                    if (faculty.getPassword().equals(password) && faculty.getDepartment().equals(department)) {
                        // Login Success
                        Session.clear();
                        Session.currentFaculty = faculty;
                        tvError.setText("");
                        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(FacultyLoginActivity.this, FacultyDashboardActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        tvError.setText("Invalid credentials (Password or Department).");
                    }
                } else {
                    tvError.setText("Error parsing faculty data.");
                }
            } else {
                tvError.setText("Account not found.");
            }
        }).addOnFailureListener(e -> {
            tvError.setText("Login failed: " + e.getMessage());
        });
    }
}
