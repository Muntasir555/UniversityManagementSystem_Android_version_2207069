package com.example.universitymanagement.controllers;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universitymanagement.R;
import com.example.universitymanagement.database.AdminDatabase;
import com.example.universitymanagement.database.FacultyDatabase;
import com.example.universitymanagement.database.StudentDatabase;
import com.example.universitymanagement.util.Session;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private Button btnChangePassword;
    private AdminDatabase adminDatabase;
    private FacultyDatabase facultyDatabase;
    private StudentDatabase studentDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        adminDatabase = new AdminDatabase(this);
        facultyDatabase = new FacultyDatabase(this);
        studentDatabase = new StudentDatabase(this);

        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String currentPass = etCurrentPassword.getText().toString().trim();
        String newPass = etNewPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

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
            
            new Thread(() -> {
                boolean success = adminDatabase.updatePassword(Session.currentAdmin.getUsername(), newPass);
                
                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                        Session.currentAdmin.setPassword(newPass);
                        finish();
                    } else {
                        Toast.makeText(this, "Error updating password", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        }
        // Check for Faculty Session
        else if (Session.currentFaculty != null) {
            if (!Session.currentFaculty.getPassword().equals(currentPass)) {
                Toast.makeText(this, "Incorrect current password", Toast.LENGTH_SHORT).show();
                return;
            }
            
            new Thread(() -> {
                boolean success = facultyDatabase.updatePassword(Session.currentFaculty.getId(), newPass);
                
                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                        Session.currentFaculty.setPassword(newPass);
                        finish();
                    } else {
                        Toast.makeText(this, "Error updating password", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        }
        // Check for Student Session
        else if (Session.currentStudent != null) {
            if (!Session.currentStudent.getPassword().equals(currentPass)) {
                Toast.makeText(this, "Incorrect current password", Toast.LENGTH_SHORT).show();
                return;
            }
            
            new Thread(() -> {
                boolean success = studentDatabase.updatePassword(Session.currentStudent.getId(), newPass);
                
                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                        Session.currentStudent.setPassword(newPass);
                        finish();
                    } else {
                        Toast.makeText(this, "Error updating password", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        } else {
            Toast.makeText(this, "No active session found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
