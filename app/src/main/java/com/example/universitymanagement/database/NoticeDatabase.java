package com.example.universitymanagement.database;

import com.example.universitymanagement.models.Notice;
import com.example.universitymanagement.util.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import com.example.universitymanagement.util.DBUtil;

public class NoticeDatabase {
        private final FirebaseFirestore db;

        public NoticeDatabase() {
                db = DBUtil.getInstance().getDb();
        }

        public Task<Void> addNotice(Notice notice) {
                DocumentReference docRef = db.collection(Constants.COLLECTION_NOTICES).document();
                notice.setId(docRef.getId());
                return docRef.set(notice);
        }

        public Task<QuerySnapshot> getAllNotices() {
                return db.collection(Constants.COLLECTION_NOTICES)
                                .orderBy("date", Query.Direction.DESCENDING)
                                .get();
        }

        public Task<Void> deleteNotice(String noticeId) {
                return db.collection(Constants.COLLECTION_NOTICES).document(noticeId).delete();
        }
}
