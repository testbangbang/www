package com.onyx.android.edu.view;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.base.BaseQuestionView;
import com.onyx.android.edu.db.model.AtomicAnswer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 16/6/29.
 * 问答题
 */
public class FillQuestionView extends BaseQuestionView {

    private LinearLayout mAnswerLayout;
    private List<FillQuestionItemView> mItemViews;
    private LinearLayout mMainFillView;

    public FillQuestionView(Context context) {
        super(context);
    }

    @Override
    protected int getInflateLayoutId() {
        return R.layout.view_fill_question;
    }

    @Override
    protected void initView() {
        mQuesTitle = (TextView)findViewById(R.id.question_title);
        mQuesImage = (ImageView) findViewById(R.id.question_image);
        mMainFillView = (LinearLayout)findViewById(R.id.main_fill_view);
        mQuesImage.setVisibility(GONE);

        mAnswerLayout = (LinearLayout) findViewById(R.id.answers);
    }


    @Override
    public boolean hasAnswers() {
        return true;
    }


    @Override
    public List<AtomicAnswer> getAnswers() {
        generateAnswers();
        return mAnswers;
    }

    @Override
    public boolean isRight(int index) {
        return false;
    }

    @Override
    public float getScore(int index) {
        return 0;
    }

    private void generateAnswers(){
        mAnswers.clear();
        for (FillQuestionItemView view : mItemViews) {
            AtomicAnswer answer = new AtomicAnswer();
            mAnswers.add(answer);
        }
    }

    private void addItemViews(int count){
        mItemViews = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            FillQuestionItemView itemView = new FillQuestionItemView(mContext);
            int position = i + 1;
            itemView.setIndex(position);
            itemView.setLayoutParams(generateLayoutParams());
            mItemViews.add(itemView);
            mAnswerLayout.addView(itemView);
        }
    }

    private LinearLayout.LayoutParams generateLayoutParams() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        int margin = 10;
        layoutParams.setMargins(margin, margin, margin, margin);
        layoutParams.weight = 1;
        layoutParams.gravity = Gravity.CENTER;
        return layoutParams;
    }

    @Override
    public void showAnswer(boolean showButton) {
        showAnswer = true;
        mAnswerLayout.setVisibility(GONE);
        AnalysisAnswerView analysisAnswerView = new AnalysisAnswerView(mContext);
        mMainFillView.addView(analysisAnswerView);
    }
}
