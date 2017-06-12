package com.onyx.android.edu.ui.exerciserespond;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.android.edu.R;
import com.onyx.android.edu.base.BaseActivity;
import com.onyx.android.edu.base.Constant;
import com.onyx.android.edu.utils.ActivityUtils;
import com.onyx.libedu.model.BookNode;
import com.onyx.libedu.model.ChooseQuestionVariable;
import com.onyx.libedu.model.Question;

import java.util.List;

import butterknife.Bind;


/**
 * Created by ming on 16/6/24.
 */
public class ExerciseRespondActivity extends BaseActivity {

    public static final String SHOW_ANSWER = "show_answer";

    @Bind(R.id.contentFrame)
    FrameLayout mContentFrame;
    @Bind(R.id.back)
    ImageView mBack;
    @Bind(R.id.left_title)
    TextView mLeftTitle;
    @Bind(R.id.toolbar_title)
    TextView mToolbarTitle;
    @Bind(R.id.right_title)
    TextView mRightTitle;
    @Bind(R.id.toolbar_content)
    RelativeLayout mToolbarContent;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_general;
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        boolean showAnswer = intent.getBooleanExtra(SHOW_ANSWER, false);
        String questions = intent.getStringExtra(Constant.QUESTION);
        String variable = intent.getStringExtra(Constant.CHOOSE_QUESTION_VARIABLE);
        List<Question> questionList = JSON.parseObject(questions, new TypeReference<List<Question>>(){});
        ChooseQuestionVariable chooseQuestionVariable = JSON.parseObject(variable, ChooseQuestionVariable.class);

        ExerciseRespondFragment exerciseRespondFragment = (ExerciseRespondFragment) getFragmentManager()
                .findFragmentById(R.id.contentFrame);
        if (exerciseRespondFragment == null) {
            exerciseRespondFragment = ExerciseRespondFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getFragmentManager(),
                    exerciseRespondFragment, R.id.contentFrame);
        }


        new ExerciseRespondPresenter(exerciseRespondFragment, showAnswer, questionList, chooseQuestionVariable);

        mToolbarTitle.setVisibility(View.GONE);
        mRightTitle.setVisibility(View.GONE);
        String text = String.format("科目：%s 分册 %s 共有%d道习题", chooseQuestionVariable.getSubject().getSubjectName(),
                chooseQuestionVariable.getTextbook().getBookName(),
                questionList.size());
        mLeftTitle.setText(text);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
