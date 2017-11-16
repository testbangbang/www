package com.onyx.android.plato.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.cloud.bean.ContentBean;
import com.onyx.android.plato.databinding.HomeworkItemBinding;
import com.onyx.android.plato.event.UnfinishedEvent;
import com.onyx.android.plato.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by li on 2017/9/30.
 */

public class HomeworkUnfinishedAdapter extends PageRecyclerView.PageAdapter {
    private List<ContentBean> data;

    @Override
    public int getRowCount() {
        return SunApplication.getInstance().getResources().getInteger(R.integer.homework_adapter_row);
    }

    @Override
    public int getColumnCount() {
        return SunApplication.getInstance().getResources().getInteger(R.integer.homework_adapter_col);
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
        int position = (int) tag;
        ContentBean contentBean = data.get(position);
        EventBus.getDefault().post(new UnfinishedEvent(contentBean.id, contentBean.type, contentBean.title));
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
