package com.onyx.android.sun.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.cloud.bean.PracticesResultBean;
import com.onyx.android.sun.databinding.TodayTaskItemBinding;
import com.onyx.android.sun.view.PageRecyclerView;

import java.util.List;

/**
 * Created by li on 2017/9/30.
 */

public class TodayTaskAdapter extends PageRecyclerView.PageAdapter<TodayTaskAdapter.ViewHolder> {
    private List<PracticesResultBean.DataBean.ContentBean> data;

    public TodayTaskAdapter() {
    }

    public void setData(List<PracticesResultBean.DataBean.ContentBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getRowCount() {
        return SunApplication.getInstence().getResources().getInteger(R.integer.today_task_adapter_row);
    }

    @Override
    public int getColumnCount() {
        return SunApplication.getInstence().getResources().getInteger(R.integer.today_task_adapter_col);
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_today_task_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(ViewHolder holder, int position) {
        holder.getBind().setData(data.get(position));
        holder.itemView.setOnClickListener(this);
        holder.itemView.setTag(position);
        holder.getBind().executePendingBindings();
    }

    @Override
    public void onClick(View view) {
        Object tag = view.getTag();
        if (tag == null) {
            return;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TodayTaskItemBinding bind;

        public ViewHolder(View itemView) {
            super(itemView);
            bind = DataBindingUtil.bind(itemView);
        }

        public TodayTaskItemBinding getBind() {
            return bind;
        }
    }
}
