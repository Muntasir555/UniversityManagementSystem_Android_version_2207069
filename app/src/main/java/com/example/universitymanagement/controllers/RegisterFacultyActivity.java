package com.example.universitymanagement.controllers;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universitymanagement.R;
import com.example.universitymanagement.database.FacultyDatabase;
import com.example.universitymanagement.models.Faculty;

public class RegisterFacultyActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private Spinner spDepartment;
    private Button btnRegister;
    private android.widget.ProgressBar progressBar;
    private FacultyDatabase facultyDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_faculty);

        facultyDatabase = new FacultyDatabase(this);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        spDepartment = findViewById(R.id.spDepartment);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        // Populate Department Spinner
        String[] departments = { "CSE", "EEE", "Civil", "ME", "Physics", "Math", "Chemistry" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                departments);
        spDepartment.setAdapter(adapter);

        btnRegister.setOnClickListener(v -> registerFaculty());
    }

    private void registerFaculty() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String department = spDepartment.getSelectedItem() != null ? spDepartment.getSelectedItem().toString() : "";

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || department.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.endsWith("@university.edu")) {
            Toast.makeText(this, "Email must end with @university.edu", Toast.LENGTH_SHORT).show();
            return;
        }

        // UI Updates: Show loading
        btnRegister.setEnabled(false);
        btnRegister.setText("Registering...");
        progressBar.setVisibility(android.view.View.VISIBLE);

        Faculty faculty = new Faculty(name, email, department, password);
        faculty.setId(java.util.UUID.randomUUID().toString());

        new Thread(() -> {
            // Check for duplicate email in the same department
            Faculty existingFaculty = facultyDatabase.getFacultyByEmailAndDepartment(email, department);

            runOnUiThread(() -> {
                if (existingFaculty != null) {
                    progressBar.setVisibility(android.view.View.GONE);
                    btnRegister.setEnabled(true);
                    btnRegister.setText("Register Faculty");

                    new androidx.appcompat.app.AlertDialog.Builder(RegisterFacultyActivity.this)
                            .setTitle("Registration Failed")
                            .setMessage("Change Email ID, it has already been used in this department.")
                            .setPositiveButton("OK", null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }

                // Perform registration in a background thread
                new Thread(() -> {
                    boolean success = facultyDatabase.addFaculty(faculty);

                    runOnUiThread(() -> {
                        // UI Updates: Hide loading
                        progressBar.setVisibility(android.view.View.GONE);
                        btnRegister.setEnabled(true);
                        btnRegister.setText("Register Faculty");

                        if (success) {
                            showSuccessDialog();
                        } else {
                            new androidx.appcompat.app.AlertDialog.Builder(this)
                                    .setTitle("Registration Failed")
                                    .setMessage("An error occurred while registering the faculty.")
                                    .setPositiveButton("OK", null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    });
                }).start();
            });
        }).start();
    }

    private void showSuccessDialog() {
        if (isFinishing() || isDestroyed())
            return;
        
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage("Faculty registered successfully")
                .setCancelable(false) // Prevent dismissal by clicking outside
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    clearFields();
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    private void clearFields() {
        etName.setText("");
        etEmail.setText("");
        etPassword.setText("");
        if (spDepartment.getAdapter().getCount() > 0) {
            spDepartment.setSelection(0);
        }
    }
}
