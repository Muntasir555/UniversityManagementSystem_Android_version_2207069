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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;

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

        courseAssignmentDatabase = new CourseAssignmentDatabase();
        subjectDatabase = new SubjectDatabase();

        loadCourses();
    }

    private void loadCourses() {
        if (Session.currentFaculty == null) {
            Toast.makeText(this, "Not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        courseAssignmentDatabase.getAssignmentsByFaculty(Session.currentFaculty.getId())
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<CourseAssignment> assignments = queryDocumentSnapshots.toObjects(CourseAssignment.class);
                    Set<String> addedSubjectIds = new HashSet<>();
                    List<Task<DocumentSnapshot>> tasks = new ArrayList<>();

                    for (CourseAssignment ca : assignments) {
                        if (!addedSubjectIds.contains(ca.getSubjectId())) {
                            addedSubjectIds.add(ca.getSubjectId());
                            tasks.add(subjectDatabase.getSubjectById(ca.getSubjectId()));
                        }
                    }

                    if (tasks.isEmpty()) {
                        Toast.makeText(this, "No courses assigned.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Tasks.whenAllSuccess(tasks).addOnSuccessListener(objects -> {
                        List<Subject> subjects = new ArrayList<>();
                        for (Object obj : objects) {
                            DocumentSnapshot snap = (DocumentSnapshot) obj;
                            if (snap.exists()) {
                                subjects.add(snap.toObject(Subject.class));
                            }
                        }
                        SubjectAdapter adapter = new SubjectAdapter(subjects);
                        rvCourses.setAdapter(adapter);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Error fetching course details", Toast.LENGTH_SHORT).show();
                    });

                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching assignments", Toast.LENGTH_SHORT).show();
                });
    }
}
