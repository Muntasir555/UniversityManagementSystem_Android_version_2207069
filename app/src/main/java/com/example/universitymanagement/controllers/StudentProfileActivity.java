package com.example.universitymanagement.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universitymanagement.R;
import com.example.universitymanagement.models.Student;
import com.example.universitymanagement.util.Session;

public class StudentProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        TextView tvName = findViewById(R.id.tvStudentName);
        TextView tvId = findViewById(R.id.tvStudentId);
        TextView tvEmail = findViewById(R.id.tvStudentEmail);
        TextView tvDepartment = findViewById(R.id.tvStudentDepartment);
        TextView tvBatch = findViewById(R.id.tvStudentBatch);
        TextView tvCGPA = findViewById(R.id.tvStudentCGPA);
        Button btnChangePassword = findViewById(R.id.btnChangePassword);

        if (Session.currentStudent == null) {
            Toast.makeText(this, "Session Expired", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Student student = Session.currentStudent;
        tvName.setText(student.getName());
        tvId.setText("ID: " + student.getId());
        tvEmail.setText(student.getEmail());
        tvDepartment.setText("Department: " + student.getDepartment());
        tvBatch.setText("Batch: " + student.getBatch());
        tvCGPA.setText(String.format("CGPA: %.2f", student.getCgpa()));

        btnChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(StudentProfileActivity.this, ChangePasswordActivity.class));
        });
    }
}
