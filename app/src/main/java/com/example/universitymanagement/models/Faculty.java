package com.example.universitymanagement.models;

public class Faculty {
    private String id;
    private String name;
    private String email;
    private String department;
    private String designation;
    private String password;

    public Faculty() {
        // Required for Firestore
    }

    public Faculty(String name, String email, String department, String password) {
        this.name = name;
        this.email = email;
        this.department = department;
        this.password = password;
        this.designation = "Lecturer"; // Default designation
    }

    public Faculty(String id, String name, String email, String department, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
        this.password = password;
        this.designation = "Lecturer"; // Default designation
    }

    public Faculty(String id, String name, String email, String department, String designation, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
        this.designation = designation;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
