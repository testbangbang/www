package com.onyx.android.dr.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.onyx.android.dr.event.BackToMainViewEvent;
import com.onyx.android.dr.fragment.ChildViewID;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;

/**
 * Created by hehai on 17/6/28.
 */
public abstract class BaseActivity extends OnyxAppCompatActivity {

    protected abstract Integer getLayoutId();

    protected abstract void initConfig();

    protected abstract void initView();

    protected abstract void initData();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        initConfig();
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onObject(Object event) {

    }
}
