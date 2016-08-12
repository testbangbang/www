package com.onyx.android.edu.speakingtest;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.onyx.android.edu.R;
import com.onyx.android.edu.base.BaseActivity;
import com.onyx.android.edu.utils.ActivityUtils;

import butterknife.Bind;

/**
 * Created by ming on 16/6/24.
 */
public class SpeakingTestActivity extends BaseActivity {

    @Bind(R.id.contentFrame)
    FrameLayout mContentFrame;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_speaking_test;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        SpeakingTestFragment speakingTestFragment = (SpeakingTestFragment) getFragmentManager()
                .findFragmentById(R.id.contentFrame);
        if (speakingTestFragment == null) {
            speakingTestFragment = SpeakingTestFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getFragmentManager(),
                    speakingTestFragment, R.id.contentFrame);
        }

        new SpeakingTestPresenter(speakingTestFragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
