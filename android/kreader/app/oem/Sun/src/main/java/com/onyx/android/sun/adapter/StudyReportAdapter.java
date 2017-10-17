package com.onyx.android.sun.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.cloud.bean.FinishContent;
import com.onyx.android.sun.databinding.ItemStudyReportBinding;
import com.onyx.android.sun.view.PageRecyclerView;

import java.util.List;

/**
 * Created by li on 2017/10/12.
 */

public class StudyReportAdapter extends PageRecyclerView.PageAdapter {
    private List<FinishContent> data;

    @Override
    public int getRowCount() {
        return SunApplication.getInstence().getResources().getInteger(R.integer.homework_study_report_row);
    }

    @Override
    public int getColumnCount() {
        return SunApplication.getInstence().getResources().getInteger(R.integer.homework_study_report_col);
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_study_report, parent, false);
        return new StudyReportViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        StudyReportViewHolder viewHolder = (StudyReportViewHolder) holder;
        ItemStudyReportBinding studyReportBinding = viewHolder.getStudyReportBinding();
        studyReportBinding.setContent(data.get(position));
        studyReportBinding.executePendingBindings();
    }

    @Override
    public void onClick(View view) {

    }

    public void setData(List<FinishContent> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    static class StudyReportViewHolder extends RecyclerView.ViewHolder {
        private ItemStudyReportBinding studyReportBinding;

        public StudyReportViewHolder(View itemView) {
            super(itemView);
            studyReportBinding = DataBindingUtil.bind(itemView);
        }

        public ItemStudyReportBinding getStudyReportBinding() {
            return studyReportBinding;
        }
    }
}
