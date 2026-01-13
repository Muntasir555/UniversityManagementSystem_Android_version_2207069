package com.example.universitymanagement.controllers;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universitymanagement.R;
import com.example.universitymanagement.adapters.FacultyAdapter;
import com.example.universitymanagement.database.FacultyDatabase;

public class FacultyListActivity extends AppCompatActivity {

    private FacultyDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_list);

        android.widget.Spinner spDepartmentFilter = findViewById(R.id.spDepartmentFilter);
        RecyclerView rvFacultyList = findViewById(R.id.rvFacultyList);
        rvFacultyList.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        String[] departments = { "All", "CSE", "EEE", "Civil", "ME", "Physics", "Math", "Chemistry" };
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, departments);
        spDepartmentFilter.setAdapter(adapter);

        db = new FacultyDatabase(this);

        spDepartmentFilter.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position,
                    long id) {
                String selectedDept = departments[position];
                if (selectedDept.equals("All")) {
                    loadAllFaculty(rvFacultyList);
                } else {
                    loadFacultyByDept(rvFacultyList, selectedDept);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
    }

    private void loadAllFaculty(RecyclerView rv) {
        new Thread(() -> {
            java.util.List<com.example.universitymanagement.models.Faculty> facultyList = db.getAllFaculty();
            
            runOnUiThread(() -> {
                FacultyAdapter adapter = new FacultyAdapter(facultyList,
                        faculty -> showDeleteConfirmation(faculty, rv));
                rv.setAdapter(adapter);
            });
        }).start();
    }

    private void loadFacultyByDept(RecyclerView rv, String dept) {
        new Thread(() -> {
            java.util.List<com.example.universitymanagement.models.Faculty> facultyList = db.getFacultyByDepartment(dept);
            
            runOnUiThread(() -> {
                FacultyAdapter adapter = new FacultyAdapter(facultyList,
                        faculty -> showDeleteConfirmation(faculty, rv));
                rv.setAdapter(adapter);
            });
        }).start();
    }

    private void showDeleteConfirmation(com.example.universitymanagement.models.Faculty faculty,
            RecyclerView rv) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Faculty")
                .setMessage("Do you really want to delete " + faculty.getName() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    new Thread(() -> {
                        boolean success = db.deleteFaculty(faculty.getId());
                        
                        runOnUiThread(() -> {
                            if (success) {
                                android.widget.Toast.makeText(FacultyListActivity.this, "Faculty deleted",
                                        android.widget.Toast.LENGTH_SHORT).show();
                                // Refresh the list
                                android.widget.Spinner spDepartmentFilter = findViewById(R.id.spDepartmentFilter);
                                String selectedDept = spDepartmentFilter.getSelectedItem().toString();
                                if (selectedDept.equals("All")) {
                                    loadAllFaculty(rv);
                                } else {
                                    loadFacultyByDept(rv, selectedDept);
                                }
                            } else {
                                android.widget.Toast.makeText(FacultyListActivity.this, "Error deleting faculty",
                                        android.widget.Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).start();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
