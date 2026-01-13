package com.example.universitymanagement.database;

import com.example.universitymanagement.models.Faculty;
import com.example.universitymanagement.util.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import com.example.universitymanagement.util.DBUtil;

public class FacultyDatabase {
    private final FirebaseFirestore db;

    public FacultyDatabase() {
        db = DBUtil.getInstance().getDb();
    }

    public Task<Void> addFaculty(Faculty faculty) {
        return db.collection(Constants.COLLECTION_FACULTY)
                .document(faculty.getId())
                .set(faculty);
    }

    public Task<DocumentSnapshot> getFaculty(String facultyId) {
        return db.collection(Constants.COLLECTION_FACULTY)
                .document(facultyId)
                .get();
    }

    public Task<QuerySnapshot> getAllFaculty() {
        return db.collection(Constants.COLLECTION_FACULTY).get();
    }

    public Task<QuerySnapshot> validateLogin(String email, String password, String department) {
        return db.collection(Constants.COLLECTION_FACULTY)
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .whereEqualTo("department", department)
                .get();
    }

    public Task<QuerySnapshot> getFacultyByEmail(String email) {
        return db.collection(Constants.COLLECTION_FACULTY)
                .whereEqualTo("email", email)
                .get();
    }

    public Task<QuerySnapshot> getFacultyByDepartment(String department) {
        return db.collection(Constants.COLLECTION_FACULTY)
                .whereEqualTo("department", department)
                .get();
    }

    public Task<Void> updatePassword(String facultyId, String newPassword) {
        return db.collection(Constants.COLLECTION_FACULTY).document(facultyId)
                .update("password", newPassword);
    }

    public Task<Void> deleteFaculty(String facultyId) {
        return db.collection(Constants.COLLECTION_FACULTY)
                .document(facultyId)
                .delete();
    }

    public Task<QuerySnapshot> getFacultyByEmailAndDepartment(String email, String department) {
        return db.collection(Constants.COLLECTION_FACULTY)
                .whereEqualTo("email", email)
                .whereEqualTo("department", department)
                .get();
    }
}
