package com.onyx.android.edu.ui.findexercise;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.base.BaseActivity;
import com.onyx.android.edu.utils.ActivityUtils;

import butterknife.Bind;

/**
 * Created by ming on 16/8/18.
 */
public class FindExerciseActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.back)
    ImageButton back;
    @Bind(R.id.left_title)
    TextView leftTitle;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.right_title)
    TextView rightTitle;
    @Bind(R.id.right_arrow)
    ImageButton rightArrow;
    @Bind(R.id.toolbar_content)
    RelativeLayout toolbarContent;
    @Bind(R.id.divider_line)
    View dividerLine;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.contentFrame)
    FrameLayout contentFrame;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_general;
    }

    @Override
    protected void initView() {
        back.setOnClickListener(this);
        rightArrow.setVisibility(View.INVISIBLE);
        leftTitle.setText(getString(R.string.exercise_test));
        toolbarTitle.setText(getString(R.string.find_more));
    }

    @Override
    protected void initData() {
        FindExerciseFragment findExerciseFragment = (FindExerciseFragment) getFragmentManager()
                .findFragmentById(R.id.contentFrame);
        if (findExerciseFragment == null) {
            findExerciseFragment = FindExerciseFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getFragmentManager(),
                    findExerciseFragment, R.id.contentFrame);
        }

        new FindExercisePresenter(findExerciseFragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(back)){
            finish();
        }
    }
}
