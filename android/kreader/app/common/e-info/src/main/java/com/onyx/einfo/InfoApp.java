package com.onyx.einfo;

import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.onyx.einfo.action.FileSystemScanAction;
import com.onyx.einfo.device.DeviceConfig;
import com.onyx.einfo.events.DataRefreshEvent;
import com.onyx.einfo.holder.LibraryDataHolder;
import com.onyx.einfo.manager.ConfigPreferenceManager;
import com.onyx.einfo.manager.LeanCloudManager;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.utils.CloudConf;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.ui.compat.AppCompatImageViewCollection;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;
import com.onyx.android.sdk.utils.DeviceReceiver;
import com.onyx.android.sdk.utils.StringUtils;
import com.squareup.leakcanary.LeakCanary;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by zhuzeng on 14/11/2016.
 */
public class InfoApp extends MultiDexApplication {
    static private final String MMC_STORAGE_ID = "flash";

    static private InfoApp sInstance = null;
    static private CloudStore cloudStore;
    static private LibraryDataHolder libraryDataHolder;

    private DeviceReceiver deviceReceiver = new DeviceReceiver();

    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
        LeakCanary.install(this);
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(InfoApp.this);
    }

    @Override
    public void onTerminate() {
        terminateCloudStore();
        deviceReceiver.enable(this, false);
        super.onTerminate();
    }

    private void initConfig() {
        try {
            sInstance = this;
            ConfigPreferenceManager.init(this);
            initCloudStore();
            initDeviceConfig();
            initEventListener();
            initFrescoLoader();
            initLeanCloud();
            initSystemInBackground();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    private void initSystemInBackground() {
        enableWifiDetect();
        turnOffLed();
    }

    private void initDeviceConfig() {
        AppCompatImageViewCollection.setAlignView(AppCompatUtils.isColorDevice(this));
        DeviceConfig.sharedInstance(this);
    }

    private void initEventListener() {
        deviceReceiver.setMediaStateListener(new DeviceReceiver.MediaStateListener() {
            @Override
            public void onMediaMounted(Intent intent) {
                if (EnvironmentUtil.isRemovableSDDirectory(getApplicationContext(), intent)) {
                    String sdcardCid = EnvironmentUtil.getRemovableSDCardCid();
                    if (StringUtils.isNullOrEmpty(sdcardCid)) {
                        return;
                    }
                    FileSystemScanAction systemScanAction = new FileSystemScanAction(sdcardCid, false);
                    systemScanAction.execute(getLibraryDataHolder(), null);
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
                LeanCloudManager.saveInstallation(singleton().getApplicationContext());
            }
        });
        deviceReceiver.enable(getApplicationContext(), true);
    }

    public void turnOffLed() {
        Device.currentDevice().led(this, false);
    }

    private void initLeanCloud() {
        LeanCloudManager.initialize(this, DeviceConfig.sharedInstance(this).getLeanCloudApplicationId(),
                DeviceConfig.sharedInstance(this).getLeanCloudClientKey());
    }

    public void initCloudStore() {
        CloudStore.init(this);
    }

    private void initFrescoLoader() {
        Fresco.initialize(singleton().getApplicationContext());
    }

    private void enableWifiDetect() {
        Device.currentDevice().enableWifiDetect(this, true);
    }

    public void terminateCloudStore() {
        CloudStore.terminate();
    }

    public static InfoApp singleton() {
        return sInstance;
    }

    static public CloudStore getCloudStore() {
        if (cloudStore == null) {
            cloudStore = new CloudStore();
            cloudStore.setCloudConf(getCloudConf());
        }
        return cloudStore;
    }

    private static CloudConf getCloudConf() {
        String host = DeviceConfig.sharedInstance(singleton()).getCloudContentHost();
        String api = DeviceConfig.sharedInstance(singleton()).getCloudContentApi();
        CloudConf cloudConf = new CloudConf(host, api, Constant.DEFAULT_CLOUD_STORAGE);
        return cloudConf;
    }


    static public DataManager getDataManager() {
        return getLibraryDataHolder().getDataManager();
    }

    public static LibraryDataHolder getLibraryDataHolder() {
        if (libraryDataHolder == null) {
            libraryDataHolder = new LibraryDataHolder(sInstance);
            libraryDataHolder.setCloudManager(getCloudStore().getCloudManager());
        }
        return libraryDataHolder;
    }

    public static Map<String, String> getInstallationIdMap() {
        Map<String, String> map = new HashMap<>();
        map.put(Constant.PUSH_KEY, LeanCloudManager.getInstallationId());
        return map;
    }
}