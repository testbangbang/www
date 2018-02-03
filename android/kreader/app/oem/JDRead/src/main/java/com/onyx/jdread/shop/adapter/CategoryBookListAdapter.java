package com.onyx.jdread.shop.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.AllCategoryNormalItemBinding;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.event.CategoryItemClickEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/12.
 */

public class CategoryBookListAdapter extends PageAdapter<PageRecyclerView.ViewHolder, CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo, CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> {

    protected EventBus eventBus;
    private int row;
    private int col;
    private boolean canSelected = false;

    public CategoryBookListAdapter(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void setCanSelected(boolean canSelected) {
        this.canSelected = canSelected;
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
    public PageRecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new AllCategoryNormalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_category_normal, null));
    }

    @Override
    public void onPageBindViewHolder(PageRecyclerView.ViewHolder holder, int position) {
        final CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo catListBean = getItemVMList().get(position);
        AllCategoryNormalViewHolder viewHolder = (AllCategoryNormalViewHolder) holder;
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        viewHolder.getBind().categoryName.setSelected(catListBean.isSelect);
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
        List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> levelTwoList = getItemVMList();
        if (eventBus != null && levelTwoList != null) {
            changeItemState(position, levelTwoList);
            eventBus.post(new CategoryItemClickEvent(levelTwoList.get(position)));
        }
    }

    private void changeItemState(int position, List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> levelTwoList) {
        for (CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo catebean : levelTwoList) {
            catebean.isSelect = false;
        }
        CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo categoryBeanLevelTwo = levelTwoList.get(position);
        if (canSelected) {
            categoryBeanLevelTwo.isSelect = true;
            notifyItemChanged(position);
        }
    }

    static class AllCategoryNormalViewHolder extends PageRecyclerView.ViewHolder {

        private final AllCategoryNormalItemBinding bind;

        public AllCategoryNormalViewHolder(View view) {
            super(view);
            bind = DataBindingUtil.bind(view);
        }

        public AllCategoryNormalItemBinding getBind() {
            return bind;
        }

        public void bindTo(CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo catListBean) {
            bind.setCategoryBean(catListBean);
            bind.executePendingBindings();
        }
    }
}