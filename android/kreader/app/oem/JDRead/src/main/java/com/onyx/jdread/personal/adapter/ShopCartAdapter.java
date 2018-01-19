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
import com.onyx.jdread.databinding.ItemShopCartBinding;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.shop.model.ShopCartItemData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2018/1/4.
 */

public class ShopCartAdapter extends PageRecyclerView.PageAdapter implements View.OnClickListener {
    private List<ShopCartItemData> data;

    @Override
    public int getRowCount() {
        return ResManager.getResManager().getInteger(R.integer.shop_cart_row);
    }

    @Override
    public int getColumnCount() {
        return ResManager.getResManager().getInteger(R.integer.shop_cart_col);
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_cart_layout, parent, false);
        return new ShopCartViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ShopCartViewHolder viewHolder = (ShopCartViewHolder) holder;
        ShopCartItemData itemData = data.get(position);
        viewHolder.itemView.setTag(position);
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.getBind().itemShopCartCheck.setTag(position);
        viewHolder.getBind().itemShopCartCheck.setOnClickListener(this);
        viewHolder.bindTo(itemData);
    }

    public void setData(List<ShopCartItemData> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public List<ShopCartItemData> getData() {
        return data;
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag == null) {
            return;
        }
        int position = (int) tag;
        ShopCartItemData itemData = data.get(position);
        boolean checked = itemData.isChecked();
        itemData.setChecked(!checked);
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(position);
        }
    }

    static class ShopCartViewHolder extends RecyclerView.ViewHolder {
        private ItemShopCartBinding bind;

        public ShopCartViewHolder(View itemView) {
            super(itemView);
            bind = (ItemShopCartBinding) DataBindingUtil.bind(itemView);
        }

        public ItemShopCartBinding getBind() {
            return bind;
        }

        public void bindTo(ShopCartItemData itemData) {
            bind.setItemData(itemData);
            bind.executePendingBindings();
        }
    }
}
