package com.onyx.android.eschool;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.onyx.android.eschool.device.DeviceConfig;
import com.onyx.android.eschool.utils.StudentPreferenceManager;
import com.onyx.android.libsetting.view.activity.FirmwareOTAActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.manager.OTAManager;
import com.onyx.android.sdk.data.model.Firmware;
import com.onyx.android.sdk.data.request.cloud.FirmwareUpdateRequest;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.ui.compat.AppCompatImageViewCollection;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;

/**
 * Created by zhuzeng on 14/11/2016.
 */
public class SchoolApp extends Application {
    private static final int OTA_CHECK_DELAY_MS = 1500;

    static private SchoolApp sInstance = null;
    static private DataManager dataManager;
    private Handler handler = new Handler(Looper.getMainLooper());


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
}
