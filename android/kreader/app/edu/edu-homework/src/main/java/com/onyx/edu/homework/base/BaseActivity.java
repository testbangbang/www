package com.onyx.edu.homework.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.onyx.android.sdk.utils.DeviceUtils;

/**
 * Created by lxm on 2017/12/5.
 */

public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceUtils.setFullScreenOnCreate(this, isFullScreen());
    }

    public abstract boolean isFullScreen();

    @Override
    protected void onPostResume() {
        super.onPostResume();
        DeviceUtils.setFullScreenOnResume(this, isFullScreen());
    }
}
