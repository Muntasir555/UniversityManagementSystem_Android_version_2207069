package com.example.universitymanagement.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "university_management.db";
    private static final int DATABASE_VERSION = 2;

    private static DatabaseHelper instance;

    // Table Names
    public static final String TABLE_ADMINS = "admins";
    public static final String TABLE_STUDENTS = "students";
    public static final String TABLE_FACULTY = "faculty";
    public static final String TABLE_COURSES = "courses";
    public static final String TABLE_SUBJECTS = "subjects";
    public static final String TABLE_COURSE_ASSIGNMENTS = "course_assignments";
    public static final String TABLE_ATTENDANCE = "attendance";
    public static final String TABLE_RESULTS = "results";
    public static final String TABLE_NOTICES = "notices";

    // Common Columns
    public static final String COLUMN_ID = "id";

    // Admin Table Columns
    public static final String COLUMN_ADMIN_USERNAME = "username";
    public static final String COLUMN_ADMIN_PASSWORD = "password";

    // Student Table Columns
    public static final String COLUMN_STUDENT_NAME = "name";
    public static final String COLUMN_STUDENT_EMAIL = "email";
    public static final String COLUMN_STUDENT_DEPARTMENT = "department";
    public static final String COLUMN_STUDENT_BATCH = "batch";
    public static final String COLUMN_STUDENT_CGPA = "cgpa";
    public static final String COLUMN_STUDENT_PASSWORD = "password";

    // Faculty Table Columns
    public static final String COLUMN_FACULTY_NAME = "name";
    public static final String COLUMN_FACULTY_EMAIL = "email";
    public static final String COLUMN_FACULTY_DEPARTMENT = "department";
    public static final String COLUMN_FACULTY_DESIGNATION = "designation";
    public static final String COLUMN_FACULTY_PASSWORD = "password";

    // Course Table Columns
    public static final String COLUMN_COURSE_CODE = "course_code";
    public static final String COLUMN_COURSE_NAME = "course_name";
    public static final String COLUMN_COURSE_DEPARTMENT = "department";
    public static final String COLUMN_COURSE_BATCH = "batch";
    public static final String COLUMN_COURSE_SEMESTER = "semester";
    public static final String COLUMN_COURSE_FACULTY_ID = "assigned_to_faculty_id";

    // Subject Table Columns
    public static final String COLUMN_SUBJECT_CODE = "code";
    public static final String COLUMN_SUBJECT_NAME = "name";
    public static final String COLUMN_SUBJECT_CREDITS = "credits";
    public static final String COLUMN_SUBJECT_DEPARTMENT = "department";
    public static final String COLUMN_SUBJECT_SEMESTER = "semester";

    // Course Assignment Table Columns
    public static final String COLUMN_CA_FACULTY_ID = "faculty_id";
    public static final String COLUMN_CA_STUDENT_ID = "student_id";
    public static final String COLUMN_CA_SUBJECT_ID = "subject_id";
    public static final String COLUMN_CA_SEMESTER = "semester";

    // Attendance Table Columns
    public static final String COLUMN_ATT_STUDENT_ID = "student_id";
    public static final String COLUMN_ATT_SUBJECT_ID = "subject_id";
    public static final String COLUMN_ATT_DATE = "date";
    public static final String COLUMN_ATT_STATUS = "status";

    // Results Table Columns
    public static final String COLUMN_RES_STUDENT_ID = "student_id";
    public static final String COLUMN_RES_SUBJECT_ID = "subject_id";
    public static final String COLUMN_RES_MARKS = "marks";
    public static final String COLUMN_RES_GRADE = "grade";
    public static final String COLUMN_RES_SEMESTER = "semester";

    // Notice Table Columns
    public static final String COLUMN_NOTICE_TITLE = "title";
    public static final String COLUMN_NOTICE_CONTENT = "content";
    public static final String COLUMN_NOTICE_DATE = "date";
    public static final String COLUMN_NOTICE_POSTED_BY = "posted_by";

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating database tables...");

        // Create Admins Table
        String CREATE_ADMINS_TABLE = "CREATE TABLE " + TABLE_ADMINS + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY, " +
                COLUMN_ADMIN_USERNAME + " TEXT UNIQUE NOT NULL, " +
                COLUMN_ADMIN_PASSWORD + " TEXT NOT NULL)";
        db.execSQL(CREATE_ADMINS_TABLE);

        // Create Students Table
        String CREATE_STUDENTS_TABLE = "CREATE TABLE " + TABLE_STUDENTS + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY, " +
                COLUMN_STUDENT_NAME + " TEXT NOT NULL, " +
                COLUMN_STUDENT_EMAIL + " TEXT NOT NULL, " +
                COLUMN_STUDENT_DEPARTMENT + " TEXT NOT NULL, " +
                COLUMN_STUDENT_BATCH + " TEXT NOT NULL, " +
                COLUMN_STUDENT_CGPA + " REAL DEFAULT 0.0, " +
                COLUMN_STUDENT_PASSWORD + " TEXT NOT NULL, " +
                "UNIQUE(" + COLUMN_STUDENT_EMAIL + ", " + COLUMN_STUDENT_DEPARTMENT + "))";
        db.execSQL(CREATE_STUDENTS_TABLE);

        // Create Faculty Table
        String CREATE_FACULTY_TABLE = "CREATE TABLE " + TABLE_FACULTY + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY, " +
                COLUMN_FACULTY_NAME + " TEXT NOT NULL, " +
                COLUMN_FACULTY_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COLUMN_FACULTY_DEPARTMENT + " TEXT NOT NULL, " +
                COLUMN_FACULTY_DESIGNATION + " TEXT NOT NULL, " +
                COLUMN_FACULTY_PASSWORD + " TEXT NOT NULL)";
        db.execSQL(CREATE_FACULTY_TABLE);

        // Create Courses Table
        String CREATE_COURSES_TABLE = "CREATE TABLE " + TABLE_COURSES + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY, " +
                COLUMN_COURSE_CODE + " TEXT NOT NULL, " +
                COLUMN_COURSE_NAME + " TEXT NOT NULL, " +
                COLUMN_COURSE_DEPARTMENT + " TEXT NOT NULL, " +
                COLUMN_COURSE_BATCH + " TEXT NOT NULL, " +
                COLUMN_COURSE_SEMESTER + " TEXT NOT NULL, " +
                COLUMN_COURSE_FACULTY_ID + " TEXT)";
        db.execSQL(CREATE_COURSES_TABLE);

        // Create Subjects Table
        String CREATE_SUBJECTS_TABLE = "CREATE TABLE " + TABLE_SUBJECTS + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY, " +
                COLUMN_SUBJECT_CODE + " TEXT UNIQUE NOT NULL, " +
                COLUMN_SUBJECT_NAME + " TEXT NOT NULL, " +
                COLUMN_SUBJECT_CREDITS + " REAL NOT NULL, " +
                COLUMN_SUBJECT_DEPARTMENT + " TEXT NOT NULL, " +
                COLUMN_SUBJECT_SEMESTER + " TEXT NOT NULL)";
        db.execSQL(CREATE_SUBJECTS_TABLE);

        // Create Course Assignments Table
        String CREATE_COURSE_ASSIGNMENTS_TABLE = "CREATE TABLE " + TABLE_COURSE_ASSIGNMENTS + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY, " +
                COLUMN_CA_FACULTY_ID + " TEXT NOT NULL, " +
                COLUMN_CA_STUDENT_ID + " TEXT NOT NULL, " +
                COLUMN_CA_SUBJECT_ID + " TEXT NOT NULL, " +
                COLUMN_CA_SEMESTER + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_CA_FACULTY_ID + ") REFERENCES " + TABLE_FACULTY + "(" + COLUMN_ID + "), " +
                "FOREIGN KEY(" + COLUMN_CA_STUDENT_ID + ") REFERENCES " + TABLE_STUDENTS + "(" + COLUMN_ID + "), " +
                "FOREIGN KEY(" + COLUMN_CA_SUBJECT_ID + ") REFERENCES " + TABLE_SUBJECTS + "(" + COLUMN_ID + "))";
        db.execSQL(CREATE_COURSE_ASSIGNMENTS_TABLE);

        // Create Attendance Table
        String CREATE_ATTENDANCE_TABLE = "CREATE TABLE " + TABLE_ATTENDANCE + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY, " +
                COLUMN_ATT_STUDENT_ID + " TEXT NOT NULL, " +
                COLUMN_ATT_SUBJECT_ID + " TEXT NOT NULL, " +
                COLUMN_ATT_DATE + " TEXT NOT NULL, " +
                COLUMN_ATT_STATUS + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_ATT_STUDENT_ID + ") REFERENCES " + TABLE_STUDENTS + "(" + COLUMN_ID + "), " +
                "FOREIGN KEY(" + COLUMN_ATT_SUBJECT_ID + ") REFERENCES " + TABLE_SUBJECTS + "(" + COLUMN_ID + "))";
        db.execSQL(CREATE_ATTENDANCE_TABLE);

        // Create Results Table
        String CREATE_RESULTS_TABLE = "CREATE TABLE " + TABLE_RESULTS + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY, " +
                COLUMN_RES_STUDENT_ID + " TEXT NOT NULL, " +
                COLUMN_RES_SUBJECT_ID + " TEXT NOT NULL, " +
                COLUMN_RES_MARKS + " REAL NOT NULL, " +
                COLUMN_RES_GRADE + " TEXT NOT NULL, " +
                COLUMN_RES_SEMESTER + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_RES_STUDENT_ID + ") REFERENCES " + TABLE_STUDENTS + "(" + COLUMN_ID + "), " +
                "FOREIGN KEY(" + COLUMN_RES_SUBJECT_ID + ") REFERENCES " + TABLE_SUBJECTS + "(" + COLUMN_ID + "))";
        db.execSQL(CREATE_RESULTS_TABLE);

        // Create Notices Table
        String CREATE_NOTICES_TABLE = "CREATE TABLE " + TABLE_NOTICES + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY, " +
                COLUMN_NOTICE_TITLE + " TEXT NOT NULL, " +
                COLUMN_NOTICE_CONTENT + " TEXT NOT NULL, " +
                COLUMN_NOTICE_DATE + " TEXT NOT NULL, " +
                COLUMN_NOTICE_POSTED_BY + " TEXT NOT NULL)";
        db.execSQL(CREATE_NOTICES_TABLE);

        // Create indexes for better performance
        db.execSQL("CREATE INDEX idx_students_dept_batch ON " + TABLE_STUDENTS + 
                "(" + COLUMN_STUDENT_DEPARTMENT + ", " + COLUMN_STUDENT_BATCH + ")");
        db.execSQL("CREATE INDEX idx_students_email ON " + TABLE_STUDENTS + "(" + COLUMN_STUDENT_EMAIL + ")");
        db.execSQL("CREATE INDEX idx_faculty_dept ON " + TABLE_FACULTY + "(" + COLUMN_FACULTY_DEPARTMENT + ")");
        db.execSQL("CREATE INDEX idx_attendance_student ON " + TABLE_ATTENDANCE + "(" + COLUMN_ATT_STUDENT_ID + ")");
        db.execSQL("CREATE INDEX idx_results_student ON " + TABLE_RESULTS + "(" + COLUMN_RES_STUDENT_ID + ")");

        Log.d(TAG, "Database tables created successfully");

        // Insert default admin
        insertDefaultAdmin(db);
    }

    private void insertDefaultAdmin(SQLiteDatabase db) {
        try {
            db.execSQL("INSERT INTO " + TABLE_ADMINS + " VALUES ('admin', 'admin', 'admin123')");
            Log.d(TAG, "Default admin created: username=admin, password=admin123");
        } catch (Exception e) {
            Log.e(TAG, "Error creating default admin", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        
        // Drop all tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMINS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FACULTY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE_ASSIGNMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTICES);
        
        // Recreate tables
        onCreate(db);
    }

    public void resetDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db, 0, 1);
        Log.d(TAG, "Database reset completed");
    }
}

