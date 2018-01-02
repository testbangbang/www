package com.onyx.jdread.personal.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ItemPointsForBinding;
import com.onyx.jdread.personal.model.PointsForData;

import java.util.List;

/**
 * Created by li on 2018/1/2.
 */

public class PointsForAdapter extends PageRecyclerView.PageAdapter implements View.OnClickListener {
    private List<PointsForData> data;

    @Override
    public int getRowCount() {
        return 5;
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_points_for_layout, parent, false);
        return new PointsViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PointsViewHolder viewHolder = (PointsViewHolder) holder;
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        viewHolder.bindTo(data.get(position));
    }

    public void setData(List<PointsForData> data) {
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

    static class PointsViewHolder extends RecyclerView.ViewHolder {
        private ItemPointsForBinding binding;

        public PointsViewHolder(View itemView) {
            super(itemView);
            binding = (ItemPointsForBinding) DataBindingUtil.bind(itemView);
        }

        public ItemPointsForBinding getBinding() {
            return binding;
        }

        public void bindTo(PointsForData data) {
            binding.setData(data);
            binding.executePendingBindings();
        }
    }
}
