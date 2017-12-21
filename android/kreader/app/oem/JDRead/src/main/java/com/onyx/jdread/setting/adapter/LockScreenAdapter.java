package com.onyx.jdread.setting.adapter;

import android.databinding.DataBindingUtil;
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

public class LockScreenAdapter extends PageRecyclerView.PageAdapter implements View.OnClickListener {
    private CharSequence[] times;
    private CharSequence[] values;
    private String currentScreenTimeout;

    @Override
    public int getRowCount() {
        return JDReadApplication.getInstance().getResources().getInteger(R.integer.lock_screen_adapter_row);
    }

    @Override
    public int getColumnCount() {
        return JDReadApplication.getInstance().getResources().getInteger(R.integer.lock_screen_adapter_col);
    }

    @Override
    public int getDataCount() {
        return times == null ? 0 : times.length;
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_refresh_layout, parent, false);
        return new LockScreenViewHolder(view);
    }

    public void setTimes(final CharSequence[] times, final CharSequence[] values, final String currentScreenTimeout) {
        this.times = times;
        this.values = values;
        this.currentScreenTimeout = currentScreenTimeout;
        notifyDataSetChanged();
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LockScreenViewHolder viewHolder = (LockScreenViewHolder) holder;
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        ItemRefreshBinding binding = viewHolder.getBinding();
        binding.setShow(values[position] == currentScreenTimeout);
        viewHolder.bindTo(times[position]);
    }

    public String getCurrentScreenTimeout() {
        return currentScreenTimeout;
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag == null) {
            return;
        }
        int position = (int) tag;
        currentScreenTimeout = (String) values[position];
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(position);
        }
        notifyDataSetChanged();
    }

    static class LockScreenViewHolder extends RecyclerView.ViewHolder {
        private ItemRefreshBinding binding;

        public LockScreenViewHolder(View itemView) {
            super(itemView);
            binding = (ItemRefreshBinding) DataBindingUtil.bind(itemView);
        }

        public ItemRefreshBinding getBinding() {
            return binding;
        }

        public void bindTo(CharSequence pageTime) {
            binding.setPageTime(pageTime);
            binding.executePendingBindings();
        }
    }
}
