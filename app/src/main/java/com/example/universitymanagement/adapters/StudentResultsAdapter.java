package com.example.universitymanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universitymanagement.R;
import com.example.universitymanagement.models.Result;

import java.util.List;
import java.util.Map;

public class StudentResultsAdapter extends RecyclerView.Adapter<StudentResultsAdapter.ViewHolder> {

    private final List<Result> resultList;
    private final Map<String, com.example.universitymanagement.models.Subject> subjectMap;

    public StudentResultsAdapter(List<Result> resultList,
            Map<String, com.example.universitymanagement.models.Subject> subjectMap) {
        this.resultList = resultList;
        this.subjectMap = subjectMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Result result = resultList.get(position);
        com.example.universitymanagement.models.Subject subject = subjectMap.get(result.getSubjectId());

        holder.tvSemester.setText(result.getSemester() != null ? result.getSemester() : "N/A");

        if (subject != null) {
            holder.tvSubjectCode.setText(subject.getCode());
            holder.tvSubject.setText(subject.getName());
            holder.tvCredit.setText(String.valueOf(subject.getCredit()));
        } else {
            holder.tvSubjectCode.setText("N/A");
            holder.tvSubject.setText("Unknown");
            holder.tvCredit.setText("0");
        }

        holder.tvMarks.setText(String.valueOf(result.getMarks()));
        holder.tvGrade.setText(result.getGrade());
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSemester, tvSubjectCode, tvSubject, tvCredit, tvMarks, tvGrade;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSemester = itemView.findViewById(R.id.tvSemester);
            tvSubjectCode = itemView.findViewById(R.id.tvSubjectCode);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvCredit = itemView.findViewById(R.id.tvCredit);
            tvMarks = itemView.findViewById(R.id.tvMarks);
            tvGrade = itemView.findViewById(R.id.tvGrade);
        }
    }
}
