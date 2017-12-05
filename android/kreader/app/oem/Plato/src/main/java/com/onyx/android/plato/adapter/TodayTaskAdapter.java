package com.onyx.android.plato.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.cloud.bean.ContentBean;
import com.onyx.android.plato.databinding.TodayTaskItemBinding;
import com.onyx.android.plato.event.UnfinishedEvent;
import com.onyx.android.plato.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by li on 2017/9/30.
 */

public class TodayTaskAdapter extends PageRecyclerView.PageAdapter<TodayTaskAdapter.ViewHolder> {
    private List<ContentBean> data;

    public TodayTaskAdapter() {
    }

    public void setData(List<ContentBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getRowCount() {
        return SunApplication.getInstance().getResources().getInteger(R.integer.today_task_adapter_row);
    }

    @Override
    public int getColumnCount() {
        return SunApplication.getInstance().getResources().getInteger(R.integer.today_task_adapter_col);
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
        int position = (int) tag;
        ContentBean contentBean = data.get(position);
        EventBus.getDefault().post(new UnfinishedEvent(contentBean.id, contentBean.practiceId, contentBean.type, contentBean.title));
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