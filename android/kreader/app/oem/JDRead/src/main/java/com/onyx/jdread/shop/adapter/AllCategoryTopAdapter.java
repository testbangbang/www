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
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean.CatListBean;
import com.onyx.jdread.shop.event.OnCategoryItemClickEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2018/1/2.
 */

public class AllCategoryTopAdapter extends PageAdapter<PageRecyclerView.ViewHolder, CatListBean, CatListBean> {

    private EventBus eventBus;
    private int row = JDReadApplication.getInstance().getResources().getInteger(R.integer.all_category_top_recycle_view_row);
    private int col = JDReadApplication.getInstance().getResources().getInteger(R.integer.all_category_recycle_view_col);

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
        final CatListBean catListBean = getItemVMList().get(position);
        AllCategoryImageViewHolder viewHolder = (AllCategoryImageViewHolder) holder;
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        viewHolder.bindTo(catListBean);
    }

    @Override
    public void setRawData(List<CatListBean> rawData, Context context) {
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
            eventBus.post(new OnCategoryItemClickEvent(getItemVMList().get(position)));
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

        public void bindTo(CatListBean catListBean) {
            bind.setCategoryListBean(catListBean);
            bind.executePendingBindings();
        }
    }
}