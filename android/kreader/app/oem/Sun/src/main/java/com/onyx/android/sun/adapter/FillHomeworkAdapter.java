package com.onyx.android.sun.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.cloud.bean.QuestionData;
import com.onyx.android.sun.databinding.ItemFillHomeworkBinding;
import com.onyx.android.sun.view.PageRecyclerView;

import java.util.List;

/**
 * Created by li on 2017/10/13.
 */

public class FillHomeworkAdapter extends PageRecyclerView.PageAdapter {
    private List<QuestionData> data;

    @Override
    public int getRowCount() {
        return SunApplication.getInstence().getResources().getInteger(R.integer.fill_homework_row);
    }

    @Override
    public int getColumnCount() {
        return SunApplication.getInstence().getResources().getInteger(R.integer.fill_homework_col);
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fill_homework_layout, parent, false);
        return new FillHomeworkViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FillHomeworkViewHolder viewHolder = (FillHomeworkViewHolder) holder;
        ItemFillHomeworkBinding fillHomeworkBinding = viewHolder.getFillHomeworkBinding();
        fillHomeworkBinding.itemHomeworkQuestion.setQuestionData(data.get(position).exercise);
        fillHomeworkBinding.executePendingBindings();
    }

    @Override
    public void onClick(View view) {

    }

    public void setData(List<QuestionData> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    static class FillHomeworkViewHolder extends RecyclerView.ViewHolder {
        private ItemFillHomeworkBinding fillHomeworkBinding;

        public FillHomeworkViewHolder(View itemView) {
            super(itemView);
            fillHomeworkBinding = (ItemFillHomeworkBinding) DataBindingUtil.bind(itemView);
        }

        public ItemFillHomeworkBinding getFillHomeworkBinding() {
            return fillHomeworkBinding;
        }
    }
}
