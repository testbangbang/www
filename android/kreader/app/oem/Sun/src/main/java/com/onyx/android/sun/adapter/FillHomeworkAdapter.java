package com.onyx.android.sun.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.cloud.bean.ExerciseBean;
import com.onyx.android.sun.cloud.bean.Question;
import com.onyx.android.sun.cloud.bean.QuestionData;
import com.onyx.android.sun.cloud.bean.QuestionViewBean;
import com.onyx.android.sun.databinding.ItemFillHomeworkBinding;
import com.onyx.android.sun.event.ParseAnswerEvent;
import com.onyx.android.sun.interfaces.OnCheckAnswerListener;
import com.onyx.android.sun.presenter.FillHomeworkPresenter;
import com.onyx.android.sun.view.PageRecyclerView;

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
    private List<QuestionViewBean> questionList = new ArrayList<>();
    private int currentParentId;
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

    public void setData(List<QuestionData> data, String title, int taskId) {
        this.title = title;
        this.taskId = taskId;
        if (questionList != null) {
            questionList.clear();
        }
        for (QuestionData questionData : data) {
            List<ExerciseBean> exercises = questionData.exercises;
            if (exercises != null && exercises.size() > 0) {
                for (int i = 0; i < exercises.size(); i++) {
                    ExerciseBean exercise = exercises.get(i);
                    List<Question> questions = exercise.exercises;
                    if (questions != null && questions.size() > 0) {
                        for (int j = 0; j < questions.size(); j++) {
                            Question question = questions.get(j);
                            QuestionViewBean exerciseBean = new QuestionViewBean();
                            exerciseBean.setShow(i == 0 && j == 0);
                            exerciseBean.setAllScore(questionData.allScore);
                            exerciseBean.setExeNumber(questionData.exeNumber);
                            exerciseBean.setShowType(questionData.showType);
                            exerciseBean.setParentId(exercise.id);
                            exerciseBean.setShowReaderComprehension(currentParentId != exercise.id);
                            currentParentId = exercise.id;
                            exerciseBean.setScene(exercise.scene);
                            exerciseBean.setId(question.id);
                            exerciseBean.setContent(question.content);
                            exerciseBean.setExerciseSelections(question.exerciseSelections);
                            exerciseBean.setUserAnswer(question.userAnswer);
                            this.questionList.add(exerciseBean);
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public List<QuestionViewBean> getQuestionList() {
        return questionList;
    }

    @Override
    public void checkAnswerListener(QuestionViewBean questionViewBean) {
        insertAnswer(taskId, questionViewBean);
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

    /*static class FillHomeworkTitleViewHolder extends RecyclerView.ViewHolder {
        private ItemFillHomeworkTitleBinding fillHomeworkTitleBinding;

        public FillHomeworkTitleViewHolder(View itemView) {
            super(itemView);
            fillHomeworkTitleBinding = (ItemFillHomeworkTitleBinding) DataBindingUtil.bind(itemView);
        }

        public ItemFillHomeworkTitleBinding getFillHomeworkTitleBinding() {
            return fillHomeworkTitleBinding;
        }
    }*/
}
