package com.onyx.jdread.reader.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ItemReaderSearchBinding;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.reader.menu.model.SearchResultBean;

import java.util.List;

/**
 * Created by li on 2018/1/13.
 */

public class SearchAdapter extends PageRecyclerView.PageAdapter implements View.OnClickListener {
    private List<SearchResultBean> data;

    @Override
    public int getRowCount() {
        return ResManager.getInteger(R.integer.search_reader_adapter_row);
    }

    @Override
    public int getColumnCount() {
        return ResManager.getInteger(R.integer.search_reader_adapter_col);
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reader_search_layout, parent, false);
        return new ReaderSearchViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ReaderSearchViewHolder viewHolder = (ReaderSearchViewHolder) holder;
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        viewHolder.bindTo(data.get(position));
    }

    public void setData(List<SearchResultBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag == null) {
            return;
        }
        int position = (int) tag;
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(position);
        }
    }

    static class ReaderSearchViewHolder extends RecyclerView.ViewHolder {
        private ItemReaderSearchBinding bind;

        public ReaderSearchViewHolder(View itemView) {
            super(itemView);
            bind = (ItemReaderSearchBinding) DataBindingUtil.bind(itemView);
        }

        public ItemReaderSearchBinding getBind() {
            return bind;
        }

        public void bindTo(SearchResultBean bean) {
            bind.setBean(bean);
            bind.executePendingBindings();
        }
    }
}
