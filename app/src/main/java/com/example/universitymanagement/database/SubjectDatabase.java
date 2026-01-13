package com.example.universitymanagement.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.universitymanagement.models.Subject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SubjectDatabase {
    private static final String TAG = "SubjectDatabase";
    private final DatabaseHelper dbHelper;

    public SubjectDatabase(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public boolean addSubject(Subject subject) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        if (subject.getId() == null || subject.getId().isEmpty()) {
            subject.setId(UUID.randomUUID().toString());
        }
        
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID, subject.getId());
        values.put(DatabaseHelper.COLUMN_SUBJECT_CODE, subject.getCode());
        values.put(DatabaseHelper.COLUMN_SUBJECT_NAME, subject.getName());
        values.put(DatabaseHelper.COLUMN_SUBJECT_CREDITS, subject.getCredits());
        values.put(DatabaseHelper.COLUMN_SUBJECT_DEPARTMENT, subject.getDepartment());
        values.put(DatabaseHelper.COLUMN_SUBJECT_SEMESTER, subject.getSemester());

        try {
            long result = db.insertOrThrow(DatabaseHelper.TABLE_SUBJECTS, null, values);
            if (result != -1) {
                Log.d(TAG, "Subject added successfully: " + subject.getCode());
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding subject: " + subject.getCode(), e);
        }
        return false;
    }

    public Subject getSubjectByCode(String code) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Subject subject = null;

        String selection = DatabaseHelper.COLUMN_SUBJECT_CODE + " = ?";
        String[] selectionArgs = {code};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_SUBJECTS,
                null,
                selection,
                selectionArgs,
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            subject = cursorToSubject(cursor);
            cursor.close();
        } else if (cursor != null) {
            cursor.close();
        }

        return subject;
    }

    public Subject getSubjectById(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Subject subject = null;

        String selection = DatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {id};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_SUBJECTS,
                null,
                selection,
                selectionArgs,
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            subject = cursorToSubject(cursor);
            cursor.close();
        } else if (cursor != null) {
            cursor.close();
        }

        return subject;
    }

    public List<Subject> getAllSubjects() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Subject> subjects = new ArrayList<>();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_SUBJECTS,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_SUBJECT_CODE
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                subjects.add(cursorToSubject(cursor));
            }
            cursor.close();
        }

        Log.d(TAG, "Retrieved " + subjects.size() + " subjects");
        return subjects;
    }

    private Subject cursorToSubject(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
        String code = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBJECT_CODE));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBJECT_NAME));
        double credits = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBJECT_CREDITS));
        String department = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBJECT_DEPARTMENT));
        String semester = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBJECT_SEMESTER));

        Subject subject = new Subject();
        subject.setId(id);
        subject.setCode(code);
        subject.setName(name);
        subject.setCredits(credits);
        subject.setDepartment(department);
        subject.setSemester(semester);
        return subject;
    }
}
