package com.example.universitymanagement.models;

public class Subject {
    private String id;
    private String code;
    private String name;
    private double credits;
    private String department;
    private String semester;

    public Subject() {
    }

    public Subject(String id, String name, String code, double credits, String department) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.credits = credits;
        this.department = department;
    }

    public Subject(String name, String code, double credits, String department) {
        this.name = name;
        this.code = code;
        this.credits = credits;
        this.department = department;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCredits() {
        return credits;
    }

    public void setCredits(double credits) {
        this.credits = credits;
    }

    // Alias method for backwards compatibility
    public double getCredit() {
        return credits;
    }

    public void setCredit(double credit) {
        this.credits = credit;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}
