package com.onyx.android.plato.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.plato.R;
import com.onyx.android.plato.cloud.bean.SubjectBean;
import com.onyx.android.plato.databinding.ItemMistakeBinding;
import com.onyx.android.plato.view.PageRecyclerView;

import java.util.List;

/**
 * Created by li on 2017/11/29.
 */

public class MistakeAdapter extends PageRecyclerView.PageAdapter {
    private List<SubjectBean> data;

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mistake_layout, parent, false);
        return new MistakeViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SubjectBean subjectBean = data.get(position);
        MistakeViewHolder viewHolder = (MistakeViewHolder) holder;
        ItemMistakeBinding itemMistakeBinding = viewHolder.getItemMistakeBinding();
        itemMistakeBinding.setBean(subjectBean);
        itemMistakeBinding.itemMistake.setSelected(subjectBean.selected);
        viewHolder.itemView.setTag(position);
        viewHolder.itemView.setOnClickListener(this);
        itemMistakeBinding.executePendingBindings();
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag == null ) {
            return;
        }
        int position = (int) tag;
        SubjectBean subjectBean = data.get(position);
        subjectBean.selected = true;
        notifyItemChanged(position);
    }

    public void setData(List<SubjectBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    static class MistakeViewHolder extends RecyclerView.ViewHolder {
        private ItemMistakeBinding itemMistakeBinding;

        public MistakeViewHolder(View itemView) {
            super(itemView);
            itemMistakeBinding = (ItemMistakeBinding) DataBindingUtil.bind(itemView);
        }

        public ItemMistakeBinding getItemMistakeBinding() {
            return itemMistakeBinding;
        }
    }
}
