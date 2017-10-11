package com.onyx.android.sun.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.cloud.bean.ContentBean;
import com.onyx.android.sun.databinding.HomeworkItemBinding;
import com.onyx.android.sun.view.PageRecyclerView;

import java.util.List;

/**
 * Created by li on 2017/9/30.
 */

public class HomeworkUnfinishedAdapter extends PageRecyclerView.PageAdapter {
    private List<ContentBean> data;

    @Override
    public int getRowCount() {
        return SunApplication.getInstence().getResources().getInteger(R.integer.homework_adapter_row);
    }

    @Override
    public int getColumnCount() {
        return SunApplication.getInstence().getResources().getInteger(R.integer.homework_adapter_col);
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_homework_layout, parent, false);
        HomeworkViewHolder homeworkViewHolder = new HomeworkViewHolder(view);
        return homeworkViewHolder;
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HomeworkViewHolder viewHolder = (HomeworkViewHolder) holder;
        viewHolder.getBind().setData(data.get(position));
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        viewHolder.getBind().executePendingBindings();
    }

    @Override
    public void onClick(View view) {
        Object tag = view.getTag();
        if (tag == null) {
            return;
        }
    }

    public void setData(List<ContentBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    static class HomeworkViewHolder extends RecyclerView.ViewHolder {
        private final HomeworkItemBinding bind;

        public HomeworkViewHolder(View itemView) {
            super(itemView);
            bind = (HomeworkItemBinding) DataBindingUtil.bind(itemView);
        }

        public HomeworkItemBinding getBind() {
            return bind;
        }
    }
}
