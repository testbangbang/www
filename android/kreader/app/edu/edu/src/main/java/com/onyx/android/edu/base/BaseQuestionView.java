package com.onyx.android.edu.base;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.edu.db.model.AtomicAnswer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ming on 16/6/29.
 */
public abstract class BaseQuestionView extends LinearLayout {

    protected TextView mQuesTitle;
    protected ImageView mQuesImage;
    protected Context mContext;
    protected boolean hasAnswer = false;
    protected boolean showAnswer = false;
    protected String rightAnswer;
    protected String problem;
    protected String questionAnalyze;
    protected String chooseAnswer;

    public BaseQuestionView(Context context, boolean showAnswer) {
        super(context);
        mContext = context;
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

    public void setHasAnswer(boolean hasAnswer) {
        this.hasAnswer = hasAnswer;
    }

    protected abstract int getInflateLayoutId();
    protected abstract void initView();
    public abstract boolean hasAnswers();
    public abstract boolean isRight();
    public abstract float getScore();
    public abstract void addAnalysisAnswerView();
}
