package com.example.universitymanagement.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universitymanagement.R;
import com.example.universitymanagement.database.FacultyDatabase;
import com.example.universitymanagement.models.Faculty;
import com.example.universitymanagement.util.Session;

public class FacultyLoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Spinner spDepartment;
    private TextView tvError;
    private FacultyDatabase facultyDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_login);

        facultyDatabase = new FacultyDatabase(this);

        etEmail = findViewById(R.id.etFacultyEmail);
        etPassword = findViewById(R.id.etFacultyPassword);
        spDepartment = findViewById(R.id.spDepartment);
        tvError = findViewById(R.id.tvFacultyError);
        Button btnLogin = findViewById(R.id.btnFacultyLogin);
        Button btnBack = findViewById(R.id.btnBackStart);

        // Populate Spinner
        String[] departments = {"CSE", "EEE", "Civil", "ME", "Physics", "Math", "Chemistry"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, departments);
        spDepartment.setAdapter(adapter);

        btnLogin.setOnClickListener(v -> handleLogin());
        btnBack.setOnClickListener(v -> finish());
    }

    private void handleLogin() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String department = spDepartment.getSelectedItem() != null ? spDepartment.getSelectedItem().toString() : "";

        if (email.isEmpty() || password.isEmpty() || department.isEmpty()) {
            tvError.setText("Please enter all details.");
            return;
        }

        // Perform login in background thread
        new Thread(() -> {
            Faculty faculty = facultyDatabase.validateLogin(email, password, department);
            
            runOnUiThread(() -> {
                if (faculty != null) {
                    Session.clear();
                    Session.currentFaculty = faculty;
                    startActivity(new Intent(FacultyLoginActivity.this, FacultyDashboardActivity.class));
                    finish();
                } else {
                    tvError.setText("Invalid credentials.");
                }
            });
        }).start();
    }
}
