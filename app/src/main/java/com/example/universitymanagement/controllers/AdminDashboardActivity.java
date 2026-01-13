package com.example.universitymanagement.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universitymanagement.R;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize Buttons
        Button btnRegisterStudent = findViewById(R.id.btnRegisterStudent);
        Button btnRegisterFaculty = findViewById(R.id.btnRegisterFaculty);
        Button btnAssignCourse = findViewById(R.id.btnAssignCourse);
        Button btnViewStudents = findViewById(R.id.btnViewStudents);
        Button btnViewFaculty = findViewById(R.id.btnViewFaculty);
        Button btnChangePassword = findViewById(R.id.btnChangePassword);
        Button btnNoticeBoard = findViewById(R.id.btnNoticeBoard);
        Button btnResetDatabase = findViewById(R.id.btnResetDatabase);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Set Click Listeners
        // Set Click Listeners
        btnRegisterStudent.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, RegisterStudentActivity.class));
        });

        btnRegisterFaculty.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, RegisterFacultyActivity.class));
        });

        btnAssignCourse.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminAssignCourseActivity.class));
        });

        btnViewStudents.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, StudentListActivity.class));
        });

        btnViewFaculty.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, FacultyListActivity.class));
        });

        btnChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, ChangePasswordActivity.class));
        });

        btnNoticeBoard.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminNoticeBoardActivity.class));
        });

        btnResetDatabase.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Reset Database")
                    .setMessage("Are you sure you want to delete ALL data? This cannot be undone.")
                    .setPositiveButton("Yes, Delete All", (dialog, which) -> {
                        Toast.makeText(this, "Deleting database...", Toast.LENGTH_SHORT).show();
                        new Thread(() -> {
                            com.example.universitymanagement.util.DBUtil.getInstance(this).resetDatabase(
                                    () -> runOnUiThread(() -> Toast.makeText(this, "Database Cleared Successfully", Toast.LENGTH_LONG).show()),
                                    () -> runOnUiThread(() -> Toast.makeText(this, "Error Clearing Database", Toast.LENGTH_SHORT).show()));
                        }).start();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        btnLogout.setOnClickListener(v -> {
            // Logout Logic
            Intent intent = new Intent(AdminDashboardActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
