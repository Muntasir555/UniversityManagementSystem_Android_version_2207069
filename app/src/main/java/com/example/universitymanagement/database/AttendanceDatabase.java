package com.example.universitymanagement.database;

import com.example.universitymanagement.models.Attendance;
import com.example.universitymanagement.util.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import com.example.universitymanagement.util.DBUtil;

public class AttendanceDatabase {
    private final FirebaseFirestore db;

    public AttendanceDatabase() {
        db = DBUtil.getInstance().getDb();
    }

    public Task<Void> markAttendance(Attendance attendance) {
        DocumentReference docRef = db.collection(Constants.COLLECTION_ATTENDANCE).document();
        attendance.setId(docRef.getId());
        return docRef.set(attendance);
    }

    public Task<QuerySnapshot> getAttendanceByStudentId(String studentId) {
        return db.collection(Constants.COLLECTION_ATTENDANCE)
                .whereEqualTo("studentId", studentId)
                .get();
    }
}
