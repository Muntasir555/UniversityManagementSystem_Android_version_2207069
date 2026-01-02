package com.example.universitymanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
        Button btnLogout = findViewById(R.id.btnLogout);

        // Set Click Listeners
        btnRegisterStudent.setOnClickListener(v -> {
            // TODO: Start Register Student Activity
            Toast.makeText(this, "Register Student Clicked", Toast.LENGTH_SHORT).show();
        });

        btnRegisterFaculty.setOnClickListener(v -> {
            // TODO: Start Register Faculty Activity
            Toast.makeText(this, "Register Faculty Clicked", Toast.LENGTH_SHORT).show();
        });

        btnAssignCourse.setOnClickListener(v -> {
            // TODO: Start Assign Course Activity
            Toast.makeText(this, "Assign Course Clicked", Toast.LENGTH_SHORT).show();
        });

        btnViewStudents.setOnClickListener(v -> {
            // TODO: Start View Students Activity
            Toast.makeText(this, "View Students Clicked", Toast.LENGTH_SHORT).show();
        });

        btnViewFaculty.setOnClickListener(v -> {
            // TODO: Start View Faculty Activity
            Toast.makeText(this, "View Faculty Clicked", Toast.LENGTH_SHORT).show();
        });

        btnChangePassword.setOnClickListener(v -> {
            // TODO: Start Change Password Activity
            Toast.makeText(this, "Change Password Clicked", Toast.LENGTH_SHORT).show();
        });

        btnNoticeBoard.setOnClickListener(v -> {
            // TODO: Start Notice Board Activity
            Toast.makeText(this, "Notice Board Clicked", Toast.LENGTH_SHORT).show();
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
