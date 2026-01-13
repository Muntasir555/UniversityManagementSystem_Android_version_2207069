package com.example.universitymanagement.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.universitymanagement.models.Notice;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NoticeDatabase {
    private static final String TAG = "NoticeDatabase";
    private final DatabaseHelper dbHelper;

    public NoticeDatabase(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public boolean addNotice(Notice notice) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        if (notice.getId() == null || notice.getId().isEmpty()) {
            notice.setId(UUID.randomUUID().toString());
        }
        
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID, notice.getId());
        values.put(DatabaseHelper.COLUMN_NOTICE_TITLE, notice.getTitle());
        values.put(DatabaseHelper.COLUMN_NOTICE_CONTENT, notice.getContent());
        values.put(DatabaseHelper.COLUMN_NOTICE_DATE, notice.getDate());
        values.put(DatabaseHelper.COLUMN_NOTICE_POSTED_BY, notice.getPostedBy());

        try {
            long result = db.insertOrThrow(DatabaseHelper.TABLE_NOTICES, null, values);
            if (result != -1) {
                Log.d(TAG, "Notice added successfully: " + notice.getTitle());
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding notice", e);
        }
        return false;
    }

    public List<Notice> getAllNotices() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Notice> notices = new ArrayList<>();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NOTICES,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_NOTICE_DATE + " DESC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                notices.add(cursorToNotice(cursor));
            }
            cursor.close();
        }

        Log.d(TAG, "Retrieved " + notices.size() + " notices");
        return notices;
    }

    public boolean deleteNotice(String noticeId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        String whereClause = DatabaseHelper.COLUMN_ID + " = ?";
        String[] whereArgs = {noticeId};

        int rowsDeleted = db.delete(DatabaseHelper.TABLE_NOTICES, whereClause, whereArgs);
        
        if (rowsDeleted > 0) {
            Log.d(TAG, "Notice deleted: " + noticeId);
            return true;
        }
        return false;
    }

    private Notice cursorToNotice(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTICE_TITLE));
        String content = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTICE_CONTENT));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTICE_DATE));
        String postedBy = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTICE_POSTED_BY));

        Notice notice = new Notice();
        notice.setId(id);
        notice.setTitle(title);
        notice.setContent(content);
        notice.setDate(date);
        notice.setPostedBy(postedBy);
        return notice;
    }
}
