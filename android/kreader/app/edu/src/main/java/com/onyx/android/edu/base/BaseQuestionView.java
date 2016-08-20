package com.onyx.android.edu.base;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.edu.db.model.AtomicAnswer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 16/6/29.
 */
public abstract class BaseQuestionView extends LinearLayout {

    protected TextView mQuesTitle;
    protected ImageView mQuesImage;
    protected List<AtomicAnswer> mAnswers;
    protected Context mContext;
    protected boolean hasAnswer = false;

    protected boolean showAnswer = false;

    public BaseQuestionView(Context context, boolean showAnswer) {
        super(context);
        mContext = context;
        mAnswers = new ArrayList<>();
        this.showAnswer = showAnswer;
        View.inflate(context, getInflateLayoutId(),this);
        initView();
        if (showAnswer){
            addAnalysisAnswerView();
        }
    }

    public boolean isShowAnswer() {
        return showAnswer;
    }

    public void setShowAnswer(boolean showAnswer) {
        this.showAnswer = showAnswer;
    }

    public void setHasAnswer(boolean hasAnswer) {
        this.hasAnswer = hasAnswer;
    }

    protected abstract int getInflateLayoutId();
    protected abstract void initView();
    public abstract boolean hasAnswers();
    public abstract List<AtomicAnswer> getAnswers();
    public abstract boolean isRight(int index);
    public abstract float getScore(int index);
    public abstract void addAnalysisAnswerView();
}
