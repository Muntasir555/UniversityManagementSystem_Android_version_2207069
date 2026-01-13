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
    private android.os.Handler timeoutHandler = new android.os.Handler();
    private Runnable timeoutRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_faculty);

        facultyDatabase = new FacultyDatabase();

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

    // Flag to prevent double handling (Success vs Timeout)
    private boolean isRequestHandled = false;

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
        isRequestHandled = false;

        Faculty faculty = new Faculty(name, email, department, password);
        faculty.setId(java.util.UUID.randomUUID().toString());

        // Setup Timeout (15 seconds)
        timeoutRunnable = () -> {
            if (!isRequestHandled) {
                isRequestHandled = true;
                progressBar.setVisibility(android.view.View.GONE);
                btnRegister.setEnabled(true);
                btnRegister.setText("Register Faculty");

                new androidx.appcompat.app.AlertDialog.Builder(RegisterFacultyActivity.this)
                        .setTitle("Request Timeout")
                        .setMessage(
                                "The server is taking too long to respond.\n\nCheck your internet connection or check the Faculty List to see if the registration went through.")
                        .setPositiveButton("OK", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        };
        timeoutHandler.postDelayed(timeoutRunnable, 15000);

        // Check for duplicate email in the same department
        facultyDatabase.getFacultyByEmailAndDepartment(email, department)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (isRequestHandled)
                        return;

                    if (!queryDocumentSnapshots.isEmpty()) {
                        isRequestHandled = true;
                        if (timeoutHandler != null && timeoutRunnable != null) {
                            timeoutHandler.removeCallbacks(timeoutRunnable);
                        }
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

                    facultyDatabase.addFaculty(faculty)
                            .addOnCompleteListener(task -> {
                                if (isRequestHandled)
                                    return; // Already handled by timeout

                                isRequestHandled = true;
                                if (timeoutHandler != null && timeoutRunnable != null) {
                                    timeoutHandler.removeCallbacks(timeoutRunnable);
                                }

                                // UI Updates: Hide loading
                                progressBar.setVisibility(android.view.View.GONE);
                                btnRegister.setEnabled(true);
                                btnRegister.setText("Register Faculty");

                                if (task.isSuccessful()) {
                                    showSuccessDialog();
                                } else {
                                    String errorMsg = task.getException() != null ? task.getException().getMessage()
                                            : "Unknown error";
                                    new androidx.appcompat.app.AlertDialog.Builder(this)
                                            .setTitle("Registration Failed")
                                            .setMessage("Error: " + errorMsg)
                                            .setPositiveButton("OK", null)
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    handleFailure(e);
                });
    }

    private void handleFailure(Exception e) {
        if (isRequestHandled)
            return;
        isRequestHandled = true;
        if (timeoutHandler != null && timeoutRunnable != null) {
            timeoutHandler.removeCallbacks(timeoutRunnable);
        }
        progressBar.setVisibility(android.view.View.GONE);
        btnRegister.setEnabled(true);
        btnRegister.setText("Register Faculty");

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("An error occurred: " + e.getMessage())
                .setPositiveButton("OK", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showSuccessDialog() {
        if (isFinishing())
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timeoutHandler != null && timeoutRunnable != null) {
            timeoutHandler.removeCallbacks(timeoutRunnable);
        }
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
