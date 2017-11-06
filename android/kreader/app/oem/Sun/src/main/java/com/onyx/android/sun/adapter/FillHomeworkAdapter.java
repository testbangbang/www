package com.onyx.android.sun.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
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
import com.onyx.android.sun.databinding.ItemFillHomeworkTitleBinding;
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

    /*@Override
    public int getItemViewType(int position) {
        if (questionList.get(questionList.size() == position ? position - 1 : position).getExerciseBean() == null) {
            return 0;
        } else {
            return 1;
        }
    }*/

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        //if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fill_homework_layout, parent, false);
            return new FillHomeworkViewHolder(view);
        /*} else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fill_homework_title_layout, parent, false);
            return new FillHomeworkTitleViewHolder(view);
        }*/
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        QuestionViewBean questionViewBean = questionList.get(position);
        /*if (holder instanceof FillHomeworkTitleViewHolder) {
            FillHomeworkTitleViewHolder titleViewHolder = (FillHomeworkTitleViewHolder) holder;
            ItemFillHomeworkTitleBinding fillHomeworkTitleBinding = titleViewHolder.getFillHomeworkTitleBinding();
            fillHomeworkTitleBinding.setAllScores(questionViewBean.getAllScore());
            fillHomeworkTitleBinding.setEachScore(questionViewBean.getAllScore() / questionViewBean.getExeNumber());
            fillHomeworkTitleBinding.setNumber(questionViewBean.getExeNumber());
            fillHomeworkTitleBinding.setType(questionViewBean.getShowType());
            fillHomeworkTitleBinding.executePendingBindings();
        } else {*/
            FillHomeworkViewHolder viewHolder = (FillHomeworkViewHolder) holder;
            ItemFillHomeworkBinding fillHomeworkBinding = viewHolder.getFillHomeworkBinding();
            fillHomeworkBinding.itemHomeworkQuestion.setQuestionData(questionViewBean, title);
            fillHomeworkBinding.itemHomeworkQuestion.setFinished(isFinished);
            fillHomeworkBinding.itemHomeworkQuestion.setOnCheckAnswerListener(this);
            viewHolder.itemView.setOnClickListener(this);
            viewHolder.itemView.setTag(position);
            fillHomeworkBinding.executePendingBindings();
        //}
    }

    @Override
    public void onClick(View view) {
        Object tag = view.getTag();
        if (tag == null) {
            return;
        }

        int position = (int) tag;
        /*Question question = data.get(position).exercise;
        if(isFinished) {
            EventBus.getDefault().post(new ParseAnswerEvent(question, title));
        }*/
    }

    public void setFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    public void setData(List<QuestionData> data, String title) {
        this.title = title;
        if (questionList != null) {
            questionList.clear();
        }
        for (QuestionData question : data) {
         /*   QuestionViewBean bean = new QuestionViewBean();
            bean.setAllScore(question.allScore);
            bean.setExeNumber(question.exeNumber);
            bean.setShowType(question.showType);
            questionList.add(bean);
*/
            List<ExerciseBean> exercises = question.exercises;
            if (exercises != null && exercises.size() > 0) {
                for (int i = 0; i < exercises.size(); i++) {
                    QuestionViewBean exerciseBean = new QuestionViewBean();
                    exerciseBean.setShow(i == 0);
                    exerciseBean.setAllScore(question.allScore);
                    exerciseBean.setExeNumber(question.exeNumber);
                    exerciseBean.setShowType(question.showType);
                    exerciseBean.setExerciseBean(exercises.get(i));
                    questionList.add(exerciseBean);
                }
            }
        }
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
