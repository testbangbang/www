package com.onyx.android.sample;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.onyx.android.sdk.data.OnyxDownloadManager;

/**
 * Created by suicheng on 2017/3/23.
 */

public class SampleApplication extends MultiDexApplication {
    private static SampleApplication sInstance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
    }

    private void initConfig() {
        try {
            sInstance = this;
            initDownloadManager();
        } catch (Exception e) {
        }
    }

    private void initDownloadManager() {
        OnyxDownloadManager.init(this);
        OnyxDownloadManager.getInstance();
    }
}
