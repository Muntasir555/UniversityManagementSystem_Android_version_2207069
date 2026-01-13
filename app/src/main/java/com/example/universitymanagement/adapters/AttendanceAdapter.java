package com.example.universitymanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universitymanagement.R;
import com.example.universitymanagement.models.Student;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {

    private final List<Student> studentList;
    private final Map<String, Boolean> attendanceStatus;

    public AttendanceAdapter(List<Student> studentList) {
        this.studentList = studentList;
        this.attendanceStatus = new HashMap<>();
        // Default all to present (true)
        for (Student s : studentList) {
            attendanceStatus.put(s.getId(), true);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Using simple built-in layout or creating a small custom item row
        // Let's quickly create a custom row layout inline if possible, or use standard
        // Ideally we need a layout with Name and Checkbox.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.tvName.setText(student.getName());
        holder.cbPresent.setChecked(attendanceStatus.get(student.getId()));

        holder.cbPresent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            attendanceStatus.put(student.getId(), isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public Map<String, Boolean> getAttendanceStatus() {
        return attendanceStatus;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        CheckBox cbPresent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            cbPresent = itemView.findViewById(R.id.cbPresent);
        }
    }
}
