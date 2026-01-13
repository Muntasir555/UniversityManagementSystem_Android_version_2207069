package com.example.universitymanagement.models;

public class CourseAssignment {
    private String id;
    private String studentId;
    private String subjectId;
    private String facultyId;
    private String semester;

    public CourseAssignment() {
    }

    public CourseAssignment(String id, String studentId, String subjectId, String facultyId, String semester) {
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.facultyId = facultyId;
        this.semester = semester;
    }

    public CourseAssignment(String studentId, String subjectId, String facultyId, String semester) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.facultyId = facultyId;
        this.semester = semester;
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

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}
