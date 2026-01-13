package com.example.universitymanagement.database;

import com.example.universitymanagement.models.Subject;
import com.example.universitymanagement.util.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import com.example.universitymanagement.util.DBUtil;

public class SubjectDatabase {
    private final FirebaseFirestore db;

    public SubjectDatabase() {
        db = DBUtil.getInstance().getDb();
    }

    public Task<Void> addSubject(Subject subject) {
        DocumentReference docRef = db.collection(Constants.COLLECTION_SUBJECTS).document(); // Auto-gen ID first
        subject.setId(docRef.getId()); // Set ID in object
        return docRef.set(subject);
    }

    public Task<QuerySnapshot> getSubjectByCode(String code) {
        return db.collection(Constants.COLLECTION_SUBJECTS)
                .whereEqualTo("code", code)
                .get();
    }

    public Task<DocumentSnapshot> getSubjectById(String id) {
        return db.collection(Constants.COLLECTION_SUBJECTS).document(id).get();
    }

    public Task<QuerySnapshot> getAllSubjects() {
        return db.collection(Constants.COLLECTION_SUBJECTS).get();
    }
}
