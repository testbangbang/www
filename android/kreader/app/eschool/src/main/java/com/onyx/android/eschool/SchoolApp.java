package com.onyx.android.eschool;

import android.app.Application;
import android.content.Context;

import com.onyx.android.eschool.device.DeviceConfig;
import com.onyx.android.eschool.utils.StudentPreferenceManager;
import com.onyx.android.libsetting.SettingConfig;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.manager.OTAManager;
import com.onyx.android.sdk.data.manager.OssManager;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.ui.compat.AppCompatImageViewCollection;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;

/**
 * Created by zhuzeng on 14/11/2016.
 */
public class SchoolApp extends Application {
    static public final String ERROR_REPORT_ACTION = "onyx.eschool.intent.action.ERROR_REPORT";

    static public final String OSS_LOG_KEY_ID = "LTAIXvqBXTJUKEf0";
    static public final String OSS_LOG_KEY_SECRET = "tKRDXDOPGBm9wK0GHHaJG2HaqfKWbY";
    static public final String OSS_LOG_BUCKET = "onyx-log-collection";
    static public final String OSS_LOG_ENDPOINT = "http://onyx-log-collection.onyx-international.cn";

    static private SchoolApp sInstance = null;
    static private DataManager dataManager;

    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
    }

    private void initConfig() {
        try {
            sInstance = this;
            StudentPreferenceManager.init(this);
            initCloudStoreConfig();
            initPl107DeviceConfig();
            initSettingErrorReportAction();
            initDeviceConfig();
            initSystemInBackground();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    public void initCloudStoreConfig() {
        initCloudDatabase();
        initCloudFileDownloader();
    }

    public void initPl107DeviceConfig() {
        AppCompatImageViewCollection.isPl107Device = AppCompatUtils.isPL107Device(this);
    }

    private void initSystemInBackground() {
        turnOffLed();
    }

    private void initDeviceConfig() {
        DeviceConfig.sharedInstance(this);
    }

    private void initSettingErrorReportAction() {
        SettingConfig.sharedInstance(sInstance).setErrorReportAction(ERROR_REPORT_ACTION);
    }

    public void turnOffLed() {
        Device.currentDevice().led(this, false);
    }

    @Override
    public void onTerminate() {
        terminateCloudDatabase();
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public void initCloudDatabase() {
        CloudStore.initDatabase(this);
    }

    public void initCloudFileDownloader() {
        CloudStore.initFileDownloader(this);
    }

    public void terminateCloudDatabase() {
        CloudStore.terminateCloudDatabase();
    }

    public static SchoolApp singleton() {
        return sInstance;
    }

    static public DataManager getDataManager() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        return dataManager;
    }

    public static CloudStore getCloudStore() {
        return OTAManager.sharedInstance().getCloudStore();
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
