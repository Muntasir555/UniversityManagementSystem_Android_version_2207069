package com.example.universitymanagement.controllers;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universitymanagement.R;
import com.example.universitymanagement.adapters.StudentAttendanceAdapter;
import com.example.universitymanagement.database.AttendanceDatabase;
import com.example.universitymanagement.database.SubjectDatabase;
import com.example.universitymanagement.models.Attendance;
import com.example.universitymanagement.models.Subject;
import com.example.universitymanagement.models.Student;
import com.example.universitymanagement.util.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentAttendanceActivity extends AppCompatActivity {

    private RecyclerView rvAttendanceList;
    private AttendanceDatabase attendanceDatabase;
    private SubjectDatabase subjectDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance);

        if (Session.currentStudent == null) {
            Toast.makeText(this, "Session Expired", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvAttendanceList = findViewById(R.id.rvAttendanceList);
        rvAttendanceList.setLayoutManager(new LinearLayoutManager(this));

        attendanceDatabase = new AttendanceDatabase();
        subjectDatabase = new SubjectDatabase();

        loadData();
    }

    private void loadData() {
        // First fetch all subjects to map ID -> Name
        subjectDatabase.getAllSubjects().addOnSuccessListener(subjectSnapshots -> {
            Map<String, String> subjectMap = new HashMap<>();
            List<Subject> subjects = subjectSnapshots.toObjects(Subject.class);
            for (Subject s : subjects) {
                subjectMap.put(s.getId(), s.getName());
            }

            // Then fetch attendance
            fetchAttendance(subjectMap);

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error loading subjects: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchAttendance(Map<String, String> subjectMap) {
        Student student = Session.currentStudent;
        attendanceDatabase.getAttendanceByStudentId(student.getId()).addOnSuccessListener(attendanceSnapshots -> {
            List<Attendance> attendanceList = attendanceSnapshots.toObjects(Attendance.class);

            if (attendanceList.isEmpty()) {
                Toast.makeText(this, "No attendance records found.", Toast.LENGTH_SHORT).show();
            }

            StudentAttendanceAdapter adapter = new StudentAttendanceAdapter(attendanceList, subjectMap);
            rvAttendanceList.setAdapter(adapter);

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error loading attendance: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
