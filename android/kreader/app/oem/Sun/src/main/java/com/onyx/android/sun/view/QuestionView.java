package com.onyx.android.sun.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.onyx.android.sun.R;
import com.onyx.android.sun.cloud.bean.Question;
import com.onyx.android.sun.common.Constants;

import java.util.List;
import java.util.Map;

/**
 * Created by li on 2017/10/12.
 */

public class QuestionView extends LinearLayout {
    private Context context;
    private TextView questionTitle;
    private RadioGroup choiceGroup;
    private LinearLayout subjectiveGroup;

    public QuestionView(Context context) {
        this(context, null);
    }

    public QuestionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        View view = View.inflate(context, R.layout.question_view_layout, this);
        questionTitle = (TextView) view.findViewById(R.id.question_title);
        choiceGroup = (RadioGroup) view.findViewById(R.id.choice_item);
        subjectiveGroup = (LinearLayout) view.findViewById(R.id.subjective_item);
    }

    public void setQuestionData(Question questionData) {
        questionTitle.setText(questionData.id + "." + questionData.question);
        if (Constants.QUESTION_TYPE_CHOICE.equals(questionData.type)) {
            setType(R.id.choice_item);
            generateChoice(questionData.selection);
        } else if (Constants.QUESTION_TYPE_OBJECTIVE.equals(questionData.type)) {
            setType(R.id.subjective_item);
            generateSubject(questionData.selection);
        }
    }

    private void generateSubject(List<Map<String, String>> selection) {
        //TODO:
    }

    private void generateChoice(List<Map<String, String>> selection) {
        for (int i = 0; i < selection.size(); i++) {
            Map<String, String> detail = selection.get(i);
            String select = detail.get("key") + "." + detail.get("value");
            CompoundButton button = getCompoundButton(select, false);
            choiceGroup.addView(button);
        }
    }

    private CompoundButton getCompoundButton(String detail, boolean isMulti) {
        CompoundButton button = null;
        if (isMulti) {
            button = new CheckBox(context);
        } else {
            button = new RadioButton(context);
        }

        button.setText(detail);
        button.setGravity(Gravity.LEFT | Gravity.CENTER);
        button.setTextSize(20);
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //TODO:
            }
        });

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        button.setLayoutParams(params);
        return button;
    }

    private void setType(int id) {
        choiceGroup.setVisibility(R.id.choice_item == id ? VISIBLE : GONE);
        subjectiveGroup.setVisibility(R.id.subjective_item == id ? VISIBLE : GONE);
    }
}
