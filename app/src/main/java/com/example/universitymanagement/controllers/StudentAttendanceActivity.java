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

        attendanceDatabase = new AttendanceDatabase(this);
        subjectDatabase = new SubjectDatabase(this);

        loadData();
    }

    private void loadData() {
        new Thread(() -> {
            // First fetch all subjects to map ID -> Name
            List<Subject> subjects = subjectDatabase.getAllSubjects();
            Map<String, String> subjectMap = new HashMap<>();
            for (Subject s : subjects) {
                subjectMap.put(s.getId(), s.getName());
            }

            // Then fetch attendance
            Student student = Session.currentStudent;
            List<Attendance> attendanceList = attendanceDatabase.getAttendanceByStudentId(student.getId());

            runOnUiThread(() -> {
                if (attendanceList.isEmpty()) {
                    Toast.makeText(this, "No attendance records found.", Toast.LENGTH_SHORT).show();
                }

                StudentAttendanceAdapter adapter = new StudentAttendanceAdapter(attendanceList, subjectMap);
                rvAttendanceList.setAdapter(adapter);
            });
        }).start();
    }
}
