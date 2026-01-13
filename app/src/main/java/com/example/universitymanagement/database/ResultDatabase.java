package com.example.universitymanagement.database;

import com.example.universitymanagement.models.Result;
import com.example.universitymanagement.models.Subject;
import com.example.universitymanagement.util.CGPAUtility;
import com.example.universitymanagement.util.Constants;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.universitymanagement.util.DBUtil;

public class ResultDatabase {
    private final FirebaseFirestore db;
    private final SubjectDatabase subjectDatabase;
    private final StudentDatabase studentDatabase;

    public ResultDatabase() {
        db = DBUtil.getInstance().getDb();
        subjectDatabase = new SubjectDatabase();
        studentDatabase = new StudentDatabase();
    }

    public Task<Void> addResult(Result result) {
        DocumentReference docRef = db.collection(Constants.COLLECTION_RESULTS).document();
        result.setId(docRef.getId());

        // 1. Add Result
        return docRef.set(result).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            // 2. Recalculate CGPA (Chained Task)
            return recalculateAndSaveCGPA(result.getStudentId());
        });
    }

    private Task<Void> recalculateAndSaveCGPA(String studentId) {
        // Fetch All Results for Student
        Task<QuerySnapshot> resultsTask = getResultsByStudentId(studentId);
        // Fetch All Subjects (to get credits)
        Task<QuerySnapshot> subjectsTask = subjectDatabase.getAllSubjects();

        return Tasks.whenAllSuccess(resultsTask, subjectsTask).continueWithTask(task -> {
            List<Object> results = task.getResult(); // List of results from both tasks
            QuerySnapshot resultSnaps = (QuerySnapshot) results.get(0);
            QuerySnapshot subjectSnaps = (QuerySnapshot) results.get(1);

            List<Result> allResults = resultSnaps.toObjects(Result.class);
            List<Subject> allSubjects = subjectSnaps.toObjects(Subject.class);

            Map<String, Integer> subjectCredits = new HashMap<>();
            for (Subject s : allSubjects) {
                subjectCredits.put(s.getId(), s.getCredit());
            }

            double newCgpa = CGPAUtility.calculateCGPA(allResults, subjectCredits);

            return studentDatabase.updateCGPA(studentId, newCgpa);
        });
    }

    public Task<QuerySnapshot> getResultsByStudentId(String studentId) {
        return db.collection(Constants.COLLECTION_RESULTS)
                .whereEqualTo("studentId", studentId)
                .get();
    }
}
