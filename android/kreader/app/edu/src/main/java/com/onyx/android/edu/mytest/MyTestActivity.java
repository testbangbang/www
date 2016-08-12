package com.onyx.android.edu.mytest;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.onyx.android.edu.R;
import com.onyx.android.edu.base.BaseActivity;
import com.onyx.android.edu.utils.ActivityUtils;

import butterknife.Bind;

/**
 * Created by ming on 16/6/24.
 */
public class MyTestActivity extends BaseActivity {

    @Bind(R.id.contentFrame)
    FrameLayout mContentFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_my_test;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        MyTestFragment myTestFragment = (MyTestFragment) getFragmentManager()
                .findFragmentById(R.id.contentFrame);
        if (myTestFragment == null) {
            myTestFragment = MyTestFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getFragmentManager(),
                    myTestFragment, R.id.contentFrame);
        }

        new MyTestPresenter(myTestFragment);
    }

}
