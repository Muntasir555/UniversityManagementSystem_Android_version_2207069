package com.example.universitymanagement.controllers;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universitymanagement.R;
import com.example.universitymanagement.adapters.StudentSelectionAdapter;
import com.example.universitymanagement.database.CourseAssignmentDatabase;
import com.example.universitymanagement.database.ResultDatabase;
import com.example.universitymanagement.database.StudentDatabase;
import com.example.universitymanagement.database.SubjectDatabase;
import com.example.universitymanagement.models.CourseAssignment;
import com.example.universitymanagement.models.Result;
import com.example.universitymanagement.models.Student;
import com.example.universitymanagement.models.Subject;
import com.example.universitymanagement.util.CGPAUtility;
import com.example.universitymanagement.util.Session;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FacultyAddResultActivity extends AppCompatActivity {

    private Spinner spCourses;
    private RecyclerView rvStudents;
    private TextView tvSelectedStudent;
    private EditText etMarks;
    private Button btnPublish;

    private CourseAssignmentDatabase courseAssignmentDatabase;
    private SubjectDatabase subjectDatabase;
    private StudentDatabase studentDatabase;
    private ResultDatabase resultDatabase;

    private List<CourseAssignment> myAssignments;
    private List<String> courseListDisplay;
    private Student selectedStudent;
    private String selectedCourseString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_add_result);

        // Initialize DBs
        courseAssignmentDatabase = new CourseAssignmentDatabase(this);
        subjectDatabase = new SubjectDatabase(this);
        studentDatabase = new StudentDatabase(this);
        resultDatabase = new ResultDatabase(this);

        // Initialize Views
        spCourses = findViewById(R.id.spCourses);
        rvStudents = findViewById(R.id.rvStudents);
        tvSelectedStudent = findViewById(R.id.tvSelectedStudent);
        etMarks = findViewById(R.id.etMarks);
        btnPublish = findViewById(R.id.btnPublish);

        rvStudents.setLayoutManager(new LinearLayoutManager(this));

        loadCourses();

        btnPublish.setOnClickListener(v -> publishResult());
    }

    private void loadCourses() {
        if (Session.currentFaculty == null) {
            Toast.makeText(this, "Not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String facultyId = Session.currentFaculty.getId();
        
        new Thread(() -> {
            myAssignments = courseAssignmentDatabase.getAssignmentsByFaculty(facultyId);
            Set<String> subjects = new HashSet<>();
            courseListDisplay = new ArrayList<>();

            // Fetch Subject details for each assignment
            for (CourseAssignment ca : myAssignments) {
                Subject subject = subjectDatabase.getSubjectById(ca.getSubjectId());
                if (subject != null) {
                    String item = subject.getCode() + " - " + ca.getSemester();
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
                loadStudentsForCourse(selectedCourseString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadStudentsForCourse(String courseString) {
        String[] parts = courseString.split(" - ");
        String subjectCode = parts[0];
        String semester = parts[1];

        new Thread(() -> {
            Subject subject = subjectDatabase.getSubjectByCode(subjectCode);
            
            if (subject != null && myAssignments != null) {
                List<String> studentIds = new ArrayList<>();
                android.util.Log.d("FacultyAddResult", "Loading students for Subject: " + subjectCode + ", Semester: " + semester);
                android.util.Log.d("FacultyAddResult", "Subject ID: " + subject.getId());
                android.util.Log.d("FacultyAddResult", "Total assignments: " + myAssignments.size());
                
                for (CourseAssignment ca : myAssignments) {
                    android.util.Log.d("FacultyAddResult", "Assignment - SubjectID: " + ca.getSubjectId() + ", Semester: " + ca.getSemester() + ", StudentID: " + ca.getStudentId());
                    if (ca.getSubjectId().equals(subject.getId()) && ca.getSemester().equals(semester)) {
                        studentIds.add(ca.getStudentId());
                        android.util.Log.d("FacultyAddResult", "Matched student: " + ca.getStudentId());
                    }
                }
                
                android.util.Log.d("FacultyAddResult", "Total students found: " + studentIds.size());
                loadStudentDetails(studentIds);
            } else {
                android.util.Log.e("FacultyAddResult", "Subject not found or no assignments");
                runOnUiThread(() -> {
                    Toast.makeText(this, "No students found for this course", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void loadStudentDetails(List<String> studentIds) {
        new Thread(() -> {
            List<Student> students = new ArrayList<>();
            android.util.Log.d("FacultyAddResult", "Loading details for " + studentIds.size() + " students");
            
            for (String id : studentIds) {
                android.util.Log.d("FacultyAddResult", "Fetching student: " + id);
                Student student = studentDatabase.getStudent(id);
                if (student != null) {
                    students.add(student);
                    android.util.Log.d("FacultyAddResult", "Student found: " + student.getName() + " (" + student.getId() + ")");
                } else {
                    android.util.Log.e("FacultyAddResult", "Student NOT found in database: " + id);
                }
            }
            
            android.util.Log.d("FacultyAddResult", "Total students loaded: " + students.size());
            
            runOnUiThread(() -> {
                if (students.isEmpty()) {
                    Toast.makeText(this, "No students assigned to this course. Please check course assignments.", Toast.LENGTH_LONG).show();
                    android.util.Log.w("FacultyAddResult", "No students to display!");
                }
                
                // Setup RecyclerView
                StudentSelectionAdapter adapter = new StudentSelectionAdapter(students, student -> {
                    selectedStudent = student;
                    tvSelectedStudent.setText("Selected Student: " + student.getName() + " (" + student.getId() + ")");
                });
                rvStudents.setAdapter(adapter);
                
                // Show count
                Toast.makeText(this, students.size() + " student(s) found for this course", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    private void publishResult() {
        if (selectedStudent == null || selectedCourseString == null || etMarks.getText().toString().isEmpty()) {
            Toast.makeText(this, "Select Student, Course and enter Marks", Toast.LENGTH_SHORT).show();
            return;
        }

        double marks;
        try {
            marks = Double.parseDouble(etMarks.getText().toString());
            if (marks < 0 || marks > 100) {
                Toast.makeText(this, "Marks must be between 0 and 100", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid Marks", Toast.LENGTH_SHORT).show();
            return;
        }

        String grade = CGPAUtility.calculateGrade(marks);
        String[] parts = selectedCourseString.split(" - ");
        String subjectCode = parts[0];
        String semester = parts[1];

        new Thread(() -> {
            Subject subject = subjectDatabase.getSubjectByCode(subjectCode);
            
            if (subject != null) {
                Result result = new Result();
                result.setStudentId(selectedStudent.getId());
                result.setSubjectId(subject.getId());
                result.setMarks(marks);
                result.setGrade(grade);
                result.setSemester(semester);

                boolean success = resultDatabase.addResult(result);
                
                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(this, "Published: " + grade, Toast.LENGTH_SHORT).show();
                        etMarks.setText("");
                        selectedStudent = null;
                        tvSelectedStudent.setText("Selected Student: None");
                    } else {
                        Toast.makeText(this, "Failed to publish result", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
}
