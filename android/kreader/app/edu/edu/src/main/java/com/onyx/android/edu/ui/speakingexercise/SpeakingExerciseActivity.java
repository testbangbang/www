package com.onyx.android.edu.ui.speakingexercise;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.onyx.android.edu.R;
import com.onyx.android.edu.base.BaseActivity;
import com.onyx.android.edu.utils.ActivityUtils;

import butterknife.Bind;

/**
 * Created by ming on 16/6/24.
 */
public class SpeakingExerciseActivity extends BaseActivity {

    @Bind(R.id.contentFrame)
    FrameLayout mContentFrame;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_general;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        SpeakingExerciseFragment speakingExerciseFragment = (SpeakingExerciseFragment) getFragmentManager()
                .findFragmentById(R.id.contentFrame);
        if (speakingExerciseFragment == null) {
            speakingExerciseFragment = SpeakingExerciseFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getFragmentManager(),
                    speakingExerciseFragment, R.id.contentFrame);
        }

        new SpeakingExercisePresenter(speakingExerciseFragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
