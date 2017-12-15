package com.onyx.jdread.shop.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.CategorySubjectModelItemBinding;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean.CatListBean;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/12.
 */

public class CategorySubjectAdapter extends PageAdapter<PageRecyclerView.ViewHolder, CatListBean, CatListBean> {

    private int row = JDReadApplication.getInstance().getResources().getInteger(R.integer.store_category_recycle_view_row);
    private int col = JDReadApplication.getInstance().getResources().getInteger(R.integer.store_category_recycle_view_col);

    public CategorySubjectAdapter() {

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
        return new ModelViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_subject_model_item, null));
    }

    @Override
    public void onPageBindViewHolder(PageRecyclerView.ViewHolder holder, int position) {
        final CatListBean catListBean = getItemVMList().get(position);
        ModelViewHolder viewHolder = (ModelViewHolder) holder;
        viewHolder.bindTo(catListBean);
    }

    @Override
    public void setRawData(List<CatListBean> rawData, Context context) {
        super.setRawData(rawData, context);
        setItemVMList(rawData);
        notifyDataSetChanged();
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

        public void bindTo(CatListBean catListBean) {
            bind.setCategoryListBean(catListBean);
            bind.executePendingBindings();
        }
    }
}