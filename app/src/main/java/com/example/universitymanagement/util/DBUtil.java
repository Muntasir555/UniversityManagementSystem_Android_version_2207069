package com.example.universitymanagement.util;

import com.google.firebase.firestore.FirebaseFirestore;

public class DBUtil {
    private static DBUtil instance;
    private final FirebaseFirestore db;

    private DBUtil() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized DBUtil getInstance() {
        if (instance == null) {
            instance = new DBUtil();
        }
        return instance;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public void resetDatabase(Runnable onSuccess, Runnable onFailure) {
        // List of all collections to clear
        String[] collections = {
                Constants.COLLECTION_STUDENTS,
                Constants.COLLECTION_FACULTY,
                Constants.COLLECTION_COURSES,
                Constants.COLLECTION_NOTICES,
                Constants.COLLECTION_ATTENDANCE,
                Constants.COLLECTION_SUBJECTS,
                Constants.COLLECTION_RESULTS,
                Constants.COLLECTION_COURSE_ASSIGNMENTS
                // Add other collections here if any
        };

        deleteCollectionsRecursively(collections, 0, onSuccess, onFailure);
    }

    private void deleteCollectionsRecursively(String[] collections, int index, Runnable onSuccess, Runnable onFailure) {
        if (index >= collections.length) {
            onSuccess.run();
            return;
        }

        String collectionName = collections[index];
        deleteCollection(collectionName, () -> {
            deleteCollectionsRecursively(collections, index + 1, onSuccess, onFailure);
        }, onFailure);
    }

    private void deleteCollection(String collectionName, Runnable onSuccess, Runnable onFailure) {
        db.collection(collectionName).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        onSuccess.run();
                        return;
                    }

                    com.google.firebase.firestore.WriteBatch batch = db.batch();
                    for (com.google.firebase.firestore.DocumentSnapshot document : queryDocumentSnapshots) {
                        batch.delete(document.getReference());
                    }

                    batch.commit()
                            .addOnSuccessListener(aVoid -> onSuccess.run())
                            .addOnFailureListener(e -> onFailure.run());
                })
                .addOnFailureListener(e -> onFailure.run());
    }
}
