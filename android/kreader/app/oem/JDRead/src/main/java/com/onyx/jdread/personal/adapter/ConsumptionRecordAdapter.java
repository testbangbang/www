package com.onyx.jdread.personal.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ItemConsumptionRecordBinding;

/**
 * Created by li on 2018/1/2.
 */

public class ConsumptionRecordAdapter extends PageRecyclerView.PageAdapter {
    @Override
    public int getRowCount() {
        return 6;
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public int getDataCount() {
        return 6;
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consumption_record_layout, parent, false);
        return new ConsumptionRecordViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    static class ConsumptionRecordViewHolder extends RecyclerView.ViewHolder {
        private ItemConsumptionRecordBinding bind;

        public ConsumptionRecordViewHolder(View itemView) {
            super(itemView);
            bind = (ItemConsumptionRecordBinding) DataBindingUtil.bind(itemView);
        }

        public ItemConsumptionRecordBinding getBind() {
            return bind;
        }
    }
}
