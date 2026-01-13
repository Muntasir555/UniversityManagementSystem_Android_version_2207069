package com.example.universitymanagement.controllers;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universitymanagement.R;
import com.example.universitymanagement.adapters.NoticeAdapter;
import com.example.universitymanagement.database.NoticeDatabase;
import com.example.universitymanagement.models.Notice;
import com.example.universitymanagement.util.Session;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

public class AdminNoticeBoardActivity extends AppCompatActivity {

    private EditText etTitle, etContent;
    private TextView tvSelectedDate;
    private Button btnPostNotice, btnPickDate;
    private NoticeDatabase noticeDatabase;
    private LocalDate selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_notice_board);

        noticeDatabase = new NoticeDatabase(this);

        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnPostNotice = findViewById(R.id.btnPostNotice);

        // Default to current date
        selectedDate = LocalDate.now();
        tvSelectedDate.setText(selectedDate.toString());

        btnPickDate.setOnClickListener(v -> showDatePicker());
        btnPostNotice.setOnClickListener(v -> postNotice());

        // Initialize RecyclerView for Notices
        androidx.recyclerview.widget.RecyclerView rvNotices = findViewById(R.id.rvNotices);
        rvNotices.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        loadNotices(rvNotices);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            selectedDate = LocalDate.of(year1, month1 + 1, dayOfMonth);
            tvSelectedDate.setText(selectedDate.toString());
        }, year, month, day);
        datePickerDialog.show();
    }

    private void loadNotices(androidx.recyclerview.widget.RecyclerView rvNotices) {
        new Thread(() -> {
            List<Notice> notices = noticeDatabase.getAllNotices();
            
            runOnUiThread(() -> {
                NoticeAdapter adapter = new NoticeAdapter(notices,
                        // Click Listener (View Details)
                        notice -> {
                            showNoticeDetails(notice);
                        },
                        // Long Click Listener (Delete)
                        notice -> showDeleteConfirmation(notice, rvNotices));
                rvNotices.setAdapter(adapter);
            });
        }).start();
    }

    private void showNoticeDetails(Notice notice) {
        new AlertDialog.Builder(this)
                .setTitle(notice.getTitle())
                .setMessage("Date: " + notice.getDate() + "\n\n" + notice.getContent())
                .setPositiveButton("OK", null)
                .show();
    }

    private void showDeleteConfirmation(Notice notice, androidx.recyclerview.widget.RecyclerView rvNotices) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Notice")
                .setMessage("Are you sure you want to delete this notice?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    new Thread(() -> {
                        boolean success = noticeDatabase.deleteNotice(notice.getId());
                        
                        runOnUiThread(() -> {
                            if (success) {
                                Toast.makeText(this, "Notice deleted successfully", Toast.LENGTH_SHORT).show();
                                loadNotices(rvNotices); // Refresh list
                            } else {
                                Toast.makeText(this, "Error deleting notice", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).start();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void postNotice() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String dateString = selectedDate.toString();
        String postedBy = Session.currentAdmin != null ? Session.currentAdmin.getUsername() : "Admin";
        
        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setDate(dateString);
        notice.setPostedBy(postedBy);

        new Thread(() -> {
            boolean success = noticeDatabase.addNotice(notice);
            
            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(this, "Notice posted successfully", Toast.LENGTH_SHORT).show();
                    etTitle.setText("");
                    etContent.setText("");
                    // Reset date to today
                    selectedDate = LocalDate.now();
                    tvSelectedDate.setText(selectedDate.toString());

                    // Refresh list
                    androidx.recyclerview.widget.RecyclerView rvNotices = findViewById(R.id.rvNotices);
                    loadNotices(rvNotices);
                } else {
                    Toast.makeText(this, "Error posting notice", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}
