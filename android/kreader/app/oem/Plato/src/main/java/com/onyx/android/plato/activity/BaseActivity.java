package com.onyx.android.plato.activity;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.umeng.analytics.MobclickAgent;

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
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
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
