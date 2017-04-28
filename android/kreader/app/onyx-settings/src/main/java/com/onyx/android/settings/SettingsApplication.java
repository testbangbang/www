package com.onyx.android.settings;

import android.app.Application;

import com.onyx.android.libsetting.manager.SettingsPreferenceManager;
import com.onyx.android.sdk.data.CloudStore;

/**
 * Created by suicheng on 2017/4/28.
 */
public class SettingsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
    }

    private void initConfig() {
        try {
            SettingsPreferenceManager.init(this);
            initCloudFileDownloader();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initCloudFileDownloader() {
        CloudStore.initFileDownloader(this);
    }
}
