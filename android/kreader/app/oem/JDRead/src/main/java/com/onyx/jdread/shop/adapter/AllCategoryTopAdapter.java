package com.onyx.jdread.shop.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.AllCategoryImageItemBinding;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.event.CategoryItemClickEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2018/1/2.
 */

public class AllCategoryTopAdapter extends PageAdapter<PageRecyclerView.ViewHolder, CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo, CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> {

    private EventBus eventBus;
    private int row = ResManager.getInteger(R.integer.all_category_top_recycle_view_row);
    private int col = ResManager.getInteger(R.integer.all_category_top_recycle_view_col);

    public AllCategoryTopAdapter(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void setRowAndCol(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public int getRowCount() {
        return row;
    }

    @Override
    public int getColumnCount() {
        return col;
    }

    @Override
    public int getDataCount() {
        return getItemVMList().size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public PageRecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new AllCategoryImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_category_image, null));
    }

    @Override
    public void onPageBindViewHolder(PageRecyclerView.ViewHolder holder, int position) {
        final CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo catListBean = getItemVMList().get(position);
        AllCategoryImageViewHolder viewHolder = (AllCategoryImageViewHolder) holder;
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        viewHolder.bindTo(catListBean);
    }

    @Override
    public void setRawData(List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> rawData, Context context) {
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
        if (eventBus != null && getItemVMList() != null) {
            eventBus.post(new CategoryItemClickEvent(getItemVMList().get(position), position));
        }
    }

    static class AllCategoryImageViewHolder extends PageRecyclerView.ViewHolder {

        private final AllCategoryImageItemBinding bind;

        public AllCategoryImageViewHolder(View view) {
            super(view);
            bind = DataBindingUtil.bind(view);
        }

        public AllCategoryImageItemBinding getBind() {
            return bind;
        }

        public void bindTo(CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo catListBean) {
            bind.setCategoryBean(catListBean);
            bind.executePendingBindings();
        }
    }
}