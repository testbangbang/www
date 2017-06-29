package com.onyx.android.settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.onyx.android.libsetting.SettingConfig;
import com.onyx.android.libsetting.manager.SettingsPreferenceManager;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.manager.OssManager;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.ui.compat.AppCompatImageViewCollection;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;

/**
 * Created by suicheng on 2017/4/28.
 */
public class SettingsApplication extends MultiDexApplication {

    static public final String OSS_LOG_KEY_ID = "LTAIXvqBXTJUKEf0";
    static public final String OSS_LOG_KEY_SECRET = "tKRDXDOPGBm9wK0GHHaJG2HaqfKWbY";
    static public final String OSS_LOG_BUCKET = "onyx-log-collection";
    static public final String OSS_LOG_ENDPOINT = "http://onyx-log-collection.onyx-international.cn";

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
            initErrorReportAction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initErrorReportAction() {
        SettingConfig.sharedInstance(this).setErrorReportAction("com.onyx.intent.action.ERROR_REPORT");
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

    public static OssManager getLogOssManger(Context context) {
        OssManager.OssConfig ossConfig = new OssManager.OssConfig();
        ossConfig.setBucketName(OSS_LOG_BUCKET);
        ossConfig.setEndPoint(OSS_LOG_ENDPOINT);
        ossConfig.setKeyId(OSS_LOG_KEY_ID);
        ossConfig.setKeySecret(OSS_LOG_KEY_SECRET);
        return new OssManager(context.getApplicationContext(), ossConfig);
    }
}
