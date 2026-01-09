package com.example.universitymanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.universitymanagement.R;
import com.example.universitymanagement.models.Notice;
import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> {

    private final List<Notice> noticeList;
    private final OnItemClickListener listener;
    private final OnItemLongClickListener longListener;

    public interface OnItemClickListener {
        void onItemClick(Notice notice);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Notice notice);
    }

    public NoticeAdapter(List<Notice> noticeList, OnItemClickListener listener, OnItemLongClickListener longListener) {
        this.noticeList = noticeList;
        this.listener = listener;
        this.longListener = longListener;
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice, parent, false);
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        Notice notice = noticeList.get(position);
        holder.bind(notice, listener, longListener);
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    static class NoticeViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNoticeDate;
        private final TextView tvNoticeTitle;

        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNoticeDate = itemView.findViewById(R.id.tvNoticeDate);
            tvNoticeTitle = itemView.findViewById(R.id.tvNoticeTitle);
        }

        public void bind(final Notice notice, final OnItemClickListener listener,
                final OnItemLongClickListener longListener) {
            tvNoticeDate.setText(notice.getDate());
            tvNoticeTitle.setText(notice.getTitle());
            itemView.setOnClickListener(v -> listener.onItemClick(notice));
            itemView.setOnLongClickListener(v -> {
                longListener.onItemLongClick(notice);
                return true;
            });
        }
    }
}
