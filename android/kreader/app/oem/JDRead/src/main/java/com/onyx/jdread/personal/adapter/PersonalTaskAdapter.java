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
import com.onyx.jdread.personal.model.PersonalTaskData;

import java.util.List;

/**
 * Created by li on 2018/1/3.
 */

public class PersonalTaskAdapter extends PageRecyclerView.PageAdapter implements View.OnClickListener {
    private List<PersonalTaskData> data;

    @Override
    public int getRowCount() {
        return ResManager.getInteger(R.integer.personal_task_row);
    }

    @Override
    public int getColumnCount() {
        return ResManager.getInteger(R.integer.personal_task_col);
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_personal_task_layout, parent, false);
        return new PersonalTaskViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PersonalTaskViewHolder viewHolder = (PersonalTaskViewHolder) holder;
        ItemPersonalTaskBinding bind = viewHolder.getBind();
        bind.taskReceive.setOnClickListener(this);
        bind.taskReceive.setTag(position);
        viewHolder.bindTo(data.get(position));
    }

    public void setData(List<PersonalTaskData> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag == null) {
            return;
        }
        int position = (int) tag;
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(position);
        }
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

        public void bindTo(PersonalTaskData bean) {
            bind.setBean(bean);
            bind.executePendingBindings();
        }
    }
}
