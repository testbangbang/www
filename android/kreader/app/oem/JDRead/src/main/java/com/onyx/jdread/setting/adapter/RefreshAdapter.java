package com.onyx.jdread.setting.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ItemRefreshBinding;

/**
 * Created by li on 2017/12/21.
 */

public class RefreshAdapter extends PageRecyclerView.PageAdapter implements View.OnClickListener {
    private boolean a2Mode;
    private String[] data;
    private String currentPage;

    @Override
    public int getRowCount() {
        return JDReadApplication.getInstance().getResources().getInteger(R.integer.refresh_adapter_row);
    }

    @Override
    public int getColumnCount() {
        return JDReadApplication.getInstance().getResources().getInteger(R.integer.refresh_adapter_col);
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.length;
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_refresh_layout, parent, false);
        return new RefreshViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RefreshViewHolder viewHolder = (RefreshViewHolder) holder;
        ItemRefreshBinding binding = viewHolder.getBinding();
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        binding.setShow(data[position].equals(currentPage));
        viewHolder.bindTo(data[position]);
    }

    public void setA2Mode(boolean a2Mode) {
        this.a2Mode = a2Mode;
    }

    public void setData(String[] data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag == null || a2Mode) {
            return;
        }

        int position = (int) tag;
        currentPage = data[position];
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(position);
        }
        notifyDataSetChanged();
    }

    static class RefreshViewHolder extends RecyclerView.ViewHolder {
        private ItemRefreshBinding binding;

        public RefreshViewHolder(View itemView) {
            super(itemView);
            binding = (ItemRefreshBinding) DataBindingUtil.bind(itemView);
        }

        public ItemRefreshBinding getBinding() {
            return binding;
        }

        public void bindTo(String pageTime) {
            binding.setPageTime(pageTime);
            binding.executePendingBindings();
        }
    }
}
