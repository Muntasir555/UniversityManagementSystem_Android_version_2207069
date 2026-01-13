package com.example.universitymanagement.database;

import com.example.universitymanagement.models.CourseAssignment;
import com.example.universitymanagement.util.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import com.example.universitymanagement.util.DBUtil;

public class CourseAssignmentDatabase {
    private final FirebaseFirestore db;

    public CourseAssignmentDatabase() {
        db = DBUtil.getInstance().getDb();
    }

    public Task<Void> assignCourse(CourseAssignment assignment) {
        DocumentReference docRef = db.collection(Constants.COLLECTION_COURSE_ASSIGNMENTS).document();
        assignment.setId(docRef.getId());
        return docRef.set(assignment);
    }

    public Task<QuerySnapshot> getAssignmentsByFaculty(String facultyId) {
        return db.collection(Constants.COLLECTION_COURSE_ASSIGNMENTS)
                .whereEqualTo("facultyId", facultyId)
                .get();
    }

    public Task<QuerySnapshot> isAssigned(String studentId, String subjectId, String facultyId) {
        return db.collection(Constants.COLLECTION_COURSE_ASSIGNMENTS)
                .whereEqualTo("studentId", studentId)
                .whereEqualTo("subjectId", subjectId)
                .whereEqualTo("facultyId", facultyId)
                .get();
    }

    public Task<QuerySnapshot> getAssignmentsByFacultyAndSubject(String facultyId, String subjectId) {
        return db.collection(Constants.COLLECTION_COURSE_ASSIGNMENTS)
                .whereEqualTo("facultyId", facultyId)
                .whereEqualTo("subjectId", subjectId)
                .get();
    }
}
