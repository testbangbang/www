package com.onyx.android.plato.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.cloud.bean.ExerciseBean;
import com.onyx.android.plato.cloud.bean.ExerciseMessageBean;
import com.onyx.android.plato.cloud.bean.Question;
import com.onyx.android.plato.cloud.bean.QuestionData;
import com.onyx.android.plato.cloud.bean.QuestionViewBean;
import com.onyx.android.plato.data.database.TaskAndAnswerEntity;
import com.onyx.android.plato.databinding.ItemFillHomeworkBinding;
import com.onyx.android.plato.event.ParseAnswerEvent;
import com.onyx.android.plato.interfaces.OnCheckAnswerListener;
import com.onyx.android.plato.presenter.FillHomeworkPresenter;
import com.onyx.android.plato.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2017/10/13.
 */

public class FillHomeworkAdapter extends PageRecyclerView.PageAdapter implements OnCheckAnswerListener {
    private String title;
    private FillHomeworkPresenter fillHomeworkPresenter = new FillHomeworkPresenter();
    private boolean isFinished;
    private List<QuestionViewBean> questionList;
    private int taskId;

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
        return questionList == null ? 0 : questionList.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fill_homework_layout, parent, false);
        return new FillHomeworkViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        QuestionViewBean questionViewBean = questionList.get(position);
        FillHomeworkViewHolder viewHolder = (FillHomeworkViewHolder) holder;
        ItemFillHomeworkBinding fillHomeworkBinding = viewHolder.getFillHomeworkBinding();
        fillHomeworkBinding.itemHomeworkQuestion.setAnalyze(isFinished);
        fillHomeworkBinding.itemHomeworkQuestion.setQuestionData(questionViewBean, title);
        fillHomeworkBinding.itemHomeworkQuestion.setFinished(isFinished);
        fillHomeworkBinding.itemHomeworkQuestion.setOnCheckAnswerListener(this);
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        fillHomeworkBinding.executePendingBindings();
    }

    @Override
    public void onClick(View view) {
        Object tag = view.getTag();
        if (tag == null) {
            return;
        }

        int position = (int) tag;
        QuestionViewBean questionViewBean = questionList.get(position);
        if (isFinished) {
            EventBus.getDefault().post(new ParseAnswerEvent(questionViewBean, title));
        }
    }

    public void setFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    public void setData(List<QuestionViewBean> questionList, String title, int taskId) {
        this.title = title;
        this.taskId = taskId;
        this.questionList = questionList;
        notifyDataSetChanged();
    }

    public List<QuestionViewBean> getQuestionList() {
        return questionList;
    }

    @Override
    public void checkAnswerListener(QuestionViewBean questionViewBean) {
        insertAnswer(taskId, questionViewBean);
    }

    @Override
    public void deleteOrFavoriteQuestion(int taskId, int questionId) {
        //TODO:fake student id
        fillHomeworkPresenter.deleteOrFavorite(taskId, questionId, 108);
    }

    public void insertAnswer(int taskId, QuestionViewBean questionViewBean) {
        fillHomeworkPresenter.insertAnswer(taskId, questionViewBean);
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
