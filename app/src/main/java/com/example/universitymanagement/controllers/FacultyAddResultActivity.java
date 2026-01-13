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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;

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
    private String selectedCourseString; // Format: "Code - Sem"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_add_result);

        // Initialize DBs
        courseAssignmentDatabase = new CourseAssignmentDatabase();
        subjectDatabase = new SubjectDatabase();
        studentDatabase = new StudentDatabase();
        resultDatabase = new ResultDatabase();

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
        courseAssignmentDatabase.getAssignmentsByFaculty(facultyId).addOnSuccessListener(queryDocumentSnapshots -> {
            myAssignments = queryDocumentSnapshots.toObjects(CourseAssignment.class);
            Set<String> subjects = new HashSet<>();
            courseListDisplay = new ArrayList<>();

            // We need to fetch Subject details for each assignment to get the Code
            List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
            for (CourseAssignment ca : myAssignments) {
                tasks.add(subjectDatabase.getSubjectById(ca.getSubjectId()));
            }

            Tasks.whenAllSuccess(tasks).addOnSuccessListener(objects -> {
                for (int i = 0; i < objects.size(); i++) {
                    DocumentSnapshot snap = (DocumentSnapshot) objects.get(i);
                    Subject subject = snap.toObject(Subject.class);
                    CourseAssignment ca = myAssignments.get(i);
                    if (subject != null) {
                        String item = subject.getCode() + " - " + ca.getSemester();
                        if (!subjects.contains(item)) {
                            subjects.add(item);
                            courseListDisplay.add(item);
                        }
                    }
                }
                setupCourseSpinner();
            });
        });
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

        // Find Subject ID from Code (Need async call or cache, simplifying by
        // re-fetching or using existing list logic if optimal)
        // Since we didn't cache Subject map, let's just fetch subject by code to get ID
        subjectDatabase.getSubjectByCode(subjectCode).addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                Subject subject = queryDocumentSnapshots.getDocuments().get(0).toObject(Subject.class);
                if (subject != null) {
                    if (myAssignments != null) {
                        List<String> studentIds = new ArrayList<>();
                        for (CourseAssignment ca : myAssignments) {
                            if (ca.getSubjectId().equals(subject.getId()) && ca.getSemester().equals(semester)) {
                                studentIds.add(ca.getStudentId());
                            }
                        }
                        loadStudentDetails(studentIds);
                    }
                }
            }
        });
    }

    private void loadStudentDetails(List<String> studentIds) {
        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
        for (String id : studentIds) {
            tasks.add(studentDatabase.getStudent(id));
        }

        Tasks.whenAllSuccess(tasks).addOnSuccessListener(objects -> {
            List<Student> students = new ArrayList<>();
            for (Object obj : objects) {
                DocumentSnapshot snap = (DocumentSnapshot) obj;
                if (snap.exists()) {
                    students.add(snap.toObject(Student.class));
                }
            }
            // Setup RecyclerView
            StudentSelectionAdapter adapter = new StudentSelectionAdapter(students, student -> {
                selectedStudent = student;
                tvSelectedStudent.setText("Selected Student: " + student.getName() + " (" + student.getId() + ")");
            });
            rvStudents.setAdapter(adapter);
        });
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

        subjectDatabase.getSubjectByCode(subjectCode).addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                Subject subject = queryDocumentSnapshots.getDocuments().get(0).toObject(Subject.class);
                if (subject != null) {
                    Result result = new Result();
                    result.setStudentId(selectedStudent.getId());
                    result.setSubjectId(subject.getId());
                    result.setMarks(marks);
                    result.setGrade(grade);
                    result.setSemester(semester);

                    resultDatabase.addResult(result).addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Published: " + grade, Toast.LENGTH_SHORT).show();
                        etMarks.setText("");
                        selectedStudent = null;
                        tvSelectedStudent.setText("Selected Student: None");
                        // Ideally refresh list or selection
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to publish result", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}
