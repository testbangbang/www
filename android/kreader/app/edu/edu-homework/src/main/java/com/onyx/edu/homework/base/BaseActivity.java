package com.onyx.edu.homework.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.onyx.android.sdk.utils.DeviceUtils;

import me.yokeyword.fragmentation.SupportActivity;

/**
 * Created by lxm on 2017/12/5.
 */

public abstract class BaseActivity extends SupportActivity {

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
