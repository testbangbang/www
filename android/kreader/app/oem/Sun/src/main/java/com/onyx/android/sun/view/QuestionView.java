package com.onyx.android.sun.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.cloud.bean.Question;
import com.onyx.android.sun.common.CommonNotices;
import com.onyx.android.sun.common.Constants;
import com.onyx.android.sun.common.ManagerActivityUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by li on 2017/10/12.
 */

public class QuestionView extends LinearLayout implements View.OnClickListener {
    private Context context;
    private TextView questionTitle;
    private RadioGroup choiceGroup;
    private LinearLayout subjectiveGroup;
    private Question questionData;
    private ImageView subjectiveImage;
    private String title;

    public QuestionView(Context context) {
        this(context, null);
    }

    public QuestionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
        initListener();
    }

    private void initListener() {
        subjectiveGroup.setOnClickListener(this);
    }

    private void init() {
        View view = View.inflate(context, R.layout.question_view_layout, this);
        questionTitle = (TextView) view.findViewById(R.id.question_title);
        choiceGroup = (RadioGroup) view.findViewById(R.id.choice_item);
        subjectiveGroup = (LinearLayout) view.findViewById(R.id.subjective_item);
        subjectiveImage = (ImageView) view.findViewById(R.id.subjective_image);

    }

    public void setQuestionData(Question questionData, String title) {
        this.questionData = questionData;
        this.title = title;
        questionTitle.setText(questionData.id + "." + questionData.question);
        if (Constants.QUESTION_TYPE_CHOICE.equals(questionData.type)) {
            setVisibleType(R.id.choice_item);
            generateChoice(questionData.selection);
        } else if (Constants.QUESTION_TYPE_OBJECTIVE.equals(questionData.type)) {
            setVisibleType(R.id.subjective_item);
            generateSubject(questionData.selection);
        }
    }

    private void generateSubject(List<Map<String, String>> selection) {
        //TODO:
    }

    private void generateChoice(List<Map<String, String>> selection) {
        if (choiceGroup.getChildCount() > 0) {
            choiceGroup.removeAllViews();
        }
        for (int i = 0; i < selection.size(); i++) {
            Map<String, String> detail = selection.get(i);
            String select = detail.get("key") + "." + detail.get("value");
            CompoundButton button = getCompoundButton(select, false);
            choiceGroup.addView(button);
        }
    }

    private CompoundButton getCompoundButton(final String detail, boolean isMulti) {
        CompoundButton button = null;
        if (isMulti) {
            button = new CheckBox(context);
        } else {
            button = new RadioButton(context);
        }

        button.setText(detail);
        button.setGravity(Gravity.LEFT | Gravity.CENTER);
        button.setTextSize(20);
        final String key = detail.substring(0, 1);
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    questionData.userAnswer = key;
                }
            }
        });
        if (key.equals(questionData.userAnswer)) {
            button.setChecked(true);
        }
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        button.setLayoutParams(params);
        return button;
    }

    private void setVisibleType(int id) {
        choiceGroup.setVisibility(R.id.choice_item == id ? VISIBLE : GONE);
        subjectiveGroup.setVisibility(R.id.subjective_item == id ? VISIBLE : GONE);
    }

    @Override
    public void onClick(View v) {
        ManagerActivityUtils.startScribbleActivity(SunApplication.getInstence(), questionData.id + "", title, questionData.question);
    }
}
