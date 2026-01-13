package com.example.universitymanagement.controllers;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.universitymanagement.R;
import com.example.universitymanagement.adapters.NoticeAdapter;
import com.example.universitymanagement.database.NoticeDatabase;
import com.example.universitymanagement.models.Notice;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_opening_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.opening_page_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnStudentLogin)
                .setOnClickListener(v -> startActivity(new Intent(this, StudentLoginActivity.class)));
        findViewById(R.id.btnFacultyLogin)
                .setOnClickListener(v -> startActivity(new Intent(this, FacultyLoginActivity.class)));
        findViewById(R.id.btnAdminLogin)
                .setOnClickListener(v -> startActivity(new Intent(this, AdminLoginActivity.class)));

        RecyclerView rvNoticeBoard = findViewById(R.id.rvNoticeBoard);
        rvNoticeBoard.setLayoutManager(new LinearLayoutManager(this));

        NoticeDatabase noticeDatabase = new NoticeDatabase();
        noticeDatabase.getAllNotices().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                java.util.List<Notice> noticeList = task.getResult().toObjects(Notice.class);
                NoticeAdapter adapter = new NoticeAdapter(noticeList, this::showNoticeDetails, null);
                rvNoticeBoard.setAdapter(adapter);
            } else {
                // Handle error or show empty state
            }
        });
    }

    private void showNoticeDetails(Notice notice) {
        new AlertDialog.Builder(this)
                .setTitle("Notice Details")
                .setMessage(notice.getTitle() + "\n\n" + notice.getContent())
                .setPositiveButton("OK", null)
                .show();
    }
}