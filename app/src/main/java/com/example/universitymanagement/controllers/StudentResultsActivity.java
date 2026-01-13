package com.example.universitymanagement.controllers;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universitymanagement.R;
import com.example.universitymanagement.adapters.StudentResultsAdapter;
import com.example.universitymanagement.database.ResultDatabase;
import com.example.universitymanagement.database.SubjectDatabase;
import com.example.universitymanagement.models.Result;
import com.example.universitymanagement.models.Subject;
import com.example.universitymanagement.util.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentResultsActivity extends AppCompatActivity {

    private RecyclerView rvResultsList;
    private ResultDatabase resultDatabase;
    private SubjectDatabase subjectDatabase;

    private android.widget.TextView tvCgpaLabel, tvSemesterGpaLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_results);

        if (Session.currentStudent == null) {
            Toast.makeText(this, "Session Expired", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvCgpaLabel = findViewById(R.id.tvCgpaLabel);
        tvSemesterGpaLabel = findViewById(R.id.tvSemesterGpaLabel);
        rvResultsList = findViewById(R.id.rvResultsList);
        rvResultsList.setLayoutManager(new LinearLayoutManager(this));

        resultDatabase = new ResultDatabase();
        subjectDatabase = new SubjectDatabase();

        loadData();
    }

    private void loadData() {
        // Fetch Subjects first for mapping
        subjectDatabase.getAllSubjects().addOnSuccessListener(subjectSnapshots -> {
            Map<String, Subject> subjectMap = new HashMap<>();
            List<Subject> subjects = subjectSnapshots.toObjects(Subject.class);
            for (Subject s : subjects) {
                subjectMap.put(s.getId(), s);
            }

            fetchResults(subjectMap);

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error loading subjects: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchResults(Map<String, Subject> subjectMap) {
        resultDatabase.getResultsByStudentId(Session.currentStudent.getId()).addOnSuccessListener(resultSnapshots -> {
            List<Result> results = resultSnapshots.toObjects(Result.class);

            if (results.isEmpty()) {
                Toast.makeText(this, "No results found.", Toast.LENGTH_SHORT).show();
            }

            StudentResultsAdapter adapter = new StudentResultsAdapter(results, subjectMap);
            rvResultsList.setAdapter(adapter);

            // Display CGPA
            tvCgpaLabel.setText(String.format("CGPA: %.2f", Session.currentStudent.getCgpa()));

            // Calculate & Display Semester GPAs
            calculateSemesterGPAs(results, subjectMap);

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error loading results: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void calculateSemesterGPAs(List<Result> results, Map<String, Subject> subjectMap) {
        Map<String, List<Result>> bySemester = new HashMap<>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            bySemester = results.stream()
                    .filter(r -> r.getSemester() != null)
                    .collect(java.util.stream.Collectors.groupingBy(Result::getSemester));
        } else {
            // Basic compatible grouping
            for (Result r : results) {
                if (r.getSemester() != null) {
                    if (!bySemester.containsKey(r.getSemester())) {
                        bySemester.put(r.getSemester(), new java.util.ArrayList<>());
                    }
                    bySemester.get(r.getSemester()).add(r);
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<Result>> entry : bySemester.entrySet()) {
            String sem = entry.getKey();
            List<Result> resList = entry.getValue();

            double totalPoints = 0;
            int totalCredits = 0;

            for (Result r : resList) {
                Subject s = subjectMap.get(r.getSubjectId());
                if (s != null) {
                    double gp = com.example.universitymanagement.util.CGPAUtility.calculateGradePoint(r.getGrade());
                    totalPoints += gp * s.getCredit();
                    totalCredits += s.getCredit();
                }
            }
            double gpa = (totalCredits > 0) ? totalPoints / totalCredits : 0.0;
            sb.append(String.format("Sem %s: %.2f | ", sem, gpa));
        }
        tvSemesterGpaLabel.setText(sb.toString());
    }
}
