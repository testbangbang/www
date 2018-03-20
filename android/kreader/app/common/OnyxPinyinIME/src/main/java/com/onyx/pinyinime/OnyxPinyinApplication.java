package com.onyx.pinyinime;

import android.app.Application;

/**
 * Created by hehai on 18-3-20.
 */

public class OnyxPinyinApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initCrashExceptionHandler();
    }

    private void initCrashExceptionHandler() {
        CrashExceptionHandler.getInstance(getApplicationContext());
    }
}
