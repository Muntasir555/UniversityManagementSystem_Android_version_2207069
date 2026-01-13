package com.example.universitymanagement.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.universitymanagement.models.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CourseDatabase {
    private static final String TAG = "CourseDatabase";
    private final DatabaseHelper dbHelper;

    public CourseDatabase(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public boolean addCourse(Course course) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        if (course.getId() == null || course.getId().isEmpty()) {
            course.setId(UUID.randomUUID().toString());
        }
        
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID, course.getId());
        values.put(DatabaseHelper.COLUMN_COURSE_CODE, course.getCourseCode());
        values.put(DatabaseHelper.COLUMN_COURSE_NAME, course.getCourseName());
        values.put(DatabaseHelper.COLUMN_COURSE_DEPARTMENT, course.getDepartment());
        values.put(DatabaseHelper.COLUMN_COURSE_BATCH, course.getBatch());
        values.put(DatabaseHelper.COLUMN_COURSE_SEMESTER, course.getSemester());
        values.put(DatabaseHelper.COLUMN_COURSE_FACULTY_ID, course.getAssignedToFacultyId());

        try {
            long result = db.insertOrThrow(DatabaseHelper.TABLE_COURSES, null, values);
            if (result != -1) {
                Log.d(TAG, "Course added successfully: " + course.getCourseCode());
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding course: " + course.getCourseCode(), e);
        }
        return false;
    }

    public List<Course> getCoursesByDepartment(String department) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Course> courses = new ArrayList<>();

        String selection = DatabaseHelper.COLUMN_COURSE_DEPARTMENT + " = ?";
        String[] selectionArgs = {department};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_COURSES,
                null,
                selection,
                selectionArgs,
                null, null,
                DatabaseHelper.COLUMN_COURSE_NAME
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                courses.add(cursorToCourse(cursor));
            }
            cursor.close();
        }

        return courses;
    }

    public List<Course> getAllCourses() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Course> courses = new ArrayList<>();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_COURSES,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_COURSE_NAME
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                courses.add(cursorToCourse(cursor));
            }
            cursor.close();
        }

        Log.d(TAG, "Retrieved " + courses.size() + " courses");
        return courses;
    }

    private Course cursorToCourse(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
        String courseCode = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COURSE_CODE));
        String courseName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COURSE_NAME));
        String department = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COURSE_DEPARTMENT));
        String batch = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COURSE_BATCH));
        String semester = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COURSE_SEMESTER));
        
        Course course = new Course(id, courseCode, courseName, department, batch, semester);
        
        int facultyIdIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_COURSE_FACULTY_ID);
        if (facultyIdIndex != -1 && !cursor.isNull(facultyIdIndex)) {
            course.setAssignedToFacultyId(cursor.getString(facultyIdIndex));
        }
        
        return course;
    }
}
