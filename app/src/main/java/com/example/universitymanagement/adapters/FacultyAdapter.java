package com.example.universitymanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universitymanagement.models.Faculty;

import java.util.List;

public class FacultyAdapter extends RecyclerView.Adapter<FacultyAdapter.FacultyViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Faculty faculty);
    }

    private final List<Faculty> facultyList;
    private final OnItemClickListener listener;

    public FacultyAdapter(List<Faculty> facultyList, OnItemClickListener listener) {
        this.facultyList = facultyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FacultyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent,
                false);
        return new FacultyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacultyViewHolder holder, int position) {
        Faculty faculty = facultyList.get(position);
        holder.name.setText(faculty.getName());
        holder.details.setText(String.format("Email: %s | Dept: %s", faculty.getEmail(), faculty.getDepartment()));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(faculty));
    }

    @Override
    public int getItemCount() {
        return facultyList.size();
    }

    static class FacultyViewHolder extends RecyclerView.ViewHolder {
        TextView name, details;

        public FacultyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(android.R.id.text1);
            details = itemView.findViewById(android.R.id.text2);
        }
    }
}
