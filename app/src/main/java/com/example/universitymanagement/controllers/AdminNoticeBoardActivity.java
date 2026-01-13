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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

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

        noticeDatabase = new NoticeDatabase();

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
        noticeDatabase.getAllNotices().addOnSuccessListener(queryDocumentSnapshots -> {
            java.util.List<Notice> notices = queryDocumentSnapshots.toObjects(Notice.class);
            NoticeAdapter adapter = new NoticeAdapter(notices,
                    // Click Listener (View Details)
                    notice -> {
                        Toast.makeText(AdminNoticeBoardActivity.this, "Clicked: " + notice.getTitle(),
                                Toast.LENGTH_SHORT).show();
                    },
                    // Long Click Listener (Delete)
                    notice -> showDeleteConfirmation(notice, rvNotices));
            rvNotices.setAdapter(adapter);
        });
    }

    private void showDeleteConfirmation(Notice notice, androidx.recyclerview.widget.RecyclerView rvNotices) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Notice")
                .setMessage("Are you sure you want to delete this notice?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    noticeDatabase.deleteNotice(notice.getId())
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Notice deleted successfully", Toast.LENGTH_SHORT).show();
                                loadNotices(rvNotices); // Refresh list
                            })
                            .addOnFailureListener(
                                    e -> Toast.makeText(this, "Error deleting notice", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void postNotice() {
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String dateString = selectedDate.toString();
        Notice notice = new Notice(title, content, dateString);

        noticeDatabase.addNotice(notice)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Notice posted successfully", Toast.LENGTH_SHORT).show();
                    etTitle.setText("");
                    etContent.setText("");
                    // Reset date to today
                    selectedDate = LocalDate.now();
                    tvSelectedDate.setText(selectedDate.toString());

                    // Refresh list
                    androidx.recyclerview.widget.RecyclerView rvNotices = findViewById(R.id.rvNotices);
                    loadNotices(rvNotices);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error posting notice: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
