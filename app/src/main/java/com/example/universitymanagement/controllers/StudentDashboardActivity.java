package com.example.universitymanagement.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universitymanagement.R;
import com.example.universitymanagement.util.Session;

public class StudentDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        // Initialize Views
        TextView cgpaLabel = findViewById(R.id.cgpaLabel);
        Button btnProfile = findViewById(R.id.btnProfile);
        Button btnResults = findViewById(R.id.btnResults);
        Button btnAttendance = findViewById(R.id.btnAttendance);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Set initial CGPA (This should eventually come from the database)
        // Set initial CGPA
        if (Session.currentStudent != null) {
            cgpaLabel.setText(String.format("%.2f", Session.currentStudent.getCgpa()));
        } else {
            Toast.makeText(this, "Session Expired", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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
}
