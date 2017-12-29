package com.onyx.jdread.shop.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.CommentItemBinding;
import com.onyx.jdread.shop.cloud.entity.jdbean.CommentEntity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/12.
 */

public class BookCommentsAdapter extends PageAdapter<PageRecyclerView.ViewHolder, CommentEntity, CommentEntity> {

    private EventBus eventBus;
    private int row = JDReadApplication.getInstance().getResources().getInteger(R.integer.book_shop_comment_row);
    private int col = JDReadApplication.getInstance().getResources().getInteger(R.integer.book_shop_comment_col);

    public BookCommentsAdapter(EventBus eventBus) {
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
        return new ModelViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_detail_comment, null));
    }

    @Override
    public void onPageBindViewHolder(PageRecyclerView.ViewHolder holder, int position) {
        final CommentEntity commentEntity = getItemVMList().get(position);
        ModelViewHolder viewHolder = (ModelViewHolder) holder;
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        viewHolder.bindTo(commentEntity);
    }

    @Override
    public void setRawData(List<CommentEntity> rawData, Context context) {
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

        }
    }

    static class ModelViewHolder extends PageRecyclerView.ViewHolder {

        private final CommentItemBinding bind;

        public ModelViewHolder(View view) {
            super(view);
            bind = DataBindingUtil.bind(view);
        }

        public CommentItemBinding getBind() {
            return bind;
        }

        public void bindTo(CommentEntity commentEntity) {
            bind.setCommentEntity(commentEntity);
            bind.executePendingBindings();
        }
    }
}