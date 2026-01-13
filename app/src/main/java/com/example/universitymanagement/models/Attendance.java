package com.example.universitymanagement.models;

public class Attendance {
    private String id;
    private String studentId;
    private String date;
    private String status;
    private String subjectId; // Changed from int to String for consistency

    public Attendance() {
        // Required for Firestore
    }

    public Attendance(String studentId, String date, String status, String subjectId) {
        this.studentId = studentId;
        this.date = date;
        this.status = status;
        this.subjectId = subjectId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }
}
