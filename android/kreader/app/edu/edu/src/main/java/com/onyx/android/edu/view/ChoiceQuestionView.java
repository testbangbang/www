package com.onyx.android.edu.view;

import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.base.BaseQuestionView;

import java.util.Map;


/**
 * Created by ming on 16/6/24.
 * 选择题View
 */
public class ChoiceQuestionView extends BaseQuestionView {

    private RadioGroup mOptions;
    private LinearLayout mMainSelectView;
    private Map<String, String> optionMap;

    public ChoiceQuestionView(Context context,
                              boolean showAnswer,
                              Map<String, String> optionMap,
                              String answer,
                              String problem,
                              String questionAnalyze) {
        super(context, showAnswer);
        this.optionMap = optionMap;
        this.rightAnswer = answer;
        this.problem = problem;
        this.questionAnalyze = questionAnalyze;
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
        mQuesTitle.setText(Html.fromHtml(problem).toString());
        for (String key : optionMap.keySet()) {
            String value = optionMap.get(key);
            String text = Html.fromHtml(key).toString() + ": " + Html.fromHtml(value).toString();
            CompoundButton button = getCompoundButton(text,false);
            button.setTag(key);
            mOptions.addView(button);
        }
    }

    private CompoundButton getCompoundButton(String text, boolean IsMultiSelect) {
        CompoundButton button;
        if (IsMultiSelect){
            button = new CheckBox(mContext);
        }else {
            button = new RadioButton(mContext);
        }
        button.setText(text);
        button.setGravity(Gravity.LEFT | Gravity.CENTER);
        button.setTextSize(25);
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    setHasAnswer(true);
                    chooseAnswer = (String) buttonView.getTag();
                }
            }
        });
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0,10,0,10);
        button.setLayoutParams(lp);
        return button;
    }

    @Override
    public boolean hasAnswers() {
        return hasAnswer;
    }


    @Override
    public boolean isRight() {
        return chooseAnswer.equals(rightAnswer);
    }

    @Override
    public float getScore() {
        return 0;
    }

    @Override
    public void addAnalysisAnswerView() {
        AnalysisAnswerView analysisAnswerView = new AnalysisAnswerView(mContext);
        mMainSelectView.addView(analysisAnswerView);
    }

}
