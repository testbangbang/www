package com.onyx.jdread.shop.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ItemBuyReadVipBinding;
import com.onyx.jdread.shop.cloud.entity.jdbean.GetVipGoodsListResultBean;
import com.onyx.jdread.shop.event.VipGoodItemClickEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by li on 2018/1/13.
 */

public class BuyReadVipAdapter extends PageAdapter<PageRecyclerView.ViewHolder, GetVipGoodsListResultBean.DataBean, GetVipGoodsListResultBean.DataBean> implements View.OnClickListener {

    private EventBus eventBus;

    public BuyReadVipAdapter(EventBus eventBus) {
        this.eventBus = eventBus;
    }

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
        return getItemVMList().size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_buy_read_vip_layout, parent, false);
        return new BuyReadVipViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BuyReadVipViewHolder viewHolder = (BuyReadVipViewHolder) holder;
        final GetVipGoodsListResultBean.DataBean dataBean = getItemVMList().get(position);
        ItemBuyReadVipBinding bind = viewHolder.getBind();
        bind.itemReadVipPaid.setOnClickListener(this);
        bind.itemReadVipPaid.setTag(position);
        viewHolder.bindTo(dataBean);
    }

    @Override
    public void setRawData(List<GetVipGoodsListResultBean.DataBean> rawData, Context context) {
        super.setRawData(rawData, context);
        setItemVMList(rawData);
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag == null) {
            return;
        }
        int position = (int) tag;
        if (eventBus != null && getItemVMList() != null && getItemVMList().get(position) != null) {
            eventBus.post(new VipGoodItemClickEvent(getItemVMList().get(position)));
        }
    }

    static class BuyReadVipViewHolder extends RecyclerView.ViewHolder {
        private ItemBuyReadVipBinding bind;

        public BuyReadVipViewHolder(View itemView) {
            super(itemView);
            bind = DataBindingUtil.bind(itemView);
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
