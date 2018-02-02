package com.onyx.jdread.shop.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ItemBuyReadVipBinding;
import com.onyx.jdread.shop.cloud.entity.jdbean.GetVipGoodsListResultBean;

import java.util.List;

/**
 * Created by li on 2018/1/13.
 */

public class BuyReadVipAdapter extends PageAdapter implements View.OnClickListener {
    private List<GetVipGoodsListResultBean.DataBean> data;

    @Override
    public int getRowCount() {
        return JDReadApplication.getInstance().getResources().getInteger(R.integer.buy_read_vip_adapter_row);
    }

    @Override
    public int getColumnCount() {
        return JDReadApplication.getInstance().getResources().getInteger(R.integer.buy_read_vip_adapter_col);
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_buy_read_vip_layout, parent, false);
        return new BuyReadVipViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BuyReadVipViewHolder viewHolder = (BuyReadVipViewHolder) holder;
        ItemBuyReadVipBinding bind = viewHolder.getBind();
        bind.itemReadVipPaid.setOnClickListener(this);
        bind.itemReadVipPaid.setTag(position);
        viewHolder.bindTo(data.get(position));
    }

    public void setData(List<GetVipGoodsListResultBean.DataBean> data) {
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

    static class BuyReadVipViewHolder extends RecyclerView.ViewHolder {
        private ItemBuyReadVipBinding bind;

        public BuyReadVipViewHolder(View itemView) {
            super(itemView);
            bind =  DataBindingUtil.bind(itemView);
        }

        public ItemBuyReadVipBinding getBind() {
            return bind;
        }

        public void bindTo(GetVipGoodsListResultBean.DataBean data) {
            bind.setData(data);
            bind.executePendingBindings();
        }
    }
}
