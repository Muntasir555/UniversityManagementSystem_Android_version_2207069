package com.example.universitymanagement.controllers;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universitymanagement.R;
import com.example.universitymanagement.database.StudentDatabase;
import com.example.universitymanagement.models.Student;

public class RegisterStudentActivity extends AppCompatActivity {

    private EditText etName, etEmail, etBatch, etPassword;
    private Spinner spDepartment;
    private Button btnRegister;
    private android.widget.ProgressBar progressBar;
    private StudentDatabase studentDatabase;
    private int registrationAttempt = 0;
    private static final int MAX_RETRY_ATTEMPTS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);

        studentDatabase = new StudentDatabase(this);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etBatch = findViewById(R.id.etBatch);
        etPassword = findViewById(R.id.etPassword);
        spDepartment = findViewById(R.id.spDepartment);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        // Populate Department Spinner
        String[] departments = { "CSE", "EEE", "Civil", "ME", "Physics", "Math", "Chemistry" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                departments);
        spDepartment.setAdapter(adapter);

        btnRegister.setOnClickListener(v -> registerStudent());
    }

    private void registerStudent() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String department = spDepartment.getSelectedItem() != null ? spDepartment.getSelectedItem().toString() : "";
        String batch = etBatch.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || batch.isEmpty() || password.isEmpty() || department.isEmpty()) {
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
        registrationAttempt = 0;

        // Perform registration in background thread
        new Thread(() -> {
            try {
                // Check for duplicate email
                Student existing = studentDatabase.getStudentByEmailAndDepartment(email, department);
                if (existing != null) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(android.view.View.GONE);
                        btnRegister.setEnabled(true);
                        btnRegister.setText("Register Student");
                        
                        new androidx.appcompat.app.AlertDialog.Builder(RegisterStudentActivity.this)
                                .setTitle("Registration Failed")
                                .setMessage("Change Email ID, it has already been used in this department.")
                                .setPositiveButton("OK", null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    });
                    return;
                }

                // Attempt registration with retry logic
                attemptRegistration(name, email, department, batch, password);

            } catch (Exception e) {
                android.util.Log.e("RegisterStudent", "Registration error", e);
                runOnUiThread(() -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    btnRegister.setEnabled(true);
                    btnRegister.setText("Register Student");
                    Toast.makeText(RegisterStudentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void attemptRegistration(String name, String email, String department, String batch, String password) {
        registrationAttempt++;
        android.util.Log.d("RegisterStudent", "Registration attempt #" + registrationAttempt);

        // Generate ID
        String id = studentDatabase.getNextStudentId(batch, department);
        android.util.Log.d("RegisterStudent", "Generated ID: " + id);

        // Check if ID already exists (race condition protection)
        boolean exists = studentDatabase.isStudentIdExists(id);
        
        if (exists) {
            android.util.Log.w("RegisterStudent", "ID " + id + " already exists, retrying...");
            if (registrationAttempt < MAX_RETRY_ATTEMPTS) {
                // Retry with new ID
                attemptRegistration(name, email, department, batch, password);
                return;
            } else {
                runOnUiThread(() -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    btnRegister.setEnabled(true);
                    btnRegister.setText("Register Student");
                    Toast.makeText(RegisterStudentActivity.this,
                            "Failed to generate unique ID after " + MAX_RETRY_ATTEMPTS + " attempts. Please try again.",
                            Toast.LENGTH_LONG).show();
                });
                return;
            }
        }

        // ID is unique, proceed with registration
        Student student = new Student(id, name, email, department, batch, password);
        boolean success = studentDatabase.addStudent(student);

        runOnUiThread(() -> {
            progressBar.setVisibility(android.view.View.GONE);
            btnRegister.setEnabled(true);
            btnRegister.setText("Register Student");

            if (success) {
                String attemptInfo = registrationAttempt > 1 ? " (after " + registrationAttempt + " attempts)" : "";
                android.util.Log.d("RegisterStudent", "Student registered successfully: " + id + attemptInfo);
                
                Toast.makeText(RegisterStudentActivity.this,
                        "✓ Student registered successfully!\nID: " + id,
                        Toast.LENGTH_LONG).show();

                String successMsg = "Student registered successfully!\n\n" +
                        "Student ID: " + id + "\n" +
                        "Name: " + name + "\n" +
                        "Department: " + department + "\n" +
                        "Batch: " + batch;

                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("✓ Registration Successful")
                        .setMessage(successMsg)
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, which) -> {
                            dialog.dismiss();
                            clearFields();
                            registrationAttempt = 0;
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            } else {
                android.util.Log.e("RegisterStudent", "Registration failed for: " + id);
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Registration Failed")
                        .setMessage("Error adding student to database. Please try again.")
                        .setPositiveButton("OK", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    private void clearFields() {
        etName.setText("");
        etEmail.setText("");
        etBatch.setText("");
        etPassword.setText("");
        if (spDepartment.getAdapter().getCount() > 0) {
            spDepartment.setSelection(0);
        }
    }
}
