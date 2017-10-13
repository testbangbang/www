package com.onyx.android.sun.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.sun.R;
import com.onyx.android.sun.common.CommonNotices;

/**
 * Created by li on 2017/10/12.
 */

public class SubjectiveQuestionItemView extends LinearLayout implements View.OnClickListener {

    private View view;
    private TextView title;
    private TextView answer;

    public SubjectiveQuestionItemView(Context context) {
        this(context, null);
    }

    public SubjectiveQuestionItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        view = View.inflate(context, R.layout.subjective_question_item_layout, this);
        init();
    }

    private void init() {
        title = (TextView) view.findViewById(R.id.subjective_item_question_title);
        answer = (TextView) view.findViewById(R.id.subjective_item_answer);
        answer.setOnClickListener(this);
    }

    public void setTitle(String text) {
        title.setText(text);
    }

    public void setAnswer(String content) {
        answer.setText(content);
    }

    @Override
    public void onClick(View view) {
        CommonNotices.show("to note");
    }
}
