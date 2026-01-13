package com.example.universitymanagement.controllers;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universitymanagement.R;
import com.example.universitymanagement.adapters.AttendanceAdapter;
import com.example.universitymanagement.database.AttendanceDatabase;
import com.example.universitymanagement.database.CourseAssignmentDatabase;
import com.example.universitymanagement.database.StudentDatabase;
import com.example.universitymanagement.database.SubjectDatabase;
import com.example.universitymanagement.models.Attendance;
import com.example.universitymanagement.models.CourseAssignment;
import com.example.universitymanagement.models.Student;
import com.example.universitymanagement.models.Subject;
import com.example.universitymanagement.util.Session;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class FacultyAttendanceActivity extends AppCompatActivity {

    private Spinner spCourses;
    private TextView tvDate;
    private Button btnPickDate, btnSaveAttendance;
    private RecyclerView rvAttendance;

    private CourseAssignmentDatabase courseAssignmentDatabase;
    private SubjectDatabase subjectDatabase;
    private StudentDatabase studentDatabase;
    private AttendanceDatabase attendanceDatabase;

    private List<CourseAssignment> myAssignments;
    private List<String> courseListDisplay;
    private List<Student> currentStudentList;
    private AttendanceAdapter adapter;

    private Calendar selectedDateCalendar;
    private String selectedCourseString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_attendance);

        // Init DB
        courseAssignmentDatabase = new CourseAssignmentDatabase(this);
        subjectDatabase = new SubjectDatabase(this);
        studentDatabase = new StudentDatabase(this);
        attendanceDatabase = new AttendanceDatabase(this);

        // Init Views
        spCourses = findViewById(R.id.spCourses);
        tvDate = findViewById(R.id.tvDate);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnSaveAttendance = findViewById(R.id.btnSaveAttendance);
        rvAttendance = findViewById(R.id.rvAttendance);
        rvAttendance.setLayoutManager(new LinearLayoutManager(this));

        // Default Date = Today
        selectedDateCalendar = Calendar.getInstance();
        updateDateLabel();

        // Listeners
        btnPickDate.setOnClickListener(v -> showDatePicker());
        btnSaveAttendance.setOnClickListener(v -> saveAttendance());

        loadCourses();
    }

    private void updateDateLabel() {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tvDate.setText(sdf.format(selectedDateCalendar.getTime()));
    }

    private void showDatePicker() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDateCalendar.set(Calendar.YEAR, year);
            selectedDateCalendar.set(Calendar.MONTH, month);
            selectedDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel();
        }, selectedDateCalendar.get(Calendar.YEAR), selectedDateCalendar.get(Calendar.MONTH),
                selectedDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void loadCourses() {
        if (Session.currentFaculty == null) {
            Toast.makeText(this, "Not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        new Thread(() -> {
            myAssignments = courseAssignmentDatabase.getAssignmentsByFaculty(Session.currentFaculty.getId());
            Set<String> subjects = new HashSet<>();
            courseListDisplay = new ArrayList<>();

            // Fetch Subject details for each assignment to get the Code
            for (CourseAssignment ca : myAssignments) {
                Subject subject = subjectDatabase.getSubjectById(ca.getSubjectId());
                if (subject != null) {
                    String item = subject.getCode() + " - " + ca.getSemester();
                    // De-duplicate in display if same subject+sem assigned multiple times
                    if (!subjects.contains(item)) {
                        subjects.add(item);
                        courseListDisplay.add(item);
                    }
                }
            }
            
            runOnUiThread(this::setupCourseSpinner);
        }).start();
    }

    private void setupCourseSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                courseListDisplay);
        spCourses.setAdapter(adapter);

        spCourses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCourseString = courseListDisplay.get(position);
                loadStudents();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadStudents() {
        if (selectedCourseString == null)
            return;

        String[] parts = selectedCourseString.split(" - ");
        String subjectCode = parts[0];
        String semester = parts[1];

        new Thread(() -> {
            // Need Subject ID first
            Subject subject = subjectDatabase.getSubjectByCode(subjectCode);
            
            if (subject != null) {
                // Filter assignments to find students in this subject + semester
                List<String> validStudentIds = new ArrayList<>();
                for (CourseAssignment ca : myAssignments) {
                    if (ca.getSubjectId().equals(subject.getId()) && ca.getSemester().equals(semester)) {
                        validStudentIds.add(ca.getStudentId());
                    }
                }
                populateStudentList(validStudentIds);
            }
        }).start();
    }

    private void populateStudentList(List<String> studentIds) {
        if (studentIds.isEmpty()) {
            runOnUiThread(() -> {
                currentStudentList = new ArrayList<>();
                adapter = new AttendanceAdapter(currentStudentList);
                rvAttendance.setAdapter(adapter);
            });
            return;
        }

        new Thread(() -> {
            currentStudentList = new ArrayList<>();
            for (String id : studentIds) {
                Student student = studentDatabase.getStudent(id);
                if (student != null) {
                    currentStudentList.add(student);
                }
            }
            
            runOnUiThread(() -> {
                adapter = new AttendanceAdapter(currentStudentList);
                rvAttendance.setAdapter(adapter);
            });
        }).start();
    }

    private void saveAttendance() {
        if (adapter == null || currentStudentList == null || currentStudentList.isEmpty()) {
            Toast.makeText(this, "No students loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Boolean> statusMap = adapter.getAttendanceStatus(); // StudentID -> IsPresent
        String dateStr = tvDate.getText().toString();

        String[] parts = selectedCourseString.split(" - ");
        String subjectCode = parts[0];

        new Thread(() -> {
            Subject subject = subjectDatabase.getSubjectByCode(subjectCode);
            
            if (subject != null) {
                String subjectId = subject.getId();

                int successCount = 0;
                for (Map.Entry<String, Boolean> entry : statusMap.entrySet()) {
                    String sId = entry.getKey();
                    boolean isPresent = entry.getValue();

                    Attendance att = new Attendance();
                    att.setStudentId(sId);
                    att.setSubjectId(subjectId);
                    att.setDate(dateStr);
                    att.setStatus(isPresent ? "Present" : "Absent");

                    if (attendanceDatabase.markAttendance(att)) {
                        successCount++;
                    }
                }

                int finalCount = successCount;
                runOnUiThread(() -> {
                    Toast.makeText(this, "Attendance Saved Successfully! (" + finalCount + " records)", Toast.LENGTH_SHORT).show();
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error: Subject not found", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}
