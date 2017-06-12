package com.onyx.android.edu.ui.exercisedetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
public class ExerciseDetailActivity extends BaseActivity implements View.OnClickListener {

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
    private ExerciseDetailFragment exerciseDetailFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_general;
    }

    @Override
    protected void initView() {
        leftTitle.setText(getString(R.string.exercise_test));
        toolbarTitle.setText(R.string.exercise_detail);
        rightArrow.setVisibility(View.INVISIBLE);
        back.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        exerciseDetailFragment = (ExerciseDetailFragment) getFragmentManager()
                .findFragmentById(R.id.contentFrame);
        if (exerciseDetailFragment == null) {
            exerciseDetailFragment = ExerciseDetailFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getFragmentManager(),
                    exerciseDetailFragment, R.id.contentFrame);
        }

        new ExerciseDetailPresenter(exerciseDetailFragment);
    }

    @Override
    public void onClick(View v) {
        if (back.equals(v)){
            finish();
        }
    }
}
