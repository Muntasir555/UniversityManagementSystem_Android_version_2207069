package com.example.universitymanagement.util;

import com.example.universitymanagement.models.Admin;

public class Session {
    public static Admin currentAdmin;

    public static void clear() {
        currentAdmin = null;
        // Clear other users when implemented
    }
}
