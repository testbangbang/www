package com.onyx.android.edu.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.libedu.model.Subject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 16/8/12.
 */
public class SubjectAdapter extends RecyclerView.Adapter {

    public interface OnItemClickListener {
        void onItemClick(Subject subject);
    }

    private List<Subject> subjects = new ArrayList<>();
    private int[] resIds;
    private int selectPosition = 1;
    private OnItemClickListener onItemClickListener;

    public SubjectAdapter(List<Subject> subjects, int[] resIds) {
        this.subjects = subjects;
        this.resIds = resIds;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SubjectViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_subject_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final SubjectViewHolder subjectViewHolder = (SubjectViewHolder) holder;
        if (position == 0){
            subjectViewHolder.itemView.setVisibility(View.INVISIBLE);
        }else {
            subjectViewHolder.itemView.setVisibility(View.VISIBLE);
        }
        final int index = position > 0 ? position - 1 : position;
//        int resId = resIds[index];
//        subjectViewHolder.subjectIcon.setImageResource(resId);
        subjectViewHolder.subjectIcon.setVisibility(View.GONE);
        String name = subjects.get(index).getSubjectName();
        subjectViewHolder.itemName.setText(name);
        subjectViewHolder.itemContent.setEnabled(selectPosition != position);
        subjectViewHolder.itemName.setTextColor(selectPosition != position ? Color.BLACK : Color.WHITE);
        subjectViewHolder.itemContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPosition = position;
                notifyDataSetChanged();
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(subjects.get(index));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjects.size() + 1;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.subject_icon)
        ImageView subjectIcon;
        @Bind(R.id.item_name)
        TextView itemName;
        @Bind(R.id.item_content)
        LinearLayout itemContent;

        public SubjectViewHolder(View itemView) {
//            R.layout.view_subject_list_item
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
