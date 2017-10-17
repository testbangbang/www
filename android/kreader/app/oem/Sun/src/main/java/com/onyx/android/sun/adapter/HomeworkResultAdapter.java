package com.onyx.android.sun.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sun.R;
import com.onyx.android.sun.databinding.ItemHomeworkResultBinding;
import com.onyx.android.sun.view.PageRecyclerView;

/**
 * Created by li on 2017/10/16.
 */

public class HomeworkResultAdapter extends PageRecyclerView.PageAdapter {
    private int row;
    private int col;

    @Override
    public int getRowCount() {
        return row;
    }

    @Override
    public int getColumnCount() {
        return col;
    }

    @Override
    public int getDataCount() {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_homework_result_layout, parent, false);
        return new HomeworkResultViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public void onClick(View view) {

    }

    static class HomeworkResultViewHolder extends RecyclerView.ViewHolder {
        private ItemHomeworkResultBinding homeworkResultBinding;

        public HomeworkResultViewHolder(View itemView) {
            super(itemView);
            homeworkResultBinding = (ItemHomeworkResultBinding) DataBindingUtil.bind(itemView);
        }

        public ItemHomeworkResultBinding getHomeworkResultBinding() {
            return homeworkResultBinding;
        }
    }

    public void setRowOrCol(int row, int col) {
        this.row = row;
        this.col = col;
    }
}
