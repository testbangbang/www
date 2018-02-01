package com.onyx.android.note.common.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.onyx.android.sdk.utils.DeviceUtils;

/**
 * Created by lxm on 2018/1/30.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isFullScreen()) {
            DeviceUtils.setFullScreenOnCreate(this, isFullScreen());
        }
    }

    public abstract boolean isFullScreen();

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (isFullScreen()) {
            DeviceUtils.setFullScreenOnResume(this, isFullScreen());
        }
    }
}
