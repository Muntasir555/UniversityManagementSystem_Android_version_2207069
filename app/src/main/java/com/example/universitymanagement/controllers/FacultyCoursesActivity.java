package com.example.universitymanagement.controllers;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universitymanagement.R;
import com.example.universitymanagement.adapters.SubjectAdapter;
import com.example.universitymanagement.database.CourseAssignmentDatabase;
import com.example.universitymanagement.database.SubjectDatabase;
import com.example.universitymanagement.models.CourseAssignment;
import com.example.universitymanagement.models.Subject;
import com.example.universitymanagement.util.Session;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FacultyCoursesActivity extends AppCompatActivity {

    private RecyclerView rvCourses;
    private CourseAssignmentDatabase courseAssignmentDatabase;
    private SubjectDatabase subjectDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_courses);

        rvCourses = findViewById(R.id.rvCourses);
        rvCourses.setLayoutManager(new LinearLayoutManager(this));

        courseAssignmentDatabase = new CourseAssignmentDatabase(this);
        subjectDatabase = new SubjectDatabase(this);

        loadCourses();
    }

    private void loadCourses() {
        if (Session.currentFaculty == null) {
            Toast.makeText(this, "Not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        new Thread(() -> {
            List<CourseAssignment> assignments = courseAssignmentDatabase.getAssignmentsByFaculty(Session.currentFaculty.getId());
            Set<String> addedSubjectIds = new HashSet<>();
            List<Subject> subjects = new ArrayList<>();

            for (CourseAssignment ca : assignments) {
                if (!addedSubjectIds.contains(ca.getSubjectId())) {
                    addedSubjectIds.add(ca.getSubjectId());
                    Subject subject = subjectDatabase.getSubjectById(ca.getSubjectId());
                    if (subject != null) {
                        subjects.add(subject);
                    }
                }
            }

            runOnUiThread(() -> {
                if (subjects.isEmpty()) {
                    Toast.makeText(this, "No courses assigned.", Toast.LENGTH_SHORT).show();
                } else {
                    SubjectAdapter adapter = new SubjectAdapter(subjects);
                    rvCourses.setAdapter(adapter);
                }
            });
        }).start();
    }
}
