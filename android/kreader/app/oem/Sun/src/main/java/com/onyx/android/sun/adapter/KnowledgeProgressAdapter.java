package com.onyx.android.sun.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.cloud.bean.KnowledgeProgressResult;
import com.onyx.android.sun.databinding.KnowledgeProgressBinding;
import com.onyx.android.sun.view.PageRecyclerView;

import java.util.List;

/**
 * Created by jackdeng on 2017/11/4.
 */

public class KnowledgeProgressAdapter extends PageRecyclerView.PageAdapter {
    private int current = 0;
    private float knowledgeProgressMaxValues = 1.0f;
    private List<KnowledgeProgressResult.DataBean> knowledgeProgressData;
    private int knowledgeProgressColumnCount = SunApplication.getInstance().getResources().getInteger(R.integer.knowledge_progress_adapter_col);
    private int knowledgeProgressRowCount = SunApplication.getInstance().getResources().getInteger(R.integer.knowledge_progress_adapter_row);

    public KnowledgeProgressAdapter() {

    }

    public KnowledgeProgressAdapter(int knowledgeProgressColumnCount, int knowledgeProgressRowCount) {
        this.knowledgeProgressColumnCount = knowledgeProgressColumnCount;
        this.knowledgeProgressRowCount = knowledgeProgressRowCount;
    }

    @Override
    public int getRowCount() {
        return knowledgeProgressRowCount;
    }

    @Override
    public int getColumnCount() {
        return knowledgeProgressColumnCount;
    }

    @Override
    public int getDataCount() {
        return knowledgeProgressData == null ? 0 : knowledgeProgressData.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_knowledge_progress_layout, parent, false);
        KnowledgeProgressViewHolder knowledgeProgressViewHolder = new KnowledgeProgressViewHolder(view);
        return knowledgeProgressViewHolder;
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        KnowledgeProgressViewHolder knowledgeProgressViewHolder = (KnowledgeProgressViewHolder) holder;
        KnowledgeProgressBinding binding = knowledgeProgressViewHolder.getBinding();
        KnowledgeProgressResult.DataBean dataBean = knowledgeProgressData.get(position);
        binding.setKnowledgePointName(dataBean.KN);
        binding.itemKnowledgeProgress.setProgress(dataBean.process);
        binding.itemKnowledgeProgress.setMax(knowledgeProgressMaxValues);
        knowledgeProgressViewHolder.itemView.setOnClickListener(this);
        knowledgeProgressViewHolder.itemView.setTag(position);
        binding.executePendingBindings();
    }

    @Override
    public void onClick(View view) {
        Object tag = view.getTag();
        if (tag == null) {
            return;
        }
        int postion = (int) tag;
    }

    public void setData(List<KnowledgeProgressResult.DataBean> knowledgeProgressData) {
        this.knowledgeProgressData = knowledgeProgressData;
        notifyDataSetChanged();
    }

    static class KnowledgeProgressViewHolder extends RecyclerView.ViewHolder {
        private KnowledgeProgressBinding knowledgeProgressBinding;

        public KnowledgeProgressViewHolder(View itemView) {
            super(itemView);
            knowledgeProgressBinding = (KnowledgeProgressBinding) DataBindingUtil.bind(itemView);
        }

        public KnowledgeProgressBinding getBinding() {
            return knowledgeProgressBinding;
        }
    }
}
