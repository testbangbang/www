package com.onyx.android.edu.view;

import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.base.BaseQuestionView;
import com.onyx.android.edu.db.model.AtomicAnswer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ming on 16/6/29.
 * 主观题
 */
public class SubjectiveQuestionView extends BaseQuestionView {

    private LinearLayout mAnswerLayout;
    private List<SubjectiveQuestionItemView> mItemViews;
    private LinearLayout mMainFillView;
    private String problem;

    public SubjectiveQuestionView(Context context,
                              boolean showAnswer,
                              String answer,
                              String problem,
                              String questionAnalyze) {
        super(context, showAnswer);
        this.rightAnswer = answer;
        this.problem = problem;
        this.questionAnalyze = questionAnalyze;
        initData();
    }

    private void initData(){
        mQuesTitle.setText(Html.fromHtml(problem).toString());
        addItemViews(2);
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
    public boolean isRight() {
        return false;
    }

    @Override
    public float getScore() {
        return 0;
    }

    private void addItemViews(int count){
        mItemViews = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            SubjectiveQuestionItemView itemView = new SubjectiveQuestionItemView(mContext);
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
    public void addAnalysisAnswerView() {
        mAnswerLayout.setVisibility(GONE);
        AnalysisAnswerView analysisAnswerView = new AnalysisAnswerView(mContext);
        mMainFillView.addView(analysisAnswerView);
    }
}
