package com.onyx.demo.push;

import android.app.Application;

import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.demo.push.manager.LeanCloudManager;

/**
 * Created by suicheng on 2017/3/3.
 */
public class ReadingApplication extends Application {

    static private ReadingApplication sInstance;

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
