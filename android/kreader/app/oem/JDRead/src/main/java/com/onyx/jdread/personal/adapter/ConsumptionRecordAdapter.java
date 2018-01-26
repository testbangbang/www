package com.onyx.jdread.personal.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ItemConsumptionRecordBinding;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.personal.cloud.entity.jdbean.ConsumeRecordBean;

import java.util.List;

/**
 * Created by li on 2018/1/2.
 */

public class ConsumptionRecordAdapter extends PageRecyclerView.PageAdapter {
    private List<ConsumeRecordBean.DataBean> data;

    @Override
    public int getRowCount() {
        return ResManager.getInteger(R.integer.personal_consumption_record_row);
    }

    @Override
    public int getColumnCount() {
        return ResManager.getInteger(R.integer.personal_consumption_record_col);
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consumption_record_layout, parent, false);
        return new ConsumptionRecordViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ConsumptionRecordViewHolder viewHolder = (ConsumptionRecordViewHolder) holder;
        viewHolder.bindTo(data.get(position));
    }

    public void setData(List<ConsumeRecordBean.DataBean> data) {
        this.data = data;
        notifyDataSetChanged();
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

        public void bindTo(ConsumeRecordBean.DataBean bean) {
            bind.setBean(bean);
            bind.executePendingBindings();
        }
    }
}
