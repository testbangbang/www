package com.onyx.android.settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.onyx.android.libsetting.manager.SettingsPreferenceManager;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.device.Device;
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
        registerActivityLifecycle();
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

    private void registerActivityLifecycle() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (AppCompatUtils.isColorDevice(activity)){
                    Device.currentDevice().postInvalidate(activity.getWindow().getDecorView(), UpdateMode.GC);
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    public void initCloudStore() {
        CloudStore.init(this);
    }

}
