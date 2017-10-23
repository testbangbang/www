package com.onyx.android.sun.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.cloud.bean.Question;
import com.onyx.android.sun.cloud.bean.QuestionData;
import com.onyx.android.sun.databinding.ItemFillHomeworkBinding;
import com.onyx.android.sun.interfaces.OnCheckAnswerListener;
import com.onyx.android.sun.presenter.FillHomeworkPresenter;
import com.onyx.android.sun.view.PageRecyclerView;

import java.util.List;

/**
 * Created by li on 2017/10/13.
 */

public class FillHomeworkAdapter extends PageRecyclerView.PageAdapter implements OnCheckAnswerListener {
    private List<QuestionData> data;
    private String title;
    private FillHomeworkPresenter fillHomeworkPresenter = new FillHomeworkPresenter();

    @Override
    public int getRowCount() {
        return SunApplication.getInstance().getResources().getInteger(R.integer.fill_homework_row);
    }

    @Override
    public int getColumnCount() {
        return SunApplication.getInstance().getResources().getInteger(R.integer.fill_homework_col);
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
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        FillHomeworkViewHolder viewHolder = (FillHomeworkViewHolder) holder;
        ItemFillHomeworkBinding fillHomeworkBinding = viewHolder.getFillHomeworkBinding();
        final Question questions = data.get(position).exercise;
        fillHomeworkBinding.itemHomeworkQuestion.setQuestionData(questions, title);
        fillHomeworkBinding.itemHomeworkQuestion.setOnCheckAnswerListener(this);
        fillHomeworkBinding.executePendingBindings();
    }

    @Override
    public void onClick(View view) {

    }

    public void setData(List<QuestionData> data, String title) {
        this.data = data;
        this.title = title;
        notifyDataSetChanged();
    }

    @Override
    public void checkAnswerListener(Question question) {
        fillHomeworkPresenter.insertAnswer(question);
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
