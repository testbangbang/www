package com.onyx.android.edu.ui.myexercise;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.onyx.android.edu.R;
import com.onyx.android.edu.base.BaseActivity;
import com.onyx.android.edu.utils.ActivityUtils;

import butterknife.Bind;

/**
 * Created by ming on 16/6/24.
 */
public class MyExerciseActivity extends BaseActivity {

    @Bind(R.id.contentFrame)
    FrameLayout mContentFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_general;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        MyExerciseFragment myExerciseFragment = (MyExerciseFragment) getFragmentManager()
                .findFragmentById(R.id.contentFrame);
        if (myExerciseFragment == null) {
            myExerciseFragment = MyExerciseFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getFragmentManager(),
                    myExerciseFragment, R.id.contentFrame);
        }

        new MyExercisePresenter(myExerciseFragment);
    }

}
