package com.onyx.jdread.personal.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ItemPersonalTaskBinding;
import com.onyx.jdread.main.common.ResManager;

/**
 * Created by li on 2018/1/3.
 */

public class PersonalTaskAdapter extends PageRecyclerView.PageAdapter {
    private String[] data;

    @Override
    public int getRowCount() {
        return ResManager.getResManager().getInteger(R.integer.personal_task_row);
    }

    @Override
    public int getColumnCount() {
        return ResManager.getResManager().getInteger(R.integer.personal_task_col);
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.length;
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_personal_task_layout, parent, false);
        return new PersonalTaskViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PersonalTaskViewHolder viewHolder = (PersonalTaskViewHolder) holder;
        viewHolder.bindTo(data[position]);
    }

    public void setData(String[] data) {
        this.data = data;
        notifyDataSetChanged();
    }

    static class PersonalTaskViewHolder extends RecyclerView.ViewHolder {
        private ItemPersonalTaskBinding bind;

        public PersonalTaskViewHolder(View itemView) {
            super(itemView);
            bind = (ItemPersonalTaskBinding) DataBindingUtil.bind(itemView);
        }

        public ItemPersonalTaskBinding getBind() {
            return bind;
        }

        public void bindTo(String dec) {
            bind.setDec(dec);
            bind.executePendingBindings();
        }
    }
}
