package com.onyx.android.settings;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.onyx.android.libsetting.manager.SettingsPreferenceManager;
import com.onyx.android.sdk.data.CloudStore;

/**
 * Created by suicheng on 2017/4/28.
 */
public class SettingsApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(SettingsApplication.this);
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
