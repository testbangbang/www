package com.onyx.jdread.personal.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ItemGiftCenterBinding;

/**
 * Created by li on 2017/12/29.
 */

public class GiftCenterAdapter extends PageRecyclerView.PageAdapter implements View.OnClickListener {
    @Override
    public int getRowCount() {
        return 4;
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public int getDataCount() {
        return 4;
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

    static class GiftCenterViewHolder extends RecyclerView.ViewHolder {
        private ItemGiftCenterBinding binding;

        public GiftCenterViewHolder(View itemView) {
            super(itemView);
            binding = (ItemGiftCenterBinding) DataBindingUtil.bind(itemView);
        }

        public ItemGiftCenterBinding getBinding() {
            return binding;
        }

        public void bindTo() {

        }
    }
}
