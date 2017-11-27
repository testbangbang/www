package com.onyx.android.plato.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.cloud.bean.ExerciseSelectionBean;
import com.onyx.android.plato.cloud.bean.KnowledgeBean;
import com.onyx.android.plato.cloud.bean.QuestionViewBean;
import com.onyx.android.plato.common.ManagerActivityUtils;
import com.onyx.android.plato.event.ParseAnswerEvent;
import com.onyx.android.plato.interfaces.OnCheckAnswerListener;
import com.onyx.android.plato.utils.StringUtil;
import com.onyx.android.plato.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by li on 2017/10/12.
 */

public class QuestionView extends LinearLayout implements View.OnClickListener {
    private Context context;
    private AutoPagedWebView questionTitle;
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
    private boolean isFinished;
    private QuestionViewBean questionViewBean;
    private LinearLayout choiceTitle;
    private String format;
    private String questionBeanTitle;
    private Handler handler;
    private static final int WEBVIEW_FRESH_WHAT = 0x1000;
    private static final int DELAY_TIME = 10;
    private static final int DEFAULT_HEIGHT = 50;
    private TextView questionIntroduce;
    private LinearLayout questionPageLayout;
    private ImageButton questionPageLeft;
    private ImageButton questionPageRight;
    private TextView questionPageSize;
    private RelativeLayout questionTitleLayout;
    private boolean isAnalyze;
    private TextView userAnswer;
    private float choiceHeight;

    public QuestionView(Context context) {
        this(context, null);
    }

    public QuestionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttr(attrs);
        init();
        initListener();
    }

    private void initAttr(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.QuestionView);
        choiceHeight = typedArray.getDimensionPixelSize(R.styleable.QuestionView_choiceHeight, DEFAULT_HEIGHT);
        typedArray.recycle();
    }

    private void initListener() {
        subjectiveImage.setOnClickListener(this);
        correctFavourite.setOnClickListener(this);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == WEBVIEW_FRESH_WHAT) {
                    questionTitle.refresh();
                }
            }
        };

        questionTitle.setPageChangedListener(new AutoPagedWebView.PageChangedListener() {
            @Override
            public void onPageChanged(int currentPage, int totalPage) {
                questionPageLayout.setVisibility(totalPage == 1 ? GONE : VISIBLE);
                questionPageSize.setText(currentPage + "/" + totalPage);
            }
        });

        questionPageLeft.setOnClickListener(this);
        questionPageRight.setOnClickListener(this);
    }

    private void init() {
        View view = View.inflate(context, R.layout.question_view_layout, this);
        questionTitle = (AutoPagedWebView) view.findViewById(R.id.question_title);
        choiceGroup = (RadioGroup) view.findViewById(R.id.choice_radio_group);
        subjectiveGroup = (LinearLayout) view.findViewById(R.id.subjective_item);
        subjectiveImage = (ImageView) view.findViewById(R.id.subjective_image);
        choiceTitle = (LinearLayout) view.findViewById(R.id.choice_title);
        questionIntroduce = (TextView) view.findViewById(R.id.question_introduce);

        analyzeLayout = (LinearLayout) view.findViewById(R.id.question_analyze_layout);
        userAnswer = (TextView) view.findViewById(R.id.user_answer);
        knowledgePoint = (TextView) view.findViewById(R.id.knowledge_point);
        cognitiveLevel = (TextView) view.findViewById(R.id.cognitive_level);
        correctRate = (TextView) view.findViewById(R.id.correct_rate);

        correctState = (FrameLayout) view.findViewById(R.id.correct_state);
        questionCorrect = (LinearLayout) view.findViewById(R.id.question_correct);
        correctFavourite = (ImageView) view.findViewById(R.id.correct_favourite);
        correctMistake = (ImageView) view.findViewById(R.id.question_mistake);

        questionPageLayout = (LinearLayout) view.findViewById(R.id.question_page_layout);
        questionPageLeft = (ImageButton) view.findViewById(R.id.question_page_left);
        questionPageRight = (ImageButton) view.findViewById(R.id.question_page_right);
        questionPageSize = (TextView) view.findViewById(R.id.question_page_size);
        questionTitleLayout = (RelativeLayout) view.findViewById(R.id.question_title_layout);
    }

    public void setQuestionData(QuestionViewBean questionViewBean, String title) {
        this.questionViewBean = questionViewBean;
        this.title = title;
        questionTitleLayout.setVisibility(GONE);
        questionTitle.getSettings().setDefaultFontSize(context.getResources().getInteger(R.integer.web_view_font_size));
        subjectiveImage.setImageResource(R.drawable.ic_answer_area);
        questionIntroduce.setVisibility(questionViewBean.isShow() ? VISIBLE : GONE);
        format = SunApplication.getInstance().getResources().getString(R.string.item_fill_homework_title);
        questionBeanTitle = String.format(format, questionViewBean.getShowType(), questionViewBean.getAllScore()
                , questionViewBean.getExeNumber(), questionViewBean.getAllScore() / questionViewBean.getExeNumber());
        questionIntroduce.setText(questionBeanTitle);
        if (!StringUtil.isNullOrEmpty(questionViewBean.getScene()) && questionViewBean.isShowReaderComprehension()) {
            questionTitleLayout.setVisibility(VISIBLE);
            questionTitle.loadDataWithBaseURL(null, questionViewBean.getParentId() + "." +
                    questionViewBean.getScene(), "text/html", "utf-8", null);
            handler.sendEmptyMessageDelayed(WEBVIEW_FRESH_WHAT, DELAY_TIME);
        }
        setVisibleType(R.id.choice_radio_group);
        generateChoice(questionViewBean);
        analyze();
    }

    private void analyze() {
        if (!isAnalyze) {
            return;
        }
        StringBuilder point = new StringBuilder();
        StringBuilder level = new StringBuilder();
        List<KnowledgeBean> knowledgeDtoList = questionViewBean.getKnowledgeDtoList();
        if (knowledgeDtoList != null && knowledgeDtoList.size() > 0) {
            for (int i = 0; i < knowledgeDtoList.size(); i++) {
                KnowledgeBean knowledgeBean = knowledgeDtoList.get(i);
                point.append(knowledgeBean.name);
                level.append(knowledgeBean.levelName);
                if (i != knowledgeDtoList.size() - 1) {
                    point.append(",");
                    level.append(",");
                }
            }
        }
        correctMistake.setVisibility(questionViewBean.isCorrect() ? GONE : VISIBLE);
        questionCorrect.setVisibility(questionViewBean.isCorrect() ? VISIBLE : GONE);
        userAnswer.setText(String.format(SunApplication.getInstance().getString(R.string.user_answer), questionViewBean.getAnswer()));
        knowledgePoint.setText(String.format(SunApplication.getInstance().getString(R.string.knowledge_point), point.toString()));
        cognitiveLevel.setText(String.format(SunApplication.getInstance().getString(R.string.cognitive_level), level.toString()));
        correctRate.setText(String.format(SunApplication.getInstance().getString(R.string.correct_rate), questionViewBean.getAccuracy() + ""));
        correctFavourite.setSelected(questionViewBean.isExerciseFavored());
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

    private void generateChoice(QuestionViewBean questionViewBean) {
        if (choiceGroup.getChildCount() > 0) {
            choiceGroup.removeAllViews();
        }

        if (choiceTitle.getChildCount() > 0) {
            choiceTitle.removeAllViews();
        }

        if (StringUtil.isNullOrEmpty(questionViewBean.getScene())) {
            questionTitleLayout.setVisibility(VISIBLE);
            questionTitle.loadDataWithBaseURL(null, questionViewBean.getId() + questionViewBean.getContent(), "text/html", "utf-8", null);
            handler.sendEmptyMessageDelayed(WEBVIEW_FRESH_WHAT, DELAY_TIME);
        } else {
            TextView problem = new TextView(context);
            problem.setTextSize(20);
            Spanned spanned = Html.fromHtml(questionViewBean.getContent());
            String problemTitle = questionViewBean.getId() + "." + spanned;
            problem.setText(problemTitle);
            choiceTitle.addView(problem);
        }
        List<ExerciseSelectionBean> exerciseSelections = questionViewBean.getExerciseSelections();
        if (exerciseSelections == null || exerciseSelections.size() == 0) {
            setVisibleType(R.id.subjective_item);
            if (!StringUtil.isNullOrEmpty(questionViewBean.getUserAnswer())) {
                Utils.loadImageUrl(questionViewBean.getUserAnswer(), subjectiveImage, R.drawable.ic_answer_area);
            }
            return;
        }

        for (int i = 0; i < exerciseSelections.size(); i++) {
            CompoundButton button = getCompoundButton(exerciseSelections.get(i), i, false);
            choiceGroup.addView(button);
        }
    }

    private CompoundButton getCompoundButton(final ExerciseSelectionBean selectionBean, int i, boolean isMulti) {
        CompoundButton button = null;
        if (isMulti) {
            button = new CheckBox(context);
        } else {
            button = new RadioButton(context);
        }
        Spanned spanned = Html.fromHtml(selectionBean.content);
        button.setGravity(Gravity.TOP | Gravity.CENTER);
        button.setText(selectionBean.name + "." + spanned);
        button.setTextSize(18);
        button.setId(i);
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    questionViewBean.setUserAnswer(selectionBean.name);
                }

                if (listener != null) {
                    listener.checkAnswerListener(questionViewBean);
                }
            }
        });
        if (selectionBean.name.equals(questionViewBean.getUserAnswer())) {
            button.setChecked(true);
        }
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, (int) choiceHeight);
        button.setLayoutParams(params);
        return button;
    }

    public void setAnalyze(boolean isAnalyze) {
        this.isAnalyze = isAnalyze;
    }

    private void setVisibleType(int id) {
        choiceGroup.setVisibility(R.id.choice_radio_group == id ? VISIBLE : GONE);
        subjectiveGroup.setVisibility(R.id.subjective_item == id ? VISIBLE : GONE);
        analyzeLayout.setVisibility(isAnalyze ? VISIBLE : GONE);
        correctState.setVisibility(isAnalyze && R.id.choice_radio_group == id ? VISIBLE : GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.correct_favourite:
                deleteOrFavorite();
                break;
            case R.id.subjective_image:
                jump();
                break;
            case R.id.question_page_left:
                questionTitle.prevPage();
                break;
            case R.id.question_page_right:
                questionTitle.nextPage();
                break;
        }
    }

    private void jump() {
        if (!isFinished) {
            ManagerActivityUtils.startScribbleActivity(SunApplication.getInstance(), String.valueOf(questionViewBean.getTaskId()) + String.valueOf(questionViewBean.getId()), title, Html.fromHtml(questionViewBean.getContent()) + "");
        } else {
            EventBus.getDefault().post(new ParseAnswerEvent(questionViewBean, title));
        }
    }

    private void deleteOrFavorite() {
        if (listener != null) {
            listener.deleteOrFavoriteQuestion(questionViewBean.getTaskId(), questionViewBean.getId());
        }
        correctFavourite.setSelected(!correctFavourite.isSelected());
    }

    public void setOnCheckAnswerListener(OnCheckAnswerListener listener) {
        this.listener = listener;
    }
}
