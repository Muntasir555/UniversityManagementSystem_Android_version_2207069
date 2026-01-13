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

public class StudentDashboardActivity extends AppCompatActivity {

    private TextView cgpaLabel;
    private StudentDatabase studentDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        // Check session
        if (Session.currentStudent == null) {
            Toast.makeText(this, "Session Expired", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Database
        studentDatabase = new StudentDatabase(this);

        // Initialize Views
        cgpaLabel = findViewById(R.id.cgpaLabel);
        Button btnProfile = findViewById(R.id.btnProfile);
        Button btnResults = findViewById(R.id.btnResults);
        Button btnAttendance = findViewById(R.id.btnAttendance);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Set Click Listeners
        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(StudentDashboardActivity.this, StudentProfileActivity.class));
        });

        btnResults.setOnClickListener(v -> {
            startActivity(new Intent(StudentDashboardActivity.this, StudentResultsActivity.class));
        });

        btnAttendance.setOnClickListener(v -> {
            startActivity(new Intent(StudentDashboardActivity.this, StudentAttendanceActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            Session.clear();
            Intent intent = new Intent(StudentDashboardActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh CGPA from database every time the dashboard is shown
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
                    cgpaLabel.setText(String.format("%.2f", freshStudent.getCgpa()));
                });
            }
        }).start();
    }
}
