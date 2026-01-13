package com.example.universitymanagement.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.universitymanagement.models.CourseAssignment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CourseAssignmentDatabase {
    private static final String TAG = "CourseAssignmentDatabase";
    private final DatabaseHelper dbHelper;

    public CourseAssignmentDatabase(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public boolean assignCourse(CourseAssignment assignment) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        if (assignment.getId() == null || assignment.getId().isEmpty()) {
            assignment.setId(UUID.randomUUID().toString());
        }
        
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID, assignment.getId());
        values.put(DatabaseHelper.COLUMN_CA_FACULTY_ID, assignment.getFacultyId());
        values.put(DatabaseHelper.COLUMN_CA_STUDENT_ID, assignment.getStudentId());
        values.put(DatabaseHelper.COLUMN_CA_SUBJECT_ID, assignment.getSubjectId());
        values.put(DatabaseHelper.COLUMN_CA_SEMESTER, assignment.getSemester());

        try {
            long result = db.insertOrThrow(DatabaseHelper.TABLE_COURSE_ASSIGNMENTS, null, values);
            if (result != -1) {
                Log.d(TAG, "Course assigned successfully");
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error assigning course", e);
        }
        return false;
    }

    public List<CourseAssignment> getAssignmentsByFaculty(String facultyId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<CourseAssignment> assignments = new ArrayList<>();

        String selection = DatabaseHelper.COLUMN_CA_FACULTY_ID + " = ?";
        String[] selectionArgs = {facultyId};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_COURSE_ASSIGNMENTS,
                null,
                selection,
                selectionArgs,
                null, null, null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                assignments.add(cursorToCourseAssignment(cursor));
            }
            cursor.close();
        }

        return assignments;
    }

    public boolean isAssigned(String studentId, String subjectId, String facultyId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = DatabaseHelper.COLUMN_CA_STUDENT_ID + " = ? AND " +
                DatabaseHelper.COLUMN_CA_SUBJECT_ID + " = ? AND " +
                DatabaseHelper.COLUMN_CA_FACULTY_ID + " = ?";
        String[] selectionArgs = {studentId, subjectId, facultyId};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_COURSE_ASSIGNMENTS,
                new String[]{DatabaseHelper.COLUMN_ID},
                selection,
                selectionArgs,
                null, null, null
        );

        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }

        return exists;
    }

    public List<CourseAssignment> getAssignmentsByFacultyAndSubject(String facultyId, String subjectId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<CourseAssignment> assignments = new ArrayList<>();

        String selection = DatabaseHelper.COLUMN_CA_FACULTY_ID + " = ? AND " +
                DatabaseHelper.COLUMN_CA_SUBJECT_ID + " = ?";
        String[] selectionArgs = {facultyId, subjectId};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_COURSE_ASSIGNMENTS,
                null,
                selection,
                selectionArgs,
                null, null, null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                assignments.add(cursorToCourseAssignment(cursor));
            }
            cursor.close();
        }

        return assignments;
    }

    private CourseAssignment cursorToCourseAssignment(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
        String facultyId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CA_FACULTY_ID));
        String studentId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CA_STUDENT_ID));
        String subjectId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CA_SUBJECT_ID));
        String semester = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CA_SEMESTER));

        CourseAssignment assignment = new CourseAssignment();
        assignment.setId(id);
        assignment.setFacultyId(facultyId);
        assignment.setStudentId(studentId);
        assignment.setSubjectId(subjectId);
        assignment.setSemester(semester);
        return assignment;
    }
}
