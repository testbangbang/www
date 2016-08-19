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
public class ChoiceQuestionView extends BaseQuestionView {

    private RadioGroup mOptions;
    private LinearLayout mMainSelectView;
    private List<String> optionTexts;
    private String problem;

    public ChoiceQuestionView(Context context, List<String> optionTexts, String problem, boolean showAnswer) {
        super(context,showAnswer);
        this.optionTexts = optionTexts;
        this.problem = problem;
        initData();
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

    private void initData(){
        mQuesTitle.setText(problem);
        for (int i = 0; i < optionTexts.size(); i++) {
            CompoundButton button = getCompoundButton(optionTexts.get(i),false);
            mOptions.addView(button);
        }
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
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    setHasAnswer(true);
                }
            }
        });
        return button;
    }

    @Override
    public boolean hasAnswers() {
        return hasAnswer;
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
    public void addAnalysisAnswerView() {
        AnalysisAnswerView analysisAnswerView = new AnalysisAnswerView(mContext);
        mMainSelectView.addView(analysisAnswerView);
    }

}
