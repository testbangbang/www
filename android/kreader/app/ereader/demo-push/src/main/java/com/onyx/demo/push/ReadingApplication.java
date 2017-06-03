package com.onyx.demo.push;

import android.app.Application;
import android.content.Context;

import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.demo.push.manager.LeanCloudManager;

/**
 * Created by suicheng on 2017/3/3.
 */
public class ReadingApplication extends MultiDexApplication {

    static private ReadingApplication sInstance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(ReadingApplication.this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
    }

    private void initConfig() {
        try {
            sInstance = this;
            initLeanCloud();
            initDownloadManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initLeanCloud() {
        LeanCloudManager.initialize(sInstance.getApplicationContext());
    }

    private void initDownloadManager() {
        OnyxDownloadManager.init(sInstance.getApplicationContext());
        OnyxDownloadManager.getInstance();
    }
}
