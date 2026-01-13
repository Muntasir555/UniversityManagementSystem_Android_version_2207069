package com.example.universitymanagement.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.universitymanagement.models.Admin;

public class AdminDatabase {
    private static final String TAG = "AdminDatabase";
    private final DatabaseHelper dbHelper;

    public AdminDatabase(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    // Login validation - returns Admin if credentials match, null otherwise
    public Admin login(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Admin admin = null;

        String selection = DatabaseHelper.COLUMN_ADMIN_USERNAME + " = ? AND " +
                DatabaseHelper.COLUMN_ADMIN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_ADMINS,
                null,
                selection,
                selectionArgs,
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            admin = cursorToAdmin(cursor);
            Log.d(TAG, "Login successful for username: " + username);
            cursor.close();
        } else {
            Log.d(TAG, "Login failed for username: " + username);
            if (cursor != null) cursor.close();
        }

        return admin;
    }

    public Admin getAdminByUsername(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Admin admin = null;

        String selection = DatabaseHelper.COLUMN_ADMIN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_ADMINS,
                null,
                selection,
                selectionArgs,
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            admin = cursorToAdmin(cursor);
            cursor.close();
        } else if (cursor != null) {
            cursor.close();
        }

        return admin;
    }

    public boolean addAdmin(Admin admin) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID, admin.getUsername()); // Using username as ID
        values.put(DatabaseHelper.COLUMN_ADMIN_USERNAME, admin.getUsername());
        values.put(DatabaseHelper.COLUMN_ADMIN_PASSWORD, admin.getPassword());

        try {
            long result = db.insertOrThrow(DatabaseHelper.TABLE_ADMINS, null, values);
            if (result != -1) {
                Log.d(TAG, "Admin added successfully: " + admin.getUsername());
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding admin: " + admin.getUsername(), e);
        }
        return false;
    }

    public boolean updatePassword(String username, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ADMIN_PASSWORD, newPassword);

        String whereClause = DatabaseHelper.COLUMN_ADMIN_USERNAME + " = ?";
        String[] whereArgs = {username};

        int rowsAffected = db.update(DatabaseHelper.TABLE_ADMINS, values, whereClause, whereArgs);
        
        if (rowsAffected > 0) {
            Log.d(TAG, "Password updated for username: " + username);
            return true;
        } else {
            Log.e(TAG, "Failed to update password for username: " + username);
            return false;
        }
    }

    private Admin cursorToAdmin(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
        String username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ADMIN_USERNAME));
        String password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ADMIN_PASSWORD));
        
        return new Admin(id, username, password);
    }
}
