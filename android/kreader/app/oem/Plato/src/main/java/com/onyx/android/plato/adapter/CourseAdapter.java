package com.onyx.android.plato.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.cloud.bean.SubjectBean;
import com.onyx.android.plato.databinding.HomeworkCourseBinding;
import com.onyx.android.plato.view.PageRecyclerView;

import java.util.List;

/**
 * Created by li on 2017/10/9.
 */

public class CourseAdapter extends PageRecyclerView.PageAdapter {
    private int current = 0;
    private List<SubjectBean> data;
    private String[] data;
    private int courseColumnCount = SunApplication.getInstance().getResources().getInteger(R.integer.course_adapter_col);
    private int courseRowCount = SunApplication.getInstance().getResources().getInteger(R.integer.course_adapter_row);

    public CourseAdapter() {
    }

    public CourseAdapter(int courseColumnCount, int courseRowCount) {
        this.courseColumnCount = courseColumnCount;
        this.courseRowCount = courseRowCount;
    }

    @Override
    public int getRowCount() {
        return courseRowCount;
    }

    @Override
    public int getColumnCount() {
        return courseColumnCount;
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_homework_course_layout, parent, false);
        CourseViewHolder courseViewHolder = new CourseViewHolder(view);
        return courseViewHolder;
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CourseViewHolder courseViewHolder = (CourseViewHolder) holder;
        HomeworkCourseBinding binding = courseViewHolder.getBinding();
        binding.setCourse(data.get(position));
        courseViewHolder.itemView.setOnClickListener(this);
        courseViewHolder.itemView.setTag(position);
        binding.itemHomeworkCourse.setSelected(position == current);
        binding.executePendingBindings();
    }

    @Override
    public void onClick(View view) {
        Object tag = view.getTag();
        if(tag == null) {
            return;
        }
        current = (int) tag;
        if (onRecyclerViewItemClickListener != null) {
            onRecyclerViewItemClickListener.onItemClick(view, current);
        }
        notifyDataSetChanged();
    }

    public void setData(List<SubjectBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        private HomeworkCourseBinding courseBinding;

        public CourseViewHolder(View itemView) {
            super(itemView);
            courseBinding = (HomeworkCourseBinding) DataBindingUtil.bind(itemView);
        }

        public HomeworkCourseBinding getBinding() {
            return courseBinding;
        }
    }
}
