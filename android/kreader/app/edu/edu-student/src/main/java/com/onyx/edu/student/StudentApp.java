package com.onyx.edu.student;

import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.utils.CloudConf;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.data.v2.ContentService;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.ui.compat.AppCompatImageViewCollection;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;
import com.onyx.android.sdk.utils.DeviceReceiver;
import com.onyx.edu.student.device.DeviceConfig;
import com.onyx.edu.student.events.DataRefreshEvent;
import com.onyx.edu.student.holder.LibraryDataHolder;
import com.onyx.edu.student.manager.ConfigPreferenceManager;
import com.squareup.leakcanary.LeakCanary;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by suicheng on 2017/10/20.
 */
public class StudentApp extends MultiDexApplication {
    private static final String TAG = "StudentApp";

    private static LibraryDataHolder dataHolder;
    private static CloudManager cloudStore;

    private DeviceReceiver deviceReceiver = new DeviceReceiver();

    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
        LeakCanary.install(this);
    }

    private void initConfig() {
        initDeviceConfig();
        ConfigPreferenceManager.init(this);
        initCloudStore();
        initEventListener();
        initSystemInBackground();
    }

    private void initDeviceConfig() {
        AppCompatUtils.setColorSupport(DeviceConfig.sharedInstance(this).isDeviceSupportColor());
        AppCompatImageViewCollection.setAlignView(AppCompatUtils.isColorDevice(this));
    }

    private void initEventListener() {
        deviceReceiver.setMediaStateListener(new DeviceReceiver.MediaStateListener() {

            @Override
            public void onMediaScanStarted(Intent intent) {
                if (DeviceConfig.sharedInstance(getApplicationContext()).supportMediaScan()) {

                }
            }

            @Override
            public void onMediaMounted(Intent intent) {
                Log.w(TAG, "onMediaMounted " + intent.getData().toString());
                if (EnvironmentUtil.isRemovableSDDirectory(getApplicationContext(), intent)) {
                }
            }

            @Override
            public void onMediaUnmounted(Intent intent) {
                if (EnvironmentUtil.isRemovableSDDirectory(getApplicationContext(), intent)) {
                }
            }

            @Override
            public void onMediaBadRemoval(Intent intent) {
                if (EnvironmentUtil.isRemovableSDDirectory(getApplicationContext(), intent)) {
                }
            }

            @Override
            public void onMediaRemoved(Intent intent) {
            }
        });
        deviceReceiver.setWifiStateListener(new DeviceReceiver.WifiStateListener() {
            @Override
            public void onWifiConnected(Intent intent) {
                EventBus.getDefault().post(new DataRefreshEvent());
            }
        });
        deviceReceiver.enable(getApplicationContext(), true);
    }

    public void initCloudStore() {
        CloudStore.init(this);
    }

    private void initSystemInBackground() {
        enableWifiDetect();
        turnOffLed();
    }

    private void enableWifiDetect() {
        Device.currentDevice().enableWifiDetect(this, true);
    }

    public void turnOffLed() {
        Device.currentDevice().led(this, false);
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    public static CloudManager getCloudStore() {
        if (cloudStore == null) {
            String host = "http://120.78.79.5/";
            String api = "http://120.78.79.5/api/";
            String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1OWY4MzdlNDAxYTIwNDViZTA0MTQ3OGYiLCJyb2xlIjoic3R1ZGVudCIsImlhdCI6MTUwOTQzOTYzNiwiZXhwIjoxNTEyMDMxNjM2fQ.YEb64QrZrFlhEOb3RvcRYMbxOF4u01kLyiEIusvZRbA";
            cloudStore = CloudStore.createCloudManager(CloudConf.create(host, api, Constant.DEFAULT_CLOUD_STORAGE));
            ServiceFactory.addRetrofitTokenHeader(api,
                    Constant.HEADER_AUTHORIZATION,
                    ContentService.CONTENT_AUTH_PREFIX + token);
        }
        return cloudStore;
    }

    public static LibraryDataHolder getLibraryDataHolder() {
        if (dataHolder == null) {
            dataHolder = new LibraryDataHolder();
            dataHolder.setCloudManager(getCloudStore());
        }
        return dataHolder;
    }
}
