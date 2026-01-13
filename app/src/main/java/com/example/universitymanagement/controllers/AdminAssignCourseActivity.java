package com.example.universitymanagement.controllers;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universitymanagement.R;
import com.example.universitymanagement.database.CourseAssignmentDatabase;
import com.example.universitymanagement.database.FacultyDatabase;
import com.example.universitymanagement.database.StudentDatabase;
import com.example.universitymanagement.database.SubjectDatabase;
import com.example.universitymanagement.models.CourseAssignment;
import com.example.universitymanagement.models.Faculty;
import com.example.universitymanagement.models.Student;
import com.example.universitymanagement.models.Subject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private List<String> facultyNames = new ArrayList<>();
    private ArrayAdapter<String> batchAdapter;
    private List<String> batchList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_assign_course);

        // Initialize Databases
        studentDatabase = new StudentDatabase(this);
        facultyDatabase = new FacultyDatabase(this);
        subjectDatabase = new SubjectDatabase(this);
        courseAssignmentDatabase = new CourseAssignmentDatabase(this);

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
        String[] departments = { "Select Department", "CSE", "EEE", "Civil", "ME", "Physics", "Math", "Chemistry" };
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
        new Thread(() -> {
            List<Faculty> facultyList = facultyDatabase.getAllFaculty();
            
            runOnUiThread(() -> {
                allFaculty.clear();
                facultyNames.clear();
                facultyNames.add("Select Faculty");
                
                for (Faculty faculty : facultyList) {
                    allFaculty.add(faculty);
                    facultyNames.add(faculty.getName() + " (" + faculty.getDepartment() + ")");
                }
            });
        }).start();
    }

    private void loadBatches() {
        new Thread(() -> {
            List<Student> students = studentDatabase.getAllStudents();
            Set<String> batches = new HashSet<>();
            
            for (Student s : students) {
                if (s.getBatch() != null) {
                    batches.add(s.getBatch());
                }
            }
            
            runOnUiThread(() -> {
                batchList.clear();
                batchList.add("Select Batch");
                batchList.addAll(batches);
                batchAdapter.notifyDataSetChanged();
            });
        }).start();
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
        creditEt.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
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
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.7f);
        removeBtn.setLayoutParams(btnLp);
        removeBtn.setOnClickListener(v -> courseContainer.removeView(row));

        // Auto-fill name/credit if subject code exists
        codeEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String code = codeEt.getText().toString().trim();
                if (!code.isEmpty()) {
                    new Thread(() -> {
                        Subject subject = subjectDatabase.getSubjectByCode(code);
                        if (subject != null) {
                            runOnUiThread(() -> {
                                nameEt.setText(subject.getName());
                                creditEt.setText(String.valueOf(subject.getCredits()));
                            });
                        }
                    }).start();
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

        new Thread(() -> {
            List<Student> students = studentDatabase.getStudentsByDepartmentAndBatch(dept, batch);
            
            if (students.isEmpty()) {
                runOnUiThread(() -> statusLabel.setText("No students found for " + dept + " Batch " + batch));
                return;
            }

            int rowCount = courseContainer.getChildCount();
            if (rowCount == 0) {
                runOnUiThread(() -> statusLabel.setText("No courses added."));
                return;
            }

            for (int i = 0; i < rowCount; i++) {
                View child = courseContainer.getChildAt(i);
                if (child instanceof LinearLayout) {
                    processRow((LinearLayout) child, dept, semester, students);
                }
            }
        }).start();
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

        if (TextUtils.isEmpty(code) || facultyIdx <= 0) {
            logStatus("Skipped incomplete row: " + code);
            return;
        }

        Faculty faculty = (facultyIdx - 1 < allFaculty.size()) ? allFaculty.get(facultyIdx - 1) : null;
        if (faculty == null) return;

        double credit = 0;
        try {
            credit = Double.parseDouble(creditStr);
        } catch (NumberFormatException e) {
            logStatus("Invalid credit for " + code);
            return;
        }

        double finalCredit = credit;
        String finalName = name;
        
        // Check or Create Subject in background
        new Thread(() -> {
            Subject subjectToAssign = subjectDatabase.getSubjectByCode(code);
            
            if (subjectToAssign == null) {
                // Create new Subject
                Subject newSub = new Subject();
                newSub.setCode(code);
                newSub.setName(finalName);
                newSub.setCredits(finalCredit);
                newSub.setDepartment(dept);
                newSub.setSemester(semester);
                
                boolean success = subjectDatabase.addSubject(newSub);
                if (success) {
                    subjectToAssign = subjectDatabase.getSubjectByCode(code);
                } else {
                    logStatus("Failed to create subject " + code);
                    return;
                }
            }
            
            if (subjectToAssign != null) {
                assignToStudents(subjectToAssign, faculty, semester, students);
            }
        }).start();
    }

    private void assignToStudents(Subject subject, Faculty faculty, String semester, List<Student> students) {
        int successCount = 0;
        int alreadyAssignedCount = 0;
        int failedCount = 0;
        
        android.util.Log.d("AdminAssignCourse", "Assigning " + subject.getCode() + " to " + students.size() + " students");
        android.util.Log.d("AdminAssignCourse", "Subject ID: " + subject.getId() + ", Faculty ID: " + faculty.getId() + ", Semester: " + semester);
        
        for (Student s : students) {
            android.util.Log.d("AdminAssignCourse", "Processing student: " + s.getId() + " (" + s.getName() + ")");
            
            // Check if already assigned
            boolean isAssigned = courseAssignmentDatabase.isAssigned(s.getId(), subject.getId(), faculty.getId());
            
            if (!isAssigned) {
                CourseAssignment ca = new CourseAssignment();
                ca.setStudentId(s.getId());
                ca.setSubjectId(subject.getId());
                ca.setFacultyId(faculty.getId());
                ca.setSemester(semester);
                
                if (courseAssignmentDatabase.assignCourse(ca)) {
                    successCount++;
                    android.util.Log.d("AdminAssignCourse", "✓ Successfully assigned to: " + s.getId());
                } else {
                    failedCount++;
                    android.util.Log.e("AdminAssignCourse", "✗ Failed to assign to: " + s.getId());
                }
            } else {
                alreadyAssignedCount++;
                android.util.Log.d("AdminAssignCourse", "○ Already assigned: " + s.getId());
            }
        }
        
        int finalSuccessCount = successCount;
        int finalAlreadyCount = alreadyAssignedCount;
        int finalFailedCount = failedCount;
        
        String statusMsg = "Assigned " + subject.getCode() + " - New: " + finalSuccessCount + 
                          ", Already: " + finalAlreadyCount + ", Failed: " + finalFailedCount + 
                          " (Total students: " + students.size() + ")";
        
        android.util.Log.d("AdminAssignCourse", statusMsg);
        logStatus(statusMsg);
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
