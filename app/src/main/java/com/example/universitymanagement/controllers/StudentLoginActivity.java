package com.example.universitymanagement.controllers;

import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.universitymanagement.R;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StudentLoginActivity extends AppCompatActivity {

    private android.widget.EditText etEmail, etPassword;
    private android.widget.Spinner spDepartment;
    private android.widget.TextView tvError;
    private com.example.universitymanagement.database.StudentDatabase studentDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        studentDatabase = new com.example.universitymanagement.database.StudentDatabase();

        etEmail = findViewById(R.id.etStudentEmail);
        etPassword = findViewById(R.id.etStudentPassword);
        spDepartment = findViewById(R.id.spStudentDepartment);
        tvError = findViewById(R.id.tvStudentError);
        Button btnLogin = findViewById(R.id.btnStudentLogin);
        Button btnBack = findViewById(R.id.btnBackStart);

        // Populate Spinner
        String[] departments = { "CSE", "EEE", "Civil", "ME" };
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, departments);
        spDepartment.setAdapter(adapter);

        btnLogin.setOnClickListener(v -> handleLogin());
        btnBack.setOnClickListener(v -> finish());
    }

    private void handleLogin() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String department = spDepartment.getSelectedItem() != null ? spDepartment.getSelectedItem().toString() : "";

        if (email.isEmpty() || password.isEmpty() || department.isEmpty()) {
            tvError.setText("Please enter all details including department.");
            return;
        }

        studentDatabase.login(email, password, department).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                com.example.universitymanagement.models.Student student = task.getResult().getDocuments().get(0)
                        .toObject(com.example.universitymanagement.models.Student.class);
                if (student != null) {
                    com.example.universitymanagement.util.Session.clear();
                    com.example.universitymanagement.util.Session.currentStudent = student;
                    startActivity(
                            new android.content.Intent(StudentLoginActivity.this, StudentDashboardActivity.class));
                    finish();
                } else {
                    tvError.setText("Login failed: Invalid data.");
                }
            } else {
                tvError.setText("Invalid credentials.");
            }
        });
    }
}
