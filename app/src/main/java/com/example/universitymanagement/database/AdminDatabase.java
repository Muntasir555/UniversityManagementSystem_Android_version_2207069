package com.example.universitymanagement.database;

import com.example.universitymanagement.models.Admin;

public class AdminDatabase {

    public static Admin validateLogin(String username, String password) {
        // Hardcoded admin for demonstration
        if ("admin".equals(username) && "admin123".equals(password)) {
            return new Admin(username, password);
        }
        return null;
    }
}
