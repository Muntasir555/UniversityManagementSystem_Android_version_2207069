package com.example.universitymanagement.controllers;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.universitymanagement.R;
import com.example.universitymanagement.database.StudentDatabase;
import com.example.universitymanagement.models.Student;

public class StudentLoginActivity extends AppCompatActivity {

    private android.widget.EditText etEmail, etPassword;
    private android.widget.Spinner spDepartment;
    private android.widget.TextView tvError;
    private StudentDatabase studentDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        studentDatabase = new StudentDatabase(this);

        etEmail = findViewById(R.id.etStudentEmail);
        etPassword = findViewById(R.id.etStudentPassword);
        spDepartment = findViewById(R.id.spStudentDepartment);
        tvError = findViewById(R.id.tvStudentError);
        Button btnLogin = findViewById(R.id.btnStudentLogin);
        Button btnBack = findViewById(R.id.btnBackStart);

        // Populate Spinner
        String[] departments = { "CSE", "EEE", "Civil", "ME", "Physics", "Math", "Chemistry" };
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

        // Perform login in background thread
        new Thread(() -> {
            Student student = studentDatabase.login(email, password, department);
            
            runOnUiThread(() -> {
                if (student != null) {
                    com.example.universitymanagement.util.Session.clear();
                    com.example.universitymanagement.util.Session.currentStudent = student;
                    startActivity(new android.content.Intent(StudentLoginActivity.this, StudentDashboardActivity.class));
                    finish();
                } else {
                    tvError.setText("Invalid credentials.");
                }
            });
        }).start();
    }
}
