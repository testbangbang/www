package com.onyx.jdread.personal.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ItemReadPreferenceBinding;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;

import java.util.List;

/**
 * Created by li on 2018/1/3.
 */

public class ReadPreferenceAdapter extends PageRecyclerView.PageAdapter implements View.OnClickListener {
    private List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> data;

    @Override
    public int getRowCount() {
        return ResManager.getResInteger(R.integer.read_preference_row);
    }

    @Override
    public int getColumnCount() {
        return ResManager.getResInteger(R.integer.read_preference_col);
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_read_preference_layout, parent, false);
        return new ReadPreferenceViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ReadPreferenceViewHolder viewHolder = (ReadPreferenceViewHolder) holder;
        CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo catListBean = data.get(position);
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        viewHolder.getBinding().itemPreferenceType.setSelected(catListBean.isSelect);
        viewHolder.BindTo(catListBean.name);
    }

    public void setData(List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> getData() {
        return data;
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag == null) {
            return;
        }
        int position = (int) tag;
        CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo catListBean = data.get(position);
        if (catListBean.cateLevel == Constants.CATEGORY_LEVEL_TWO) {
            catListBean.isSelect = true;
            notifyItemChanged(position);
            return;
        }
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(position);
        }
    }

    static class ReadPreferenceViewHolder extends RecyclerView.ViewHolder {
        private ItemReadPreferenceBinding binding;

        public ReadPreferenceViewHolder(View itemView) {
            super(itemView);
            binding = (ItemReadPreferenceBinding) DataBindingUtil.bind(itemView);
        }

        public ItemReadPreferenceBinding getBinding() {
            return binding;
        }

        public void BindTo(String type) {
            binding.setType(type);
            binding.executePendingBindings();
        }
    }
}
