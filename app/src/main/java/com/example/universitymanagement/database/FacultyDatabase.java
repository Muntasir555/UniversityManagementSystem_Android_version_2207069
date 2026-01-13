package com.example.universitymanagement.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.universitymanagement.models.Faculty;

import java.util.ArrayList;
import java.util.List;

public class FacultyDatabase {
    private static final String TAG = "FacultyDatabase";
    private final DatabaseHelper dbHelper;

    public FacultyDatabase(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public boolean addFaculty(Faculty faculty) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID, faculty.getId());
        values.put(DatabaseHelper.COLUMN_FACULTY_NAME, faculty.getName());
        values.put(DatabaseHelper.COLUMN_FACULTY_EMAIL, faculty.getEmail());
        values.put(DatabaseHelper.COLUMN_FACULTY_DEPARTMENT, faculty.getDepartment());
        values.put(DatabaseHelper.COLUMN_FACULTY_DESIGNATION, faculty.getDesignation());
        values.put(DatabaseHelper.COLUMN_FACULTY_PASSWORD, faculty.getPassword());

        try {
            long result = db.insertOrThrow(DatabaseHelper.TABLE_FACULTY, null, values);
            if (result != -1) {
                Log.d(TAG, "Faculty added successfully: " + faculty.getId());
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding faculty: " + faculty.getId(), e);
        }
        return false;
    }

    public Faculty getFaculty(String facultyId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Faculty faculty = null;

        String selection = DatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {facultyId};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_FACULTY,
                null,
                selection,
                selectionArgs,
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            faculty = cursorToFaculty(cursor);
            cursor.close();
        } else if (cursor != null) {
            cursor.close();
        }

        return faculty;
    }

    public List<Faculty> getAllFaculty() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Faculty> facultyList = new ArrayList<>();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_FACULTY,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_FACULTY_NAME
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                facultyList.add(cursorToFaculty(cursor));
            }
            cursor.close();
        }

        Log.d(TAG, "Retrieved " + facultyList.size() + " faculty members");
        return facultyList;
    }

    public Faculty validateLogin(String email, String password, String department) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Faculty faculty = null;

        String selection = DatabaseHelper.COLUMN_FACULTY_EMAIL + " = ? AND " +
                DatabaseHelper.COLUMN_FACULTY_PASSWORD + " = ? AND " +
                DatabaseHelper.COLUMN_FACULTY_DEPARTMENT + " = ?";
        String[] selectionArgs = {email, password, department};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_FACULTY,
                null,
                selection,
                selectionArgs,
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            faculty = cursorToFaculty(cursor);
            Log.d(TAG, "Faculty login successful: " + email);
            cursor.close();
        } else {
            Log.d(TAG, "Faculty login failed: " + email);
            if (cursor != null) cursor.close();
        }

        return faculty;
    }

    public Faculty getFacultyByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Faculty faculty = null;

        String selection = DatabaseHelper.COLUMN_FACULTY_EMAIL + " = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_FACULTY,
                null,
                selection,
                selectionArgs,
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            faculty = cursorToFaculty(cursor);
            cursor.close();
        } else if (cursor != null) {
            cursor.close();
        }

        return faculty;
    }

    public List<Faculty> getFacultyByDepartment(String department) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Faculty> facultyList = new ArrayList<>();

        String selection = DatabaseHelper.COLUMN_FACULTY_DEPARTMENT + " = ?";
        String[] selectionArgs = {department};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_FACULTY,
                null,
                selection,
                selectionArgs,
                null, null,
                DatabaseHelper.COLUMN_FACULTY_NAME
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                facultyList.add(cursorToFaculty(cursor));
            }
            cursor.close();
        }

        return facultyList;
    }

    public boolean updatePassword(String facultyId, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_FACULTY_PASSWORD, newPassword);

        String whereClause = DatabaseHelper.COLUMN_ID + " = ?";
        String[] whereArgs = {facultyId};

        int rowsAffected = db.update(DatabaseHelper.TABLE_FACULTY, values, whereClause, whereArgs);
        
        if (rowsAffected > 0) {
            Log.d(TAG, "Password updated for faculty: " + facultyId);
            return true;
        }
        return false;
    }

    public boolean deleteFaculty(String facultyId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        String whereClause = DatabaseHelper.COLUMN_ID + " = ?";
        String[] whereArgs = {facultyId};

        int rowsDeleted = db.delete(DatabaseHelper.TABLE_FACULTY, whereClause, whereArgs);
        
        if (rowsDeleted > 0) {
            Log.d(TAG, "Faculty deleted: " + facultyId);
            return true;
        }
        return false;
    }

    public Faculty getFacultyByEmailAndDepartment(String email, String department) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Faculty faculty = null;

        String selection = DatabaseHelper.COLUMN_FACULTY_EMAIL + " = ? AND " +
                DatabaseHelper.COLUMN_FACULTY_DEPARTMENT + " = ?";
        String[] selectionArgs = {email, department};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_FACULTY,
                null,
                selection,
                selectionArgs,
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            faculty = cursorToFaculty(cursor);
            cursor.close();
        } else if (cursor != null) {
            cursor.close();
        }

        return faculty;
    }

    private Faculty cursorToFaculty(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FACULTY_NAME));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FACULTY_EMAIL));
        String department = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FACULTY_DEPARTMENT));
        String designation = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FACULTY_DESIGNATION));
        String password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FACULTY_PASSWORD));
        
        return new Faculty(id, name, email, department, designation, password);
    }
}
