package com.example.universitymanagement.controllers;

import android.os.Bundle;
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
    private android.os.Handler timeoutHandler = new android.os.Handler();
    private Runnable timeoutRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);

        studentDatabase = new StudentDatabase();

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etBatch = findViewById(R.id.etBatch);
        etPassword = findViewById(R.id.etPassword);
        spDepartment = findViewById(R.id.spDepartment);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        // Populate Department Spinner
        String[] departments = { "CSE", "EEE", "Civil", "ME" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                departments);
        spDepartment.setAdapter(adapter);

        btnRegister.setOnClickListener(v -> registerStudent());
    }

    // Flag to prevent double handling (Success vs Timeout)
    private boolean isRequestHandled = false;

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
        isRequestHandled = false;

        // Setup Timeout (15 seconds) - This covers BOTH ID generation and Student
        // saving
        timeoutRunnable = () -> {
            if (!isRequestHandled) {
                isRequestHandled = true;
                progressBar.setVisibility(android.view.View.GONE);
                btnRegister.setEnabled(true);
                btnRegister.setText("Register Student");

                new androidx.appcompat.app.AlertDialog.Builder(RegisterStudentActivity.this)
                        .setTitle("Request Timeout")
                        .setMessage(
                                "The server is taking too long to respond.\n\nCheck your internet connection or check the Student List to see if the registration went through.")
                        .setPositiveButton("OK", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        };
        timeoutHandler.postDelayed(timeoutRunnable, 15000);

        // Check for duplicate email in the same department
        studentDatabase.getStudentByEmailAndDepartment(email, department)
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
                        btnRegister.setText("Register Student");

                        new androidx.appcompat.app.AlertDialog.Builder(RegisterStudentActivity.this)
                                .setTitle("Registration Failed")
                                .setMessage("Change Email ID, it has already been used in this department.")
                                .setPositiveButton("OK", null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        return;
                    }

                    // Step 1: Generate ID
                    studentDatabase.getNextStudentId(batch, department)
                            .addOnSuccessListener(id -> {
                                if (isRequestHandled)
                                    return;

                                Student student = new Student(id, name, email, department, batch, password);

                                // Step 2: Add Student to DB
                                studentDatabase.addStudent(student)
                                        .addOnCompleteListener(task -> {
                                            if (isRequestHandled)
                                                return;

                                            isRequestHandled = true;
                                            if (timeoutHandler != null && timeoutRunnable != null) {
                                                timeoutHandler.removeCallbacks(timeoutRunnable);
                                            }

                                            progressBar.setVisibility(android.view.View.GONE);
                                            btnRegister.setEnabled(true);
                                            btnRegister.setText("Register Student");

                                            if (task.isSuccessful()) {
                                                if (isFinishing())
                                                    return;
                                                new androidx.appcompat.app.AlertDialog.Builder(this)
                                                        .setTitle("Success")
                                                        .setMessage("Student registered successfully.\nID: " + id)
                                                        .setCancelable(false)
                                                        .setPositiveButton("OK", (dialog, which) -> {
                                                            dialog.dismiss();
                                                            clearFields();
                                                        })
                                                        .setIcon(android.R.drawable.ic_dialog_info)
                                                        .show();
                                            } else {
                                                String errorMsg = task.getException() != null
                                                        ? task.getException().getMessage()
                                                        : "Unknown error";
                                                new androidx.appcompat.app.AlertDialog.Builder(this)
                                                        .setTitle("Registration Failed")
                                                        .setMessage("Error adding student: " + errorMsg)
                                                        .setPositiveButton("OK", null)
                                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                                        .show();
                                            }
                                        }); // End of addOnCompleteListener
                            }) // End of addOnSuccessListener for getNextStudentId
                            .addOnFailureListener(e -> {
                                handleFailure(e);
                            });
                }) // End of addOnSuccessListener for getStudentByEmailAndDepartment
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
        btnRegister.setText("Register Student");

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("An error occurred: " + e.getMessage())
                .setPositiveButton("OK", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
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
        etBatch.setText("");
        etPassword.setText("");
        if (spDepartment.getAdapter().getCount() > 0) {
            spDepartment.setSelection(0);
        }
    }
}
