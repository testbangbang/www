package com.onyx.android.edu.ui.chooseexercise;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.base.BaseActivity;
import com.onyx.android.edu.base.Global;
import com.onyx.android.edu.utils.ActivityUtils;
import com.onyx.android.edu.ui.wrongquestions.WrongQuestionActivity;

import butterknife.Bind;

/**
 * Created by ming on 16/6/24.
 */
public class ChooseExerciseActivity extends BaseActivity {

    @Bind(R.id.left_title)
    TextView mLeftTitle;
    @Bind(R.id.back)
    ImageView mBack;
    @Bind(R.id.toolbar_title)
    TextView mToolbarTitle;
    @Bind(R.id.right_title)
    TextView mRightTitle;
    @Bind(R.id.toolbar_content)
    RelativeLayout mToolbarContent;
    @Bind(R.id.contentFrame)
    FrameLayout mContentFrame;
    @Bind(R.id.right_arrow)
    ImageView rightArrow;
    @Bind(R.id.divider_line)
    View dividerLine;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private ChooseExerciseFragment chooseExerciseFragment;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_general;
    }

    @Override
    protected void initView() {
        mBack.setVisibility(View.INVISIBLE);
        mToolbarTitle.setText(R.string.app_name);
//        mRightTitle.setText(getString(R.string.enter_wrong_view));
//        mLeftTitle.setText(getString(R.string.exercise_test));
//        rightArrow.setVisibility(View.VISIBLE);
//        mRightTitle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(ChooseExerciseActivity.this, WrongQuestionActivity.class));
//            }
//        });
    }

    @Override
    protected void initData() {
        chooseExerciseFragment = (ChooseExerciseFragment) getFragmentManager()
                .findFragmentById(R.id.contentFrame);
        if (chooseExerciseFragment == null) {
            chooseExerciseFragment = ChooseExerciseFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getFragmentManager(),
                    chooseExerciseFragment, R.id.contentFrame);
        }

        new ChooseExercisePresenter(chooseExerciseFragment);

        Global.getInstance().loadTestData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
