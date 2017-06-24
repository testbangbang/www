package com.onyx.android.edu;

import android.app.Application;

import com.onyx.android.sdk.ui.compat.AppCompatImageViewCollection;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;

/**
 * Created by ming on 2016/11/1.
 */

public class EduApp extends Application{

    private static EduApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        initPl107DeviceConfig();
        instance = this;
    }

    public static EduApp instance() {
        return instance;
    }

    public void initPl107DeviceConfig() {
        AppCompatImageViewCollection.setAlignView(AppCompatUtils.isColorDevice(this));
    }

}
