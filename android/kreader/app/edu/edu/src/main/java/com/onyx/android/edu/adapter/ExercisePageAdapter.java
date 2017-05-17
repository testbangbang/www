package com.onyx.android.edu.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 16/8/18.
 */
public class ExercisePageAdapter extends PageRecyclerView.PageAdapter{
    @Override
    public int getRowCount() {
        return 3;
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public int getDataCount() {
        return 14;
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExerciseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false));
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.content)
        TextView content;
        @Bind(R.id.subject_name)
        TextView subjectName;
        @Bind(R.id.content_layout)
        LinearLayout contentLayout;

        public ExerciseViewHolder(View view) {
            super(view);
//            R.layout.item_exercise
            ButterKnife.bind(this, view);
        }
    }
}
