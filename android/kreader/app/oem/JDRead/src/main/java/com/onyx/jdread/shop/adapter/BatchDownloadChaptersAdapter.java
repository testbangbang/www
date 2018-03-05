package com.onyx.jdread.shop.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.BatchDownloadItemBinding;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.shop.cloud.entity.jdbean.BatchDownloadResultBean;
import com.onyx.jdread.shop.event.ChapterGroupItemClickEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/12.
 */

public class BatchDownloadChaptersAdapter extends PageAdapter<PageRecyclerView.ViewHolder, BatchDownloadResultBean.DataBean.ListBean, BatchDownloadResultBean.DataBean.ListBean> {

    private EventBus eventBus;
    private int row = ResManager.getInteger(R.integer.batch_download_recycle_view_row);
    private int col = ResManager.getInteger(R.integer.batch_download_recycle_view_col);

    public BatchDownloadChaptersAdapter(EventBus eventBus) {
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
    public PageRecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new ModelViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_batch_download, null));
    }

    @Override
    public void onPageBindViewHolder(PageRecyclerView.ViewHolder holder, int position) {
        final BatchDownloadResultBean.DataBean.ListBean bookBean = getItemVMList().get(position);
        ModelViewHolder viewHolder = (ModelViewHolder) holder;
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        viewHolder.bindTo(bookBean);
    }

    @Override
    public void setRawData(List<BatchDownloadResultBean.DataBean.ListBean> rawData, Context context) {
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
            eventBus.post(new ChapterGroupItemClickEvent(getItemVMList().get(position)));
        }
    }

    static class ModelViewHolder extends PageRecyclerView.ViewHolder {

        private final BatchDownloadItemBinding bind;

        public ModelViewHolder(View view) {
            super(view);
            bind = DataBindingUtil.bind(view);
        }

        public BatchDownloadItemBinding getBind() {
            return bind;
        }

        public void bindTo(BatchDownloadResultBean.DataBean.ListBean bookBean) {
            bind.setListBean(bookBean);
            bind.executePendingBindings();
        }
    }
}