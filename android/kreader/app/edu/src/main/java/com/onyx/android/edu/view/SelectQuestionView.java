package com.onyx.android.edu.view;

import android.content.Context;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.base.BaseQuestionView;
import com.onyx.android.edu.db.model.AtomicAnswer;

import java.util.List;


/**
 * Created by ming on 16/6/24.
 * 选择题View
 */
public class SelectQuestionView extends BaseQuestionView {

    private RadioGroup mOptions;
    private LinearLayout mMainSelectView;

    public SelectQuestionView(Context context) {
        super(context);
    }

    @Override
    protected int getInflateLayoutId() {
        return R.layout.view_select_question;
    }

    @Override
    protected void initView(){
        mQuesTitle = (TextView)findViewById(R.id.question_title);
        mQuesImage = (ImageView) findViewById(R.id.question_image);
        mOptions = (RadioGroup)findViewById(R.id.option);
        mMainSelectView = (LinearLayout)findViewById(R.id.main_select_view);
        mQuesImage.setVisibility(GONE);
    }

    private void addAnswer(String text){
        AtomicAnswer answer = new AtomicAnswer();
        mAnswers.add(answer);
    }

    private void removeAnswer(String text){
    }

    private CompoundButton getCompoundButton(String text,boolean IsMultiSelect) {
        CompoundButton button;
        if (IsMultiSelect){
            button = new CheckBox(mContext);
        }else {
            button = new RadioButton(mContext);
        }
        button.setText(text);
        button.setGravity(Gravity.LEFT | Gravity.CENTER);
        button.setTextSize(20);
        return button;
    }

    @Override
    public boolean hasAnswers() {
        if (mAnswers.size() < 1){
            return false;
        }
        return true;
    }

    @Override
    public List<AtomicAnswer> getAnswers() {
        return null;
    }


    @Override
    public boolean isRight(int index) {
        return false;
    }

    @Override
    public float getScore(int index) {
        return 0;
    }

    @Override
    public void showAnswer(boolean showButton) {
        showAnswer = true;
        AnalysisAnswerView analysisAnswerView = new AnalysisAnswerView(mContext);
        mMainSelectView.addView(analysisAnswerView);
    }
}
