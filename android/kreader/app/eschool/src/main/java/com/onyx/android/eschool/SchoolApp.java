package com.onyx.android.eschool;

import android.app.Application;

import com.onyx.android.eschool.device.DeviceConfig;
import com.onyx.android.eschool.utils.StudentPreferenceManager;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.ui.compat.AppCompatImageViewCollection;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;

/**
 * Created by zhuzeng on 14/11/2016.
 */
public class SchoolApp extends Application {

    static private SchoolApp sInstance = null;
    static private CloudStore cloudStore = new CloudStore();
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

    private void initSystemInBackground() {
        turnOffLed();
    }

    private void initDeviceConfig() {
        AppCompatImageViewCollection.setAlignView(AppCompatUtils.isColorDevice(this));
        DeviceConfig.sharedInstance(this);
    }

    public void turnOffLed() {
        Device.currentDevice().led(this, false);
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

    static public CloudStore getCloudStore() {
        return cloudStore;
    }

    static public DataManager getDataManager() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        return dataManager;
    }
}
