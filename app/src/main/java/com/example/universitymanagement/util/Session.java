package com.example.universitymanagement.util;

import com.example.universitymanagement.models.Admin;
import com.example.universitymanagement.models.Faculty;
import com.example.universitymanagement.models.Student;

public class Session {

    public static Student currentStudent;
    public static Faculty currentFaculty;
    public static Admin currentAdmin;

    public static void clear() {
        currentStudent = null;
        currentFaculty = null;
        currentAdmin = null;
    }
}
