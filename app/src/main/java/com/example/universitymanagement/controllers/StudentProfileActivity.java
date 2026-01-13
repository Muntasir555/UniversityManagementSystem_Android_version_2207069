package com.example.universitymanagement.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universitymanagement.R;
import com.example.universitymanagement.database.StudentDatabase;
import com.example.universitymanagement.models.Student;
import com.example.universitymanagement.util.Session;

public class StudentProfileActivity extends AppCompatActivity {

    private TextView tvCGPA;
    private StudentDatabase studentDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        if (Session.currentStudent == null) {
            Toast.makeText(this, "Session Expired", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Database
        studentDatabase = new StudentDatabase(this);

        // Initialize Views
        TextView tvName = findViewById(R.id.tvStudentName);
        TextView tvId = findViewById(R.id.tvStudentId);
        TextView tvEmail = findViewById(R.id.tvStudentEmail);
        TextView tvDepartment = findViewById(R.id.tvStudentDepartment);
        TextView tvBatch = findViewById(R.id.tvStudentBatch);
        tvCGPA = findViewById(R.id.tvStudentCGPA);
        Button btnChangePassword = findViewById(R.id.btnChangePassword);

        // Display student information
        Student student = Session.currentStudent;
        tvName.setText(student.getName());
        tvId.setText("ID: " + student.getId());
        tvEmail.setText(student.getEmail());
        tvDepartment.setText("Department: " + student.getDepartment());
        tvBatch.setText("Batch: " + student.getBatch());

        btnChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(StudentProfileActivity.this, ChangePasswordActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh CGPA from database every time the profile is shown
        loadCGPA();
    }

    private void loadCGPA() {
        new Thread(() -> {
            // Fetch fresh student data from database
            Student freshStudent = studentDatabase.getStudent(Session.currentStudent.getId());
            
            if (freshStudent != null) {
                // Update session with fresh CGPA
                Session.currentStudent.setCgpa(freshStudent.getCgpa());
                
                // Update UI
                runOnUiThread(() -> {
                    tvCGPA.setText(String.format("CGPA: %.2f", freshStudent.getCgpa()));
                });
            }
        }).start();
    }
}
