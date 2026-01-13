package com.example.universitymanagement.database;

import com.example.universitymanagement.models.Course;
import com.example.universitymanagement.util.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import com.example.universitymanagement.util.DBUtil;

public class CourseDatabase {
    private final FirebaseFirestore db;

    public CourseDatabase() {
        db = DBUtil.getInstance().getDb();
    }

    public Task<Void> addCourse(Course course) {
        String docId = course.getId() != null ? course.getId()
                : db.collection(Constants.COLLECTION_COURSES).document().getId();
        course.setId(docId);
        return db.collection(Constants.COLLECTION_COURSES)
                .document(docId)
                .set(course);
    }

    public Task<QuerySnapshot> getCoursesByDepartment(String department) {
        return db.collection(Constants.COLLECTION_COURSES)
                .whereEqualTo("department", department)
                .get();
    }

    public Task<QuerySnapshot> getAllCourses() {
        return db.collection(Constants.COLLECTION_COURSES).get();
    }
}
