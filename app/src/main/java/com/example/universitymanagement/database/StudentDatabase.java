package com.example.universitymanagement.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.universitymanagement.models.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentDatabase {
    private static final String TAG = "StudentDatabase";
    private final DatabaseHelper dbHelper;

    public StudentDatabase(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public boolean addStudent(Student student) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID, student.getId());
        values.put(DatabaseHelper.COLUMN_STUDENT_NAME, student.getName());
        values.put(DatabaseHelper.COLUMN_STUDENT_EMAIL, student.getEmail());
        values.put(DatabaseHelper.COLUMN_STUDENT_DEPARTMENT, student.getDepartment());
        values.put(DatabaseHelper.COLUMN_STUDENT_BATCH, student.getBatch());
        values.put(DatabaseHelper.COLUMN_STUDENT_CGPA, student.getCgpa());
        values.put(DatabaseHelper.COLUMN_STUDENT_PASSWORD, student.getPassword());

        try {
            long result = db.insertOrThrow(DatabaseHelper.TABLE_STUDENTS, null, values);
            if (result != -1) {
                Log.d(TAG, "Student added successfully: " + student.getId());
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding student: " + student.getId(), e);
        }
        return false;
    }

    public Student getStudent(String studentId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Student student = null;

        String selection = DatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {studentId};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_STUDENTS,
                null,
                selection,
                selectionArgs,
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            student = cursorToStudent(cursor);
            cursor.close();
        } else if (cursor != null) {
            cursor.close();
        }

        return student;
    }

    public List<Student> getAllStudents() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Student> students = new ArrayList<>();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_STUDENTS,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_STUDENT_DEPARTMENT + ", " + DatabaseHelper.COLUMN_STUDENT_BATCH
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                students.add(cursorToStudent(cursor));
            }
            cursor.close();
        }

        Log.d(TAG, "Retrieved " + students.size() + " students");
        return students;
    }

    public List<Student> getStudentsByDepartmentAndBatch(String department, String batch) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Student> students = new ArrayList<>();

        String selection = DatabaseHelper.COLUMN_STUDENT_DEPARTMENT + " = ? AND " +
                DatabaseHelper.COLUMN_STUDENT_BATCH + " = ?";
        String[] selectionArgs = {department, batch};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_STUDENTS,
                null,
                selection,
                selectionArgs,
                null, null,
                DatabaseHelper.COLUMN_STUDENT_NAME
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                students.add(cursorToStudent(cursor));
            }
            cursor.close();
        }

        return students;
    }

    public List<Student> getStudentsByDepartment(String department) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Student> students = new ArrayList<>();

        String selection = DatabaseHelper.COLUMN_STUDENT_DEPARTMENT + " = ?";
        String[] selectionArgs = {department};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_STUDENTS,
                null,
                selection,
                selectionArgs,
                null, null,
                DatabaseHelper.COLUMN_STUDENT_NAME
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                students.add(cursorToStudent(cursor));
            }
            cursor.close();
        }

        return students;
    }

    public List<Student> getStudentsByBatch(String batch) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Student> students = new ArrayList<>();

        String selection = DatabaseHelper.COLUMN_STUDENT_BATCH + " = ?";
        String[] selectionArgs = {batch};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_STUDENTS,
                null,
                selection,
                selectionArgs,
                null, null,
                DatabaseHelper.COLUMN_STUDENT_NAME
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                students.add(cursorToStudent(cursor));
            }
            cursor.close();
        }

        return students;
    }

    public String getNextStudentId(String batch, String department) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String prefix = batch + department;
        int maxId = 0;

        // Query students with matching department and batch
        String selection = DatabaseHelper.COLUMN_STUDENT_DEPARTMENT + " = ? AND " +
                DatabaseHelper.COLUMN_STUDENT_BATCH + " = ?";
        String[] selectionArgs = {department, batch};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_STUDENTS,
                new String[]{DatabaseHelper.COLUMN_ID},
                selection,
                selectionArgs,
                null, null, null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                if (id != null && id.startsWith(prefix) && id.length() > prefix.length()) {
                    try {
                        String sequenceStr = id.substring(prefix.length());
                        int sequence = Integer.parseInt(sequenceStr);
                        if (sequence > maxId) {
                            maxId = sequence;
                        }
                    } catch (NumberFormatException e) {
                        Log.w(TAG, "Invalid ID format: " + id);
                    }
                }
            }
            cursor.close();
        }

        String nextId = String.format("%s%s%03d", batch, department, maxId + 1);
        Log.d(TAG, "Generated next ID: " + nextId + " (maxId was: " + maxId + ")");
        return nextId;
    }

    public boolean isStudentIdExists(String studentId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String selection = DatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {studentId};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_STUDENTS,
                new String[]{DatabaseHelper.COLUMN_ID},
                selection,
                selectionArgs,
                null, null, null
        );

        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }

        Log.d(TAG, "Checking ID " + studentId + " exists: " + exists);
        return exists;
    }

    public boolean updateCGPA(String studentId, double cgpa) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_STUDENT_CGPA, cgpa);

        String whereClause = DatabaseHelper.COLUMN_ID + " = ?";
        String[] whereArgs = {studentId};

        int rowsAffected = db.update(DatabaseHelper.TABLE_STUDENTS, values, whereClause, whereArgs);
        
        if (rowsAffected > 0) {
            Log.d(TAG, "CGPA updated for student: " + studentId);
            return true;
        }
        return false;
    }

    public boolean updatePassword(String studentId, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_STUDENT_PASSWORD, newPassword);

        String whereClause = DatabaseHelper.COLUMN_ID + " = ?";
        String[] whereArgs = {studentId};

        int rowsAffected = db.update(DatabaseHelper.TABLE_STUDENTS, values, whereClause, whereArgs);
        
        if (rowsAffected > 0) {
            Log.d(TAG, "Password updated for student: " + studentId);
            return true;
        }
        return false;
    }

    public Student login(String email, String password, String department) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Student student = null;

        String selection = DatabaseHelper.COLUMN_STUDENT_EMAIL + " = ? AND " +
                DatabaseHelper.COLUMN_STUDENT_PASSWORD + " = ? AND " +
                DatabaseHelper.COLUMN_STUDENT_DEPARTMENT + " = ?";
        String[] selectionArgs = {email, password, department};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_STUDENTS,
                null,
                selection,
                selectionArgs,
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            student = cursorToStudent(cursor);
            Log.d(TAG, "Student login successful: " + email);
            cursor.close();
        } else {
            Log.d(TAG, "Student login failed: " + email);
            if (cursor != null) cursor.close();
        }

        return student;
    }

    public Student getStudentByEmailAndDepartment(String email, String department) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Student student = null;

        String selection = DatabaseHelper.COLUMN_STUDENT_EMAIL + " = ? AND " +
                DatabaseHelper.COLUMN_STUDENT_DEPARTMENT + " = ?";
        String[] selectionArgs = {email, department};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_STUDENTS,
                null,
                selection,
                selectionArgs,
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            student = cursorToStudent(cursor);
            cursor.close();
        } else if (cursor != null) {
            cursor.close();
        }

        return student;
    }

    public boolean deleteStudent(String studentId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        String whereClause = DatabaseHelper.COLUMN_ID + " = ?";
        String[] whereArgs = {studentId};

        int rowsDeleted = db.delete(DatabaseHelper.TABLE_STUDENTS, whereClause, whereArgs);
        
        if (rowsDeleted > 0) {
            Log.d(TAG, "Student deleted: " + studentId);
            return true;
        }
        return false;
    }

    private Student cursorToStudent(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_NAME));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_EMAIL));
        String department = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_DEPARTMENT));
        String batch = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_BATCH));
        double cgpa = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_CGPA));
        String password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_PASSWORD));
        
        return new Student(id, name, email, department, batch, cgpa, password);
    }
}
