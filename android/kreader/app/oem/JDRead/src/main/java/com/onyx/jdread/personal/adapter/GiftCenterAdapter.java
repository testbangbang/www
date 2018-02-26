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
import com.onyx.jdread.databinding.ItemGiftCenterBinding;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.personal.cloud.entity.jdbean.GiftDetailBean;

import java.util.List;

/**
 * Created by li on 2017/12/29.
 */

public class GiftCenterAdapter extends PageRecyclerView.PageAdapter implements View.OnClickListener {
    private List<GiftDetailBean> data;

    @Override
    public int getRowCount() {
        return ResManager.getInteger(R.integer.gift_center_adapter_row);
    }

    @Override
    public int getColumnCount() {
        return ResManager.getInteger(R.integer.gift_center_adapter_col);
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gift_center_layout, parent, false);
        return new GiftCenterViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GiftCenterViewHolder viewHolder = (GiftCenterViewHolder) holder;
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        viewHolder.bindTo(data.get(position));
    }

    public void setData(List<GiftDetailBean> data) {
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

    public void clear() {
        if (data != null && data.size() > 0) {
            data.clear();
            notifyDataSetChanged();
        }
    }

    static class GiftCenterViewHolder extends RecyclerView.ViewHolder {
        private ItemGiftCenterBinding binding;

        public GiftCenterViewHolder(View itemView) {
            super(itemView);
            binding = (ItemGiftCenterBinding) DataBindingUtil.bind(itemView);
        }

        public ItemGiftCenterBinding getBinding() {
            return binding;
        }

        public void bindTo(GiftDetailBean bean) {
            binding.setBean(bean);
            binding.executePendingBindings();
        }
    }
}
