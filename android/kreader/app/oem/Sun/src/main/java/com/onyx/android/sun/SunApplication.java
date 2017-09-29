package com.onyx.android.sun;

import android.app.Application;

import com.onyx.android.sun.common.AppConfigData;

/**
 * Created by hehai on 17-9-29.
 */

public class SunApplication extends Application {
    private static SunApplication instence;

    public static SunApplication getInstence() {
        return instence;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initContext();
        AppConfigData.loadMainTabData(instence);
    }

    private void initContext() {
        instence = this;
    }
}
