package com.example.universitymanagement.models;

public class Student {
    private String id;
    private String name;
    private String email;
    private String department;
    private String batch;
    private double cgpa;
    private String password;

    public Student() {
        // Required for Firestore
    }

    public Student(String name, String email, String department, String batch, String password) {
        this.name = name;
        this.email = email;
        this.department = department;
        this.batch = batch;
        this.password = password;
    }

    public Student(String id, String name, String email, String department, String batch, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
        this.batch = batch;
        this.password = password;
        this.cgpa = 0.0;
    }

    public Student(String id, String name, String email, String department, String batch, double cgpa,
            String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
        this.batch = batch;
        this.cgpa = cgpa;
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

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public double getCgpa() {
        return cgpa;
    }

    public void setCgpa(double cgpa) {
        this.cgpa = cgpa;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
