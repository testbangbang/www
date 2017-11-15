package com.onyx.android.plato.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.data.database.TaskAndAnswerEntity;
import com.onyx.android.plato.databinding.ItemHomeworkResultBinding;
import com.onyx.android.plato.event.UnansweredEvent;
import com.onyx.android.plato.utils.StringUtil;
import com.onyx.android.plato.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;

/**
 * Created by li on 2017/10/16.
 */

public class HomeworkResultAdapter extends PageRecyclerView.PageAdapter {
    private int row;
    private int col;
    private List<TaskAndAnswerEntity> data;

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
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_homework_result_layout, parent, false);
        return new HomeworkResultViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HomeworkResultViewHolder viewHolder = (HomeworkResultViewHolder) holder;
        ItemHomeworkResultBinding homeworkResultBinding = viewHolder.getHomeworkResultBinding();
        TaskAndAnswerEntity entity = data.get(position);
        homeworkResultBinding.setQuestionId(String.format(SunApplication.getInstance().getResources().getString(R.string.question_order), entity.questionId));
        homeworkResultBinding.setAnswer(entity.userAnswer);
        Bitmap bitmap = NoteDataProvider.loadThumbnail(SunApplication.getInstance(), entity.userAnswer);
        homeworkResultBinding.subjectiveResultAnswer.setVisibility(bitmap == null ? View.GONE : View.VISIBLE);
        if(bitmap != null) {
            homeworkResultBinding.subjectiveResultAnswer.setImageBitmap(bitmap);
        }
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
    }

    @Override
    public void onClick(View view) {
        Object tag = view.getTag();
        if (tag == null) {
            return;
        }

        int position = (int) tag;
        TaskAndAnswerEntity entity = data.get(position);
        if (StringUtil.isNullOrEmpty(entity.userAnswer)) {
            EventBus.getDefault().post(new UnansweredEvent());
        }
    }

    public void setData(List<TaskAndAnswerEntity> data) {
        this.data = data;
        Collections.sort(data);
        notifyDataSetChanged();
    }

    static class HomeworkResultViewHolder extends RecyclerView.ViewHolder {
        private ItemHomeworkResultBinding homeworkResultBinding;

        public HomeworkResultViewHolder(View itemView) {
            super(itemView);
            homeworkResultBinding = (ItemHomeworkResultBinding) DataBindingUtil.bind(itemView);
        }

        public ItemHomeworkResultBinding getHomeworkResultBinding() {
            return homeworkResultBinding;
        }
    }

    public void setRowOrCol(int row, int col) {
        this.row = row;
        this.col = col;
    }
}
