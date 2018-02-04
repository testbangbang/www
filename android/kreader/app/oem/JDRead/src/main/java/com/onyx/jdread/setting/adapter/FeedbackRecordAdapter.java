package com.onyx.jdread.setting.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ItemFeedbackRecordBinding;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.setting.data.database.FeedbackRecord;
import com.onyx.jdread.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2018/1/18.
 */

public class FeedbackRecordAdapter extends PageRecyclerView.PageAdapter<FeedbackRecordAdapter.FeedbackRecordViewHolder> {

    private List<FeedbackRecord> feedbackRecordList = new ArrayList<>();

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
        return CollectionUtils.getSize(feedbackRecordList);
    }

    public FeedbackRecord getItem(int position) {
        return feedbackRecordList.get(position);
    }

    public void addDataList(List<FeedbackRecord> list, boolean clearBefore) {
        if (clearBefore) {
            feedbackRecordList.clear();
        }
        if (list != null) {
            feedbackRecordList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public FeedbackRecordViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback_record_layout, parent, false);
        return new FeedbackRecordViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(FeedbackRecordViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class FeedbackRecordViewHolder extends RecyclerView.ViewHolder {
        private ItemFeedbackRecordBinding binding;

        public FeedbackRecordViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public void bind(FeedbackRecord item) {
            binding.setFeedbackRecord(item);
            binding.timeDesc.setText(TimeUtils.DEFAULT_DATE_FORMAT.format(item.createdAt));
            binding.executePendingBindings();
        }
    }
}
