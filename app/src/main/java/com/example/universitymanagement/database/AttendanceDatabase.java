package com.example.universitymanagement.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.universitymanagement.models.Attendance;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AttendanceDatabase {
    private static final String TAG = "AttendanceDatabase";
    private final DatabaseHelper dbHelper;

    public AttendanceDatabase(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public boolean markAttendance(Attendance attendance) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        if (attendance.getId() == null || attendance.getId().isEmpty()) {
            attendance.setId(UUID.randomUUID().toString());
        }
        
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID, attendance.getId());
        values.put(DatabaseHelper.COLUMN_ATT_STUDENT_ID, attendance.getStudentId());
        values.put(DatabaseHelper.COLUMN_ATT_SUBJECT_ID, attendance.getSubjectId());
        values.put(DatabaseHelper.COLUMN_ATT_DATE, attendance.getDate());
        values.put(DatabaseHelper.COLUMN_ATT_STATUS, attendance.getStatus());

        try {
            long result = db.insertOrThrow(DatabaseHelper.TABLE_ATTENDANCE, null, values);
            if (result != -1) {
                Log.d(TAG, "Attendance marked successfully for student: " + attendance.getStudentId());
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error marking attendance", e);
        }
        return false;
    }

    public List<Attendance> getAttendanceByStudentId(String studentId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Attendance> attendanceList = new ArrayList<>();

        String selection = DatabaseHelper.COLUMN_ATT_STUDENT_ID + " = ?";
        String[] selectionArgs = {studentId};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_ATTENDANCE,
                null,
                selection,
                selectionArgs,
                null, null,
                DatabaseHelper.COLUMN_ATT_DATE + " DESC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                attendanceList.add(cursorToAttendance(cursor));
            }
            cursor.close();
        }

        return attendanceList;
    }

    public List<Attendance> getAttendanceByStudentAndSubject(String studentId, String subjectId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Attendance> attendanceList = new ArrayList<>();

        String selection = DatabaseHelper.COLUMN_ATT_STUDENT_ID + " = ? AND " +
                DatabaseHelper.COLUMN_ATT_SUBJECT_ID + " = ?";
        String[] selectionArgs = {studentId, subjectId};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_ATTENDANCE,
                null,
                selection,
                selectionArgs,
                null, null,
                DatabaseHelper.COLUMN_ATT_DATE + " DESC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                attendanceList.add(cursorToAttendance(cursor));
            }
            cursor.close();
        }

        return attendanceList;
    }

    private Attendance cursorToAttendance(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
        String studentId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATT_STUDENT_ID));
        String subjectId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATT_SUBJECT_ID));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATT_DATE));
        String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ATT_STATUS));

        Attendance attendance = new Attendance(studentId, date, status, subjectId);
        attendance.setId(id);
        return attendance;
    }
}
