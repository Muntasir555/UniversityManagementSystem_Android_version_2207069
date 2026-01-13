package com.example.universitymanagement.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universitymanagement.R;

public class FacultyDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_dashboard);

        // Initialize Buttons
        Button btnProfile = findViewById(R.id.btnProfile);
        Button btnCourses = findViewById(R.id.btnCourses);
        Button btnAttendance = findViewById(R.id.btnAttendance);
        Button btnResults = findViewById(R.id.btnResults);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Set Click Listeners
        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(FacultyDashboardActivity.this, FacultyProfileActivity.class));
        });

        btnCourses.setOnClickListener(v -> {
            startActivity(new Intent(FacultyDashboardActivity.this, FacultyCoursesActivity.class));
        });

        btnAttendance.setOnClickListener(v -> {
            startActivity(new Intent(FacultyDashboardActivity.this, FacultyAttendanceActivity.class));
        });

        btnResults.setOnClickListener(v -> {
            startActivity(new Intent(FacultyDashboardActivity.this, FacultyAddResultActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            // Logout Logic
            Intent intent = new Intent(FacultyDashboardActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
