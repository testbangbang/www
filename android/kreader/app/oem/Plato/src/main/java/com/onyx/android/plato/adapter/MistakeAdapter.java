package com.onyx.android.plato.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.cloud.bean.SubjectBean;
import com.onyx.android.plato.databinding.ItemMistakeBinding;
import com.onyx.android.plato.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2017/11/29.
 */

public class MistakeAdapter extends PageRecyclerView.PageAdapter {
    private List<SubjectBean> data = new ArrayList<>();

    @Override
    public int getRowCount() {
        return SunApplication.getInstance().getResources().getInteger(R.integer.mistake_adapter_row);
    }

    @Override
    public int getColumnCount() {
        return SunApplication.getInstance().getResources().getInteger(R.integer.mistake_adapter_col);
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
        if (tag == null) {
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

    public List<SubjectBean> getData() {
        return data;
    }

    public void clear() {
        if (data != null && data.size() > 0) {
            data.clear();
            notifyDataSetChanged();
        }
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
