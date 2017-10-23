package com.onyx.android.sun.activity;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by li on 2017/9/29.
 */

public abstract class BaseActivity extends OnyxAppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int viewId = getViewId();
        ViewDataBinding binding = DataBindingUtil.setContentView(this, viewId);
        EventBus.getDefault().register(this);
        initData();
        initView(binding);
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void OnObjectEvent(Object event) {
    }

    protected abstract void initData();

    protected abstract void initView(ViewDataBinding binding);

    protected abstract void initListener();

    protected abstract int getViewId();
}
