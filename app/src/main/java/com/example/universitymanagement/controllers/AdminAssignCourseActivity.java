package com.example.universitymanagement.controllers;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universitymanagement.R;

import com.example.universitymanagement.R;

import com.example.universitymanagement.database.CourseAssignmentDatabase;
import com.example.universitymanagement.database.FacultyDatabase;
import com.example.universitymanagement.database.StudentDatabase;
import com.example.universitymanagement.database.SubjectDatabase;
import com.example.universitymanagement.models.CourseAssignment;
import com.example.universitymanagement.models.Faculty;
import com.example.universitymanagement.models.Student;
import com.example.universitymanagement.models.Subject;
import com.example.universitymanagement.util.Constants;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class AdminAssignCourseActivity extends AppCompatActivity {

    private Spinner departmentSpinner;
    private Spinner batchSpinner;
    private Spinner semesterSpinner;
    private LinearLayout courseContainer;
    private TextView statusLabel;
    private Button btnAddCourse, btnAssignAll;

    private StudentDatabase studentDatabase;
    private FacultyDatabase facultyDatabase;
    private SubjectDatabase subjectDatabase;
    private CourseAssignmentDatabase courseAssignmentDatabase;

    private List<Faculty> allFaculty = new ArrayList<>();
    private List<String> facultyNames = new ArrayList<>(); // formatted names for spinner
    private ArrayAdapter<String> batchAdapter;
    private List<String> batchList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_assign_course);

        // Initialize Databases
        studentDatabase = new StudentDatabase();
        facultyDatabase = new FacultyDatabase();
        subjectDatabase = new SubjectDatabase();
        courseAssignmentDatabase = new CourseAssignmentDatabase();

        // Initialize Views
        departmentSpinner = findViewById(R.id.departmentSpinner);
        batchSpinner = findViewById(R.id.batchSpinner);
        semesterSpinner = findViewById(R.id.semesterSpinner);
        courseContainer = findViewById(R.id.courseContainer);
        statusLabel = findViewById(R.id.statusLabel);
        btnAddCourse = findViewById(R.id.btnAddCourse);
        btnAssignAll = findViewById(R.id.btnAssignAll);

        setupSpinners();
        loadFaculty();
        loadBatches();

        btnAddCourse.setOnClickListener(v -> addCourseRow());
        btnAssignAll.setOnClickListener(v -> handleAssign());

        // Add initial row
        addCourseRow();
    }

    private void setupSpinners() {
        // Departments
        String[] departments = { "Select Department", "CSE", "EEE", "Civil", "ME" };
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departments);
        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(deptAdapter);

        // Semesters
        String[] semesters = { "Select Semester", "1-1", "1-2", "2-1", "2-2", "3-1", "3-2", "4-1", "4-2" };
        ArrayAdapter<String> semAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, semesters);
        semAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        semesterSpinner.setAdapter(semAdapter);

        // Batches (Empty initially)
        batchList.add("Select Batch");
        batchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, batchList);
        batchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        batchSpinner.setAdapter(batchAdapter);
    }

    private void loadFaculty() {
        facultyDatabase.getAllFaculty().addOnSuccessListener(queryDocumentSnapshots -> {
            allFaculty.clear();
            facultyNames.clear();
            facultyNames.add("Select Faculty"); // Default option
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                Faculty faculty = doc.toObject(Faculty.class);
                if (faculty != null) {
                    allFaculty.add(faculty);
                    facultyNames.add(faculty.getName() + " (" + faculty.getDepartment() + ")");
                }
            }
            // Update any existing rows? For now, logic assumes one load.
            // Ideally we should update existing spinners, but rows are added dynamically
            // *after* this usually finishes or concurrently.
            // Simplified: New rows will pick this up.
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to load faculty", Toast.LENGTH_SHORT).show());
    }

    private void loadBatches() {
        // Fetch all students to get distinct batches (Client-side distinct)
        studentDatabase.getAllStudentsForBatches().addOnSuccessListener(queryDocumentSnapshots -> {
            Set<String> batches = new HashSet<>();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                Student s = doc.toObject(Student.class);
                if (s != null && s.getBatch() != null) {
                    batches.add(s.getBatch());
                }
            }
            batchList.clear();
            batchList.add("Select Batch");
            batchList.addAll(batches);
            batchAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to load batches", Toast.LENGTH_SHORT).show());
    }

    private void addCourseRow() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 8, 0, 8);
        row.setLayoutParams(lp);

        // Code
        EditText codeEt = new EditText(this);
        codeEt.setHint("Code");
        codeEt.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f));

        // Name
        EditText nameEt = new EditText(this);
        nameEt.setHint("Subject Name");
        nameEt.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f));

        // Credit
        EditText creditEt = new EditText(this);
        creditEt.setHint("Cr");
        creditEt.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        creditEt.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.8f));

        // Faculty Spinner
        Spinner facultySp = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, facultyNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        facultySp.setAdapter(adapter);
        facultySp.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f));

        // Remove Button
        Button removeBtn = new Button(this);
        removeBtn.setText("X");
        removeBtn.setTextColor(getResources().getColor(android.R.color.white));
        removeBtn.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,
                0.7f);
        removeBtn.setLayoutParams(btnLp);
        removeBtn.setOnClickListener(v -> courseContainer.removeView(row));

        // Logic to auto-fill name/credit if code exists (simplified: require strict
        // match or user enters)
        codeEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String code = codeEt.getText().toString().trim();
                if (!code.isEmpty()) {
                    subjectDatabase.getSubjectByCode(code).addOnSuccessListener(snaps -> {
                        if (!snaps.isEmpty()) {
                            Subject s = snaps.getDocuments().get(0).toObject(Subject.class);
                            if (s != null) {
                                nameEt.setText(s.getName());
                                creditEt.setText(String.valueOf(s.getCredit()));
                            }
                        }
                    });
                }
            }
        });

        row.addView(codeEt);
        row.addView(nameEt);
        row.addView(creditEt);
        row.addView(facultySp);
        row.addView(removeBtn);

        courseContainer.addView(row);
    }

    private void handleAssign() {
        String dept = departmentSpinner.getSelectedItem().toString();
        String batch = batchSpinner.getSelectedItem().toString();
        String semester = semesterSpinner.getSelectedItem().toString();

        if (dept.startsWith("Select") || batch.startsWith("Select") || semester.startsWith("Select")) {
            statusLabel.setText("Please select Department, Batch, and Semester.");
            statusLabel.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            return;
        }

        statusLabel.setText("Processing...");

        studentDatabase.getStudentsByDepartmentAndBatch(dept, batch).addOnSuccessListener(studentSnaps -> {
            if (studentSnaps.isEmpty()) {
                statusLabel.setText("No students found for " + dept + " Batch " + batch);
                return;
            }

            List<Student> students = studentSnaps.toObjects(Student.class);
            AtomicInteger processedRows = new AtomicInteger(0);
            int rowCount = courseContainer.getChildCount();

            if (rowCount == 0) {
                statusLabel.setText("No courses added.");
                return;
            }

            for (int i = 0; i < rowCount; i++) {
                View child = courseContainer.getChildAt(i);
                if (child instanceof LinearLayout) {
                    processRow((LinearLayout) child, dept, semester, students);
                }
            }
        }).addOnFailureListener(e -> statusLabel.setText("Error fetching students: " + e.getMessage()));
    }

    private void processRow(LinearLayout row, String dept, String semester, List<Student> students) {
        EditText codeEt = (EditText) row.getChildAt(0);
        EditText nameEt = (EditText) row.getChildAt(1);
        EditText creditEt = (EditText) row.getChildAt(2);
        Spinner facultySp = (Spinner) row.getChildAt(3);

        String code = codeEt.getText().toString().trim();
        String name = nameEt.getText().toString().trim();
        String creditStr = creditEt.getText().toString().trim();
        int facultyIdx = facultySp.getSelectedItemPosition();

        if (TextUtils.isEmpty(code) || facultyIdx <= 0) { // 0 is "Select Faculty"
            logStatus("Skipped incomplete row: " + code);
            return;
        }

        // Get actual Faculty object (Offset by 1 due to "Select Faculty")
        Faculty faculty = (facultyIdx - 1 < allFaculty.size()) ? allFaculty.get(facultyIdx - 1) : null;
        if (faculty == null)
            return;

        int credit = 0;
        try {
            credit = Integer.parseInt(creditStr);
        } catch (NumberFormatException e) {
            logStatus("Invalid credit for " + code);
            return;
        }

        // Check or Create Subject
        int finalCredit = credit;
        subjectDatabase.getSubjectByCode(code).addOnSuccessListener(snaps -> {
            Subject subjectToAssign;
            if (!snaps.isEmpty()) {
                subjectToAssign = snaps.getDocuments().get(0).toObject(Subject.class);
                assignToStudents(subjectToAssign, faculty, semester, students);
            } else {
                // Create new Subject
                Subject newSub = new Subject();
                newSub.setCode(code);
                newSub.setName(name);
                newSub.setCredit(finalCredit);
                newSub.setDepartment(dept);

                subjectDatabase.addSubject(newSub).addOnSuccessListener(v -> {
                    assignToStudents(newSub, faculty, semester, students); // Note: ID is set in addSubject but ensures
                                                                           // object ref has it
                }).addOnFailureListener(e -> logStatus("Failed to create subject " + code));
            }
        });
    }

    private void assignToStudents(Subject subject, Faculty faculty, String semester, List<Student> students) {
        for (Student s : students) {
            CourseAssignment ca = new CourseAssignment();
            ca.setStudentId(s.getId());
            ca.setSubjectId(subject.getId());
            ca.setFacultyId(faculty.getId());
            ca.setSemester(semester);
            ca.setTimestamp(Timestamp.now());

            courseAssignmentDatabase.assignCourse(ca); // Fire and forget for bulk, or track count
        }
        logStatus("Assigned " + subject.getCode() + " to " + students.size() + " students.");
    }

    private void logStatus(String msg) {
        runOnUiThread(() -> {
            String current = statusLabel.getText().toString();
            if (current.equals("Processing..."))
                current = "";
            statusLabel.setText(current + "\n" + msg);
        });
    }
}
