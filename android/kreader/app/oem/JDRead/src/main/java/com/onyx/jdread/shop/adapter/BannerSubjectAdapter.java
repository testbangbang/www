package com.onyx.jdread.shop.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.BannerSubjectModelItemBinding;
import com.onyx.jdread.shop.cloud.entity.jdbean.ResultBookBean;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/12.
 */

public class BannerSubjectAdapter extends PageAdapter<PageRecyclerView.ViewHolder, ResultBookBean, ResultBookBean> {

    private int row = JDReadApplication.getInstance().getResources().getInteger(R.integer.store_banner_recycle_view_row);
    private int col = JDReadApplication.getInstance().getResources().getInteger(R.integer.store_banner_recycle_view_col);

    public BannerSubjectAdapter() {

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
        return new ModelViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_subject_model_item, null));
    }

    @Override
    public void onPageBindViewHolder(PageRecyclerView.ViewHolder holder, int position) {
        final ResultBookBean bookBean = getItemVMList().get(position);
        ModelViewHolder viewHolder = (ModelViewHolder) holder;
        viewHolder.bindTo(bookBean);
    }

    @Override
    public void setRawData(List<ResultBookBean> rawData, Context context) {
        super.setRawData(rawData, context);
        setItemVMList(rawData);
        notifyDataSetChanged();
    }

    static class ModelViewHolder extends PageRecyclerView.ViewHolder {

        private final BannerSubjectModelItemBinding bind;

        public ModelViewHolder(View view) {
            super(view);
            bind = DataBindingUtil.bind(view);
        }

        public BannerSubjectModelItemBinding getBind() {
            return bind;
        }

        public void bindTo(ResultBookBean bookBean) {
            bind.setBookBean(bookBean);
            bind.executePendingBindings();
        }
    }
}