package com.example.universitymanagement.controllers;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universitymanagement.R;
import com.example.universitymanagement.adapters.StudentListAdapter;
import com.example.universitymanagement.database.StudentDatabase;
import com.example.universitymanagement.models.Student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StudentListActivity extends AppCompatActivity {

    private Spinner spDepartmentFilter, spBatchFilter;
    private RecyclerView rvStudentList;
    private android.widget.ProgressBar progressBar;
    private android.widget.TextView tvEmptyState;
    private StudentDatabase studentDatabase;
    private List<Student> allStudentsCache = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        studentDatabase = new StudentDatabase(this);

        spDepartmentFilter = findViewById(R.id.spDepartmentFilter);
        spBatchFilter = findViewById(R.id.spBatchFilter);
        rvStudentList = findViewById(R.id.rvStudentList);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        rvStudentList.setLayoutManager(new LinearLayoutManager(this));

        setupDepartmentFilter();
        loadBatchesAndInitialData();
    }

    private void setupDepartmentFilter() {
        String[] departments = { "All", "CSE", "EEE", "Civil", "ME", "Physics", "Math", "Chemistry" };
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                departments);
        spDepartmentFilter.setAdapter(deptAdapter);

        spDepartmentFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterStudents();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadBatchesAndInitialData() {
        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);
        rvStudentList.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.GONE);
        
        android.util.Log.d("StudentList", "Loading students...");
        
        // Fetch all students in background thread
        new Thread(() -> {
            List<Student> students = studentDatabase.getAllStudents();
            
            runOnUiThread(() -> {
                android.util.Log.d("StudentList", "Students loaded: " + students.size());
                
                allStudentsCache = students;

                // Hide loading, show RecyclerView
                progressBar.setVisibility(View.GONE);
                rvStudentList.setVisibility(View.VISIBLE);

                if (allStudentsCache.isEmpty()) {
                    tvEmptyState.setVisibility(View.VISIBLE);
                    android.util.Log.d("StudentList", "No students in database");
                    return;
                }

                Set<String> batches = new HashSet<>();
                for (Student s : allStudentsCache) {
                    if (s.getBatch() != null)
                        batches.add(s.getBatch());
                }
                List<String> batchList = new ArrayList<>(batches);
                Collections.sort(batchList);
                batchList.add(0, "All");

                ArrayAdapter<String> batchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                        batchList);
                spBatchFilter.setAdapter(batchAdapter);

                spBatchFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        filterStudents();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                // Initial Filter
                filterStudents();
            });
        }).start();
    }

    private void filterStudents() {
        if (spDepartmentFilter.getSelectedItem() == null || spBatchFilter.getSelectedItem() == null)
            return;

        String dept = spDepartmentFilter.getSelectedItem().toString();
        String batch = spBatchFilter.getSelectedItem().toString();

        List<Student> filteredList = new ArrayList<>();

        for (Student s : allStudentsCache) {
            boolean deptMatch = dept.equals("All")
                    || (s.getDepartment() != null && s.getDepartment().equalsIgnoreCase(dept));
            boolean batchMatch = batch.equals("All") || (s.getBatch() != null && s.getBatch().equalsIgnoreCase(batch));

            if (deptMatch && batchMatch) {
                filteredList.add(s);
            }
        }

        StudentListAdapter adapter = new StudentListAdapter(filteredList);
        rvStudentList.setAdapter(adapter);
        
        // Show/hide empty state
        if (filteredList.isEmpty()) {
            tvEmptyState.setText("No students found for selected filters");
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
        }
        
        android.util.Log.d("StudentList", "Filtered students: " + filteredList.size() + " (Dept: " + dept + ", Batch: " + batch + ")");
    }
}
