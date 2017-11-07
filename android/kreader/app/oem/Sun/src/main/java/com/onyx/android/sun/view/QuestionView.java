package com.onyx.android.sun.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.cloud.bean.ExerciseBean;
import com.onyx.android.sun.cloud.bean.ExerciseSelectionBean;
import com.onyx.android.sun.cloud.bean.Question;
import com.onyx.android.sun.cloud.bean.QuestionViewBean;
import com.onyx.android.sun.common.CommonNotices;
import com.onyx.android.sun.common.Constants;
import com.onyx.android.sun.common.ManagerActivityUtils;
import com.onyx.android.sun.event.ParseAnswerEvent;
import com.onyx.android.sun.interfaces.OnCheckAnswerListener;
import com.onyx.android.sun.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;

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
    private ImageView subjectiveImage;
    private String title;
    private OnCheckAnswerListener listener;
    private LinearLayout analyzeLayout;
    private TextView knowledgePoint;
    private TextView cognitiveLevel;
    private TextView correctRate;
    private FrameLayout correctState;
    private LinearLayout questionCorrect;
    private ImageView correctFavourite;
    private ImageView correctMistake;
    private boolean showAnalyze;
    private boolean isFinished;
    private QuestionViewBean questionViewBean;
    private LinearLayout choiceTitle;

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
        correctFavourite.setOnClickListener(this);
    }

    private void init() {
        View view = View.inflate(context, R.layout.question_view_layout, this);
        questionTitle = (TextView) view.findViewById(R.id.question_title);
        choiceGroup = (RadioGroup) view.findViewById(R.id.choice_radio_group);
        subjectiveGroup = (LinearLayout) view.findViewById(R.id.subjective_item);
        subjectiveImage = (ImageView) view.findViewById(R.id.subjective_image);
        choiceTitle = (LinearLayout) view.findViewById(R.id.choice_title);

        analyzeLayout = (LinearLayout) view.findViewById(R.id.question_analyze_layout);
        knowledgePoint = (TextView) view.findViewById(R.id.knowledge_point);
        cognitiveLevel = (TextView) view.findViewById(R.id.cognitive_level);
        correctRate = (TextView) view.findViewById(R.id.correct_rate);

        correctState = (FrameLayout) view.findViewById(R.id.correct_state);
        questionCorrect = (LinearLayout) view.findViewById(R.id.question_correct);
        correctFavourite = (ImageView) view.findViewById(R.id.correct_favourite);
        correctMistake = (ImageView) view.findViewById(R.id.question_mistake);
    }

    public void setVisibleAnalyze(boolean showAnalyze) {
        this.showAnalyze = showAnalyze;
        analyzeLayout.setVisibility(showAnalyze ? VISIBLE : GONE);
        correctState.setVisibility(showAnalyze ? VISIBLE : GONE);
    }

    public void setQuestionData(QuestionViewBean questionViewBean, String title) {
        this.questionViewBean = questionViewBean;
        this.title = title;
        ExerciseBean exerciseBean = questionViewBean.getExerciseBean();
        List<Question> exercises = exerciseBean.exercises;
        if (!StringUtil.isNullOrEmpty(exerciseBean.scene)) {
            Spanned spanned = Html.fromHtml(exerciseBean.id + "." + exerciseBean.scene);
            questionTitle.setText(questionViewBean.isShow() ? questionViewBean.getShowType() + "\n\n" + spanned : spanned);
        }
        setVisibleType(R.id.choice_radio_group);
        generateChoice(exercises);
    }

    public void setFinished(boolean isFinished) {
        this.isFinished = isFinished;
        if (choiceGroup.getChildCount() > 0) {
            for (int i = 0; i < choiceGroup.getChildCount(); i++) {
                View child = choiceGroup.getChildAt(i);
                child.setEnabled(!isFinished);
            }
        }
    }

    private void generateChoice(List<Question> questions) {
        if (choiceGroup.getChildCount() > 0) {
            choiceGroup.removeAllViews();
        }

        if (choiceTitle.getChildCount() > 0) {
            choiceTitle.removeAllViews();
        }
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            TextView problem = new TextView(context);
            problem.setTextSize(20);
            Spanned spanned = Html.fromHtml(question.content);
            String problemTitle = question.id + "." + spanned;
            problem.setText(StringUtil.isNullOrEmpty(questionViewBean.getExerciseBean().scene) &&
                    questionViewBean.isShow() ? questionViewBean.getShowType() + "\n\n" + problemTitle : problemTitle);
            choiceTitle.addView(problem);
            List<ExerciseSelectionBean> exerciseSelections = question.exerciseSelections;
            if (exerciseSelections == null || exerciseSelections.size() == 0) {
                setVisibleType(R.id.subjective_item);
                if (!StringUtil.isNullOrEmpty(question.userAnswer)) {
                    Bitmap bitmap = NoteDataProvider.loadThumbnail(SunApplication.getInstance(), question.userAnswer);
                    subjectiveImage.setImageBitmap(bitmap);
                }
                return;
            }

            for (ExerciseSelectionBean selectionBean : exerciseSelections) {
                CompoundButton button = getCompoundButton(question, selectionBean, false);
                choiceGroup.addView(button);
            }
        }
    }

    private CompoundButton getCompoundButton(final Question questionData, final ExerciseSelectionBean selectionBean, boolean isMulti) {
        CompoundButton button = null;
        if (isMulti) {
            button = new CheckBox(context);
        } else {
            button = new RadioButton(context);
        }
        Spanned spanned = Html.fromHtml(selectionBean.content);
        button.setGravity(Gravity.TOP | Gravity.CENTER);
        button.setText(selectionBean.name + "." + spanned);
        button.setTextSize(20);
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    questionData.userAnswer = selectionBean.name;
                }

                if (listener != null) {
                    listener.checkAnswerListener(questionData);
                }
            }
        });
        if (selectionBean.name.equals(questionData.userAnswer)) {
            button.setChecked(true);
        }
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 5, 5, 5);
        button.setLayoutParams(params);
        return button;
    }

    private void setVisibleType(int id) {
        choiceGroup.setVisibility(R.id.choice_radio_group == id ? VISIBLE : GONE);
        subjectiveGroup.setVisibility(R.id.subjective_item == id ? VISIBLE : GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.correct_favourite:
                correctFavourite.setSelected(true);
                break;
            case R.id.subjective_item:
                /*if (!isFinished) {
                    ManagerActivityUtils.startScribbleActivity(SunApplication.getInstance(), questionData.id + "", title, questionData.question);
                } else {
                    EventBus.getDefault().post(new ParseAnswerEvent(questionData, title));
                }*/
                break;
        }
    }

    public void setOnCheckAnswerListener(OnCheckAnswerListener listener) {
        this.listener = listener;
    }
}
