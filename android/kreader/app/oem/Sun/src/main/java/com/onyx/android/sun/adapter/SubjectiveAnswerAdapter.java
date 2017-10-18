package com.onyx.android.sun.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.onyx.android.sun.R;
import com.onyx.android.sun.common.CommonNotices;
import com.onyx.android.sun.databinding.ItemSubjectiveAnswerBinding;
import com.onyx.android.sun.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2017/10/18.
 */

public class SubjectiveAnswerAdapter extends PageRecyclerView.PageAdapter {
    private List<Integer> data;

    public SubjectiveAnswerAdapter() {
        data = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            data.add(R.drawable.ic_launcher);
        }
    }
    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subjective_answer_layout, parent, false);
        return new SubjectiveAnswerViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SubjectiveAnswerViewHolder viewHolder = (SubjectiveAnswerViewHolder) holder;
        ItemSubjectiveAnswerBinding dataBinding = viewHolder.getDataBinding();
        dataBinding.itemSubjectiveImage.setImageResource(R.drawable.ic_launcher);
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        dataBinding.executePendingBindings();
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if(tag == null) {
            return;
        }
        CommonNotices.show("item subjective answer" + tag);
    }

    static class SubjectiveAnswerViewHolder extends RecyclerView.ViewHolder {
        private ItemSubjectiveAnswerBinding dataBinding;

        public SubjectiveAnswerViewHolder(View itemView) {
            super(itemView);
            dataBinding = (ItemSubjectiveAnswerBinding) DataBindingUtil.bind(itemView);
        }

        public ItemSubjectiveAnswerBinding getDataBinding() {
            return dataBinding;
        }
    }
}
