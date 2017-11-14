package com.onyx.android.sun.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.cloud.bean.ContentBean;
import com.onyx.android.sun.databinding.ItemRemindBinding;
import com.onyx.android.sun.event.DeleteRemindEvent;
import com.onyx.android.sun.event.UnfinishedEvent;
import com.onyx.android.sun.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by li on 2017/11/13.
 */

public class RemindAdapter extends PageRecyclerView.PageAdapter {
    private List<ContentBean> data;

    @Override
    public int getRowCount() {
        return SunApplication.getInstance().getResources().getInteger(R.integer.remind_adapter_row);
    }

    @Override
    public int getColumnCount() {
        return SunApplication.getInstance().getResources().getInteger(R.integer.remind_adapter_col);
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_remind_layout, parent, false);
        return new RemindViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RemindViewHolder viewHolder = (RemindViewHolder) holder;
        ContentBean bean = data.get(position);
        viewHolder.getItemRemindBinding().setData(bean);
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        viewHolder.getItemRemindBinding().executePendingBindings();
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag == null) {
            return;
        }

        int position = (int) tag;
        ContentBean bean = data.get(position);
        EventBus.getDefault().post(new UnfinishedEvent(bean.practiceStudentId, bean.type, bean.title));
        EventBus.getDefault().post(new DeleteRemindEvent(bean.id));
    }

    public void setData(List<ContentBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    static class RemindViewHolder extends RecyclerView.ViewHolder {
        private ItemRemindBinding itemRemindBinding;

        public RemindViewHolder(View itemView) {
            super(itemView);
            itemRemindBinding = (ItemRemindBinding) DataBindingUtil.bind(itemView);
        }

        public ItemRemindBinding getItemRemindBinding() {
            return itemRemindBinding;
        }
    }
}
