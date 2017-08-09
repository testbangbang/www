package com.onyx.android.sample;

import android.app.Application;

import com.onyx.android.sdk.data.OnyxDownloadManager;

/**
 * Created by suicheng on 2017/3/23.
 */

public class SampleApplication extends Application {
    private static SampleApplication sInstance;

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
        OnyxDownloadManager.getInstance(sInstance);
    }
}
