package com.onyx.android.plato.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.cloud.bean.ContentBean;
import com.onyx.android.plato.cloud.bean.FinishContent;
import com.onyx.android.plato.databinding.ItemStudyReportBinding;
import com.onyx.android.plato.event.ToStudyReportDeatilEvent;
import com.onyx.android.plato.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by li on 2017/10/12.
 */

public class StudyReportAdapter extends PageRecyclerView.PageAdapter {
    private List<FinishContent> data;

    @Override
    public int getRowCount() {
        return SunApplication.getInstance().getResources().getInteger(R.integer.homework_study_report_row);
    }

    @Override
    public int getColumnCount() {
        return SunApplication.getInstance().getResources().getInteger(R.integer.homework_study_report_col);
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
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
    }

    @Override
    public void setOnItemClick(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        super.setOnItemClick(onRecyclerViewItemClickListener);
    }

    @Override
    public void onClick(View view) {
        Object tag = view.getTag();
        if (tag == null) {
            return;
        }
        int position = (int) tag;
        ContentBean contentBean = data.get(position);
        EventBus.getDefault().post(new ToStudyReportDeatilEvent(contentBean.id,contentBean.title));
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
