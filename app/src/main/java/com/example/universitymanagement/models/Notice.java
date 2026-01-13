package com.example.universitymanagement.models;

public class Notice {
    private String id;
    private String title;
    private String content;
    private String date;
    private String postedBy;

    public Notice() {
        // Required for database operations
    }

    public Notice(String title, String content, String date) {
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public Notice(String id, String title, String content, String date) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public Notice(String id, String title, String content, String date, String postedBy) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.postedBy = postedBy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }
}
