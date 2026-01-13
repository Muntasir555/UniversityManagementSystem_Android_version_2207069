package com.example.universitymanagement.database;

import com.example.universitymanagement.models.Student;
import com.example.universitymanagement.util.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import com.example.universitymanagement.util.DBUtil;

public class StudentDatabase {
    private final FirebaseFirestore db;

    public StudentDatabase() {
        db = DBUtil.getInstance().getDb();
    }

    public Task<Void> addStudent(Student student) {
        return db.collection(Constants.COLLECTION_STUDENTS)
                .document(student.getId())
                .set(student);
    }

    public Task<DocumentSnapshot> getStudent(String studentId) {
        return db.collection(Constants.COLLECTION_STUDENTS)
                .document(studentId)
                .get();
    }

    public Task<QuerySnapshot> getAllStudents() {
        return db.collection(Constants.COLLECTION_STUDENTS).get();
    }

    // Note: Firestore doesn't support distinct queries natively.
    // We fetch all students and filter locally, or maintain a separate batches
    // collection.
    // For now, fetching all students to extract batches.
    public Task<QuerySnapshot> getAllStudentsForBatches() {
        return db.collection(Constants.COLLECTION_STUDENTS).get();
    }

    public Task<QuerySnapshot> getStudentsByDepartmentAndBatch(String dept, String batch) {
        return db.collection(Constants.COLLECTION_STUDENTS)
                .whereEqualTo("department", dept)
                .whereEqualTo("batch", batch)
                .get();
    }

    public Task<QuerySnapshot> getStudentsByDepartment(String dept) {
        return db.collection(Constants.COLLECTION_STUDENTS)
                .whereEqualTo("department", dept)
                .get();
    }

    public Task<QuerySnapshot> getStudentsByBatch(String batch) {
        return db.collection(Constants.COLLECTION_STUDENTS)
                .whereEqualTo("batch", batch)
                .get();
    }

    public Task<String> getNextStudentId(String batch, String department) {
        // Use getStudentsByDepartment instead of composite query to avoid missing index
        // errors
        // This prevents the "infinite loading" or "rounding" issue
        return getStudentsByDepartment(department)
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    int maxId = 0;
                    for (DocumentSnapshot document : task.getResult()) {
                        // Filter by batch manually since we are only querying by department
                        String docBatch = document.getString("batch");
                        if (docBatch == null || !docBatch.equals(batch)) {
                            continue;
                        }

                        String id = document.getId();
                        // Expected format: 22CSE001 (Batch + Dept + Sequence)
                        // We need to extract the last 3 digits
                        try {
                            // Only consider IDs that start with the correct prefix "BatchDept"
                            String prefix = batch + department;
                            if (id.startsWith(prefix)) {
                                String sequenceStr = id.substring(prefix.length());
                                int sequence = Integer.parseInt(sequenceStr);
                                if (sequence > maxId) {
                                    maxId = sequence;
                                }
                            }
                        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                            // Ignore invalid IDs
                        }
                    }

                    // Generate next ID padded with zeros (e.g., 001, 002)
                    return String.format("%s%s%03d", batch, department, maxId + 1);
                });
    }

    public Task<Void> updateCGPA(String studentId, double cgpa) {
        return db.collection(Constants.COLLECTION_STUDENTS).document(studentId)
                .update("cgpa", cgpa);
    }

    public Task<Void> updatePassword(String studentId, String newPassword) {
        return db.collection(Constants.COLLECTION_STUDENTS).document(studentId)
                .update("password", newPassword);
    }

    public Task<QuerySnapshot> login(String email, String password, String department) {
        return db.collection(Constants.COLLECTION_STUDENTS)
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .whereEqualTo("department", department)
                .get();
    }

    public Task<QuerySnapshot> getStudentByEmailAndDepartment(String email, String department) {
        return db.collection(Constants.COLLECTION_STUDENTS)
                .whereEqualTo("email", email)
                .whereEqualTo("department", department)
                .get();
    }
}
