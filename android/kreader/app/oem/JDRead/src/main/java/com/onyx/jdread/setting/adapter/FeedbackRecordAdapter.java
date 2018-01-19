package com.onyx.jdread.setting.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ItemFeedbackRecordBinding;
import com.onyx.jdread.main.common.ResManager;

/**
 * Created by li on 2018/1/18.
 */

public class FeedbackRecordAdapter extends PageRecyclerView.PageAdapter {
    @Override
    public int getRowCount() {
        return ResManager.getInteger(R.integer.feedback_record_adapter_row);
    }

    @Override
    public int getColumnCount() {
        return ResManager.getInteger(R.integer.feedback_record_adapter_col);
    }

    @Override
    public int getDataCount() {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback_record_layout, parent, false);
        return new FeedbackRecordViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    static class FeedbackRecordViewHolder extends RecyclerView.ViewHolder {
        private ItemFeedbackRecordBinding bind;

        public FeedbackRecordViewHolder(View itemView) {
            super(itemView);
            bind = (ItemFeedbackRecordBinding) DataBindingUtil.bind(itemView);
        }

        public ItemFeedbackRecordBinding getBind() {
            return bind;
        }

        public void bindTo() {

        }
    }
}
