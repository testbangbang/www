package com.onyx.jdread.shop.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.AllCategoryNormalItemBinding;
import com.onyx.jdread.databinding.CategorySubjectModelItemBinding;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.event.CategoryItemClickEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/12.
 */

public class CategorySubjectAdapter extends PageAdapter<PageRecyclerView.ViewHolder, CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo, CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> {

    private static final int TYPE_MAINMODEL = 1;
    private static final int TYPE_ALL_CAT_NORMAL = 2;
    private boolean isAllCategory;
    private EventBus eventBus;
    private int row = JDReadApplication.getInstance().getResources().getInteger(R.integer.shop_category_recycle_view_row);
    private int col = JDReadApplication.getInstance().getResources().getInteger(R.integer.shop_category_recycle_view_col);
    private boolean canSelected = false;

    public CategorySubjectAdapter(EventBus eventBus, boolean isAllCategory) {
        this.eventBus = eventBus;
        this.isAllCategory = isAllCategory;
    }

    public void setCanSelected(boolean canSelected){
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
    public int getItemViewType(int position) {
        if (isAllCategory) {
            return TYPE_ALL_CAT_NORMAL;
        } else {
            return TYPE_MAINMODEL;
        }
    }

    @Override
    public PageRecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_MAINMODEL:
                return new ModelViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_subject_model, null));
            case TYPE_ALL_CAT_NORMAL:
                return new AllCategoryNormalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_category_normal, null));
            default:
                return new AllCategoryNormalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_category_normal, null));
        }
    }

    @Override
    public void onPageBindViewHolder(PageRecyclerView.ViewHolder holder, int position) {
        final CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo catListBean = getItemVMList().get(position);
        if (holder instanceof ModelViewHolder) {
            ModelViewHolder viewHolder = (ModelViewHolder) holder;
            viewHolder.itemView.setOnClickListener(this);
            viewHolder.itemView.setTag(position);
            viewHolder.bindTo(catListBean);
        }  else if (holder instanceof AllCategoryNormalViewHolder) {
            AllCategoryNormalViewHolder viewHolder = (AllCategoryNormalViewHolder) holder;
            viewHolder.itemView.setOnClickListener(this);
            viewHolder.itemView.setTag(position);
            viewHolder.getBind().categoryName.setSelected(catListBean.isSelect);
            viewHolder.bindTo(catListBean);
        }
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
        for (CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo catebean :levelTwoList) {
            catebean.isSelect = false;
        }
        CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo categoryBeanLevelTwo = levelTwoList.get(position);
        if (canSelected) {
            categoryBeanLevelTwo.isSelect = true;
            notifyItemChanged(position);
        }
    }

    static class ModelViewHolder extends PageRecyclerView.ViewHolder {

        private final CategorySubjectModelItemBinding bind;

        public ModelViewHolder(View view) {
            super(view);
            bind = DataBindingUtil.bind(view);
        }

        public CategorySubjectModelItemBinding getBind() {
            return bind;
        }

        public void bindTo(CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo catListBean) {
            bind.setCategoryBean(catListBean);
            bind.executePendingBindings();
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