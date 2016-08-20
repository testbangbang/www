package com.onyx.android.edu.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.edu.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 16/8/12.
 */
public class SubjectAdapter extends RecyclerView.Adapter {

    private String[] subjectNames;
    private int[] resIds;
    private int selectPosition = 1;

    public SubjectAdapter(String[] subjectNames, int[] resIds) {
        this.subjectNames = subjectNames;
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
        int index = position > 0 ? position - 1 : position;
        int resId = resIds[index];
        subjectViewHolder.subjectIcon.setImageResource(resId);
        String name = subjectNames[index];
        subjectViewHolder.itemName.setText(name);
        subjectViewHolder.itemContent.setEnabled(selectPosition != position);
        subjectViewHolder.itemContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPosition = position;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjectNames.length + 1;
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
