package com.example.universitymanagement.database;

import com.example.universitymanagement.models.Admin;
import com.example.universitymanagement.util.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import com.example.universitymanagement.util.DBUtil;

public class AdminDatabase {
    private final FirebaseFirestore db;

    public AdminDatabase() {
        db = DBUtil.getInstance().getDb();
    }

    // Login validation via Firestore (Note: FirebaseAuth is recommended for
    // production)
    // Login validation via Firestore
    // query by username only, check password locally to avoid composite index
    // issues
    public Task<QuerySnapshot> getAdminByUsername(String username) {
        return db.collection(Constants.COLLECTION_ADMINS)
                .whereEqualTo("username", username)
                .get();
    }

    public Task<Void> addAdmin(Admin admin) {
        // Assuming username is unique ID for simplicity, or auto-gen
        return db.collection(Constants.COLLECTION_ADMINS).document(admin.getUsername()).set(admin);
    }

    public void checkAndCreateDefaultAdmin() {
        db.collection(Constants.COLLECTION_ADMINS).document("admin").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        Admin admin = new Admin("admin", "admin123");
                        addAdmin(admin);
                    }
                });
    }

    public Task<Void> updatePassword(String username, String newPassword) {
        // Assuming username is the document ID as per addAdmin implementation
        return db.collection(Constants.COLLECTION_ADMINS).document(username)
                .update("password", newPassword);
    }
}
