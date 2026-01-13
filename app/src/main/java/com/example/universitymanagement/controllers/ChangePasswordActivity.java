package com.example.universitymanagement.controllers;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universitymanagement.R;
import com.example.universitymanagement.database.AdminDatabase;
import com.example.universitymanagement.util.Session;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private Button btnChangePassword;
    private AdminDatabase adminDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        adminDatabase = new AdminDatabase();

        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String currentPass = etCurrentPassword.getText().toString();
        String newPass = etNewPassword.getText().toString();
        String confirmPass = etConfirmPassword.getText().toString();

        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check for Admin Session
        if (Session.currentAdmin != null) {
            if (!Session.currentAdmin.getPassword().equals(currentPass)) {
                Toast.makeText(this, "Incorrect current password", Toast.LENGTH_SHORT).show();
                return;
            }
            adminDatabase.updatePassword(Session.currentAdmin.getUsername(), newPass)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                        Session.currentAdmin.setPassword(newPass);
                        finish();
                    })
                    .addOnFailureListener(
                            e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
        // Check for Faculty Session
        else if (Session.currentFaculty != null) {
            if (!Session.currentFaculty.getPassword().equals(currentPass)) {
                Toast.makeText(this, "Incorrect current password", Toast.LENGTH_SHORT).show();
                return;
            }
            new com.example.universitymanagement.database.FacultyDatabase()
                    .updatePassword(Session.currentFaculty.getId(), newPass)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                        Session.currentFaculty.setPassword(newPass);
                        finish();
                    })
                    .addOnFailureListener(
                            e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
        // Check for Student Session
        else if (Session.currentStudent != null) {
            if (!Session.currentStudent.getPassword().equals(currentPass)) {
                Toast.makeText(this, "Incorrect current password", Toast.LENGTH_SHORT).show();
                return;
            }
            new com.example.universitymanagement.database.StudentDatabase()
                    .updatePassword(Session.currentStudent.getId(), newPass)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                        Session.currentStudent.setPassword(newPass);
                        finish();
                    })
                    .addOnFailureListener(
                            e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "No active session found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
