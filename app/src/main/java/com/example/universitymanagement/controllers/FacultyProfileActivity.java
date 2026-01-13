package com.example.universitymanagement.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universitymanagement.R;
import com.example.universitymanagement.models.Faculty;
import com.example.universitymanagement.util.Session;

public class FacultyProfileActivity extends AppCompatActivity {

    private TextView tvName, tvId, tvEmail, tvDepartment;
    private Button btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_profile);

        tvName = findViewById(R.id.tvFacultyName);
        tvId = findViewById(R.id.tvFacultyId);
        tvEmail = findViewById(R.id.tvFacultyEmail);
        tvDepartment = findViewById(R.id.tvFacultyDepartment);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        if (Session.currentFaculty != null) {
            Faculty faculty = Session.currentFaculty;
            tvName.setText(faculty.getName());
            tvId.setText("ID: " + faculty.getId());
            tvEmail.setText(faculty.getEmail());
            tvDepartment.setText("Department: " + faculty.getDepartment());
        } else {
            Toast.makeText(this, "Session Expired", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(FacultyProfileActivity.this, ChangePasswordActivity.class));
        });
    }
}
