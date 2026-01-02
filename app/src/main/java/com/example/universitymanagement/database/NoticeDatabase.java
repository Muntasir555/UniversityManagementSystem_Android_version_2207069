package com.example.universitymanagement.database;

import com.example.universitymanagement.models.Notice;
import java.util.ArrayList;
import java.util.List;

public class NoticeDatabase {

    public static List<Notice> getAllNotices() {
        List<Notice> notices = new ArrayList<>();
        notices.add(new Notice("Midterm Exam Schedule",
                "The midterm exams will start from March 10th. Please check the portal for the detailed schedule.",
                "2025-02-28"));
        notices.add(new Notice("Library Maintenance",
                "The central library will be closed for maintenance on March 5th from 9 AM to 2 PM.", "2025-03-01"));
        notices.add(new Notice("Sports Week Registration",
                "Registration for the annual sports week is now open. Interested students can register at the gym.",
                "2025-03-02"));
        notices.add(new Notice("Guest Lecture on AI",
                "Dr. Smith will be delivering a guest lecture on Artificial Intelligence on March 12th in the Auditorium.",
                "2025-03-03"));
        notices.add(new Notice("Campus Cleanup Drive",
                "Join us for the campus cleanup drive this Sunday at 8 AM. Refreshments will be provided.",
                "2025-03-04"));
        return notices;
    }
}
