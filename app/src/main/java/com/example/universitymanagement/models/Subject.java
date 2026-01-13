package com.example.universitymanagement.models;

public class Subject {
    private String id;
    private String code;
    private String name;
    private int credit;
    private String department;

    public Subject() {
        // Required for Firestore
    }

    public Subject(String id, String name, String code, int credit, String department) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.credit = credit;
        this.department = department;
    }

    public Subject(String name, String code, int credit, String department) {
        this.name = name;
        this.code = code;
        this.credit = credit;
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

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
