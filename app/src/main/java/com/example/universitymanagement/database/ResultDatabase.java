package com.example.universitymanagement.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.universitymanagement.models.Result;
import com.example.universitymanagement.models.Subject;
import com.example.universitymanagement.util.CGPAUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ResultDatabase {
    private static final String TAG = "ResultDatabase";
    private final DatabaseHelper dbHelper;
    private final SubjectDatabase subjectDatabase;
    private final StudentDatabase studentDatabase;

    public ResultDatabase(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
        subjectDatabase = new SubjectDatabase(context);
        studentDatabase = new StudentDatabase(context);
    }

    public boolean addResult(Result result) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        if (result.getId() == null || result.getId().isEmpty()) {
            result.setId(UUID.randomUUID().toString());
        }
        
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID, result.getId());
        values.put(DatabaseHelper.COLUMN_RES_STUDENT_ID, result.getStudentId());
        values.put(DatabaseHelper.COLUMN_RES_SUBJECT_ID, result.getSubjectId());
        values.put(DatabaseHelper.COLUMN_RES_MARKS, result.getMarks());
        values.put(DatabaseHelper.COLUMN_RES_GRADE, result.getGrade());
        values.put(DatabaseHelper.COLUMN_RES_SEMESTER, result.getSemester());

        try {
            long insertResult = db.insertOrThrow(DatabaseHelper.TABLE_RESULTS, null, values);
            if (insertResult != -1) {
                Log.d(TAG, "Result added successfully for student: " + result.getStudentId());
                
                // Recalculate and update CGPA
                recalculateAndSaveCGPA(result.getStudentId());
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding result", e);
        }
        return false;
    }

    private void recalculateAndSaveCGPA(String studentId) {
        try {
            // Fetch all results for student
            List<Result> results = getResultsByStudentId(studentId);
            
            // Fetch all subjects to get credits
            List<Subject> allSubjects = subjectDatabase.getAllSubjects();
            
            // Build credit map
            Map<String, Double> subjectCredits = new HashMap<>();
            for (Subject subject : allSubjects) {
                subjectCredits.put(subject.getId(), subject.getCredits());
            }
            
            // Calculate CGPA
            double cgpa = CGPAUtility.calculateCGPA(results, subjectCredits);
            
            // Update student CGPA
            studentDatabase.updateCGPA(studentId, cgpa);
            
            Log.d(TAG, "CGPA updated for student " + studentId + ": " + cgpa);
        } catch (Exception e) {
            Log.e(TAG, "Error recalculating CGPA for student: " + studentId, e);
        }
    }

    public List<Result> getResultsByStudentId(String studentId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Result> results = new ArrayList<>();

        String selection = DatabaseHelper.COLUMN_RES_STUDENT_ID + " = ?";
        String[] selectionArgs = {studentId};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_RESULTS,
                null,
                selection,
                selectionArgs,
                null, null,
                DatabaseHelper.COLUMN_RES_SEMESTER
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                results.add(cursorToResult(cursor));
            }
            cursor.close();
        }

        return results;
    }

    public List<Result> getResultsByStudentAndSemester(String studentId, String semester) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Result> results = new ArrayList<>();

        String selection = DatabaseHelper.COLUMN_RES_STUDENT_ID + " = ? AND " +
                DatabaseHelper.COLUMN_RES_SEMESTER + " = ?";
        String[] selectionArgs = {studentId, semester};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_RESULTS,
                null,
                selection,
                selectionArgs,
                null, null, null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                results.add(cursorToResult(cursor));
            }
            cursor.close();
        }

        return results;
    }

    private Result cursorToResult(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
        String studentId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RES_STUDENT_ID));
        String subjectId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RES_SUBJECT_ID));
        double marks = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RES_MARKS));
        String grade = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RES_GRADE));
        String semester = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RES_SEMESTER));

        return new Result(id, studentId, subjectId, marks, grade, semester);
    }
}
