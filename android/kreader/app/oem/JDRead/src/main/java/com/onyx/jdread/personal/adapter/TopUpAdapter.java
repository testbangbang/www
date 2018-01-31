package com.onyx.jdread.personal.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ItemDialogTopUpBinding;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetRechargePackageBean;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by li on 2017/12/30.
 */

public class TopUpAdapter extends PageRecyclerView.PageAdapter implements View.OnClickListener {
    private List<GetRechargePackageBean.DataBean> data;

    @Override
    public int getRowCount() {
        return ResManager.getInteger(R.integer.top_up_adapter_row);
    }

    @Override
    public int getColumnCount() {
        return ResManager.getInteger(R.integer.top_up_adapter_col);
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_item_top_up_layout, parent, false);
        return new TopUpViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TopUpViewHolder viewHolder = (TopUpViewHolder) holder;
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        viewHolder.bindTo(data.get(position));
    }

    public void setData(List<GetRechargePackageBean.DataBean> data) {
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

    static class TopUpViewHolder extends RecyclerView.ViewHolder {
        private ItemDialogTopUpBinding bind;

        public TopUpViewHolder(View itemView) {
            super(itemView);
            bind = (ItemDialogTopUpBinding) DataBindingUtil.bind(itemView);
        }

        public ItemDialogTopUpBinding getBind() {
            return bind;
        }

        public void bindTo(GetRechargePackageBean.DataBean bean) {
            bind.setBean(bean);
            bind.executePendingBindings();
        }
    }
}
