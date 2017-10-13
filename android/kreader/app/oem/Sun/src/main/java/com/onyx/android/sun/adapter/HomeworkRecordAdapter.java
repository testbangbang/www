package com.onyx.android.sun.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.databinding.ItemHomeworkRecordBinding;
import com.onyx.android.sun.view.PageRecyclerView;

/**
 * Created by li on 2017/10/13.
 */

public class HomeworkRecordAdapter extends PageRecyclerView.PageAdapter {
    @Override
    public int getRowCount() {
        return SunApplication.getInstence().getResources().getInteger(R.integer.homework_record_row);
    }

    @Override
    public int getColumnCount() {
        return SunApplication.getInstence().getResources().getInteger(R.integer.homework_record_col);
    }

    @Override
    public int getDataCount() {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_homework_record, parent, false);
        return new HomeworkRecordViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public void onClick(View view) {

    }

    static class HomeworkRecordViewHolder extends RecyclerView.ViewHolder {
        private ItemHomeworkRecordBinding homeworkRecordBinding;

        public HomeworkRecordViewHolder(View itemView) {
            super(itemView);
            homeworkRecordBinding = (ItemHomeworkRecordBinding) DataBindingUtil.bind(itemView);
        }

        public ItemHomeworkRecordBinding getHomeworkRecordBinding() {
            return homeworkRecordBinding;
        }
    }
}
