package com.onyx.android.settings;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.onyx.android.libsetting.manager.SettingsPreferenceManager;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.ui.compat.AppCompatImageViewCollection;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;

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
            initDeviceConfig();
            initCloudStore();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDeviceConfig() {
        AppCompatImageViewCollection.setAlignView(AppCompatUtils.isColorDevice(this));
    }

    public void initCloudStore() {
        CloudStore.init(this);
    }

}
