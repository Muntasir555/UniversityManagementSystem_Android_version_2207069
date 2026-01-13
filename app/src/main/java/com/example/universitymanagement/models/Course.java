package com.example.universitymanagement.models;

public class Course {
    private String id;
    private String courseCode;
    private String courseName;
    private String department;
    private String batch;
    private String semester;
    private String assignedToFacultyId; // Optional: ID of faculty assigned

    public Course() {
        // Required for Firestore
    }

    public Course(String id, String courseCode, String courseName, String department, String batch, String semester) {
        this.id = id;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.department = department;
        this.batch = batch;
        this.semester = semester;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
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

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getAssignedToFacultyId() {
        return assignedToFacultyId;
    }

    public void setAssignedToFacultyId(String assignedToFacultyId) {
        this.assignedToFacultyId = assignedToFacultyId;
    }
}
