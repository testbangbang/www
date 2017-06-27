package com.onyx.edu.manager;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * Created by suicheng on 2017/6/16.
 */

public class AppApplication extends MultiDexApplication {
    private static AppApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(AppApplication.this);
    }

    public static final AppApplication sInstance() {
        return sInstance;
    }
}
