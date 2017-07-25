package com.onyx.einfo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by ming on 16/6/24.
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

        initConfig();
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
