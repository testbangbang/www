package com.onyx.android.edu.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.onyx.android.edu.utils.ToastUtils;

import butterknife.ButterKnife;

/**
 * Created by ming on 16/6/24.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected abstract Integer getLayoutId();
    protected abstract void initView();
    protected abstract void initData();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public void showToast(String message){
        ToastUtils.showToast(this,message);
    }
}
