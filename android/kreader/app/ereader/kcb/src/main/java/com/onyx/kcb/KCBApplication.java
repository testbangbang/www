package com.onyx.kcb;

import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.utils.DeviceReceiver;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kcb.action.ActionChain;
import com.onyx.kcb.action.RxFileSystemScanAction;
import com.onyx.kcb.device.DeviceConfig;
import com.onyx.kcb.holder.DataBundle;
import com.onyx.kcb.manager.ConfigPreferenceManager;


/**
 * Created by hehai on 17-11-13.
 */

public class KCBApplication extends MultiDexApplication {
    private boolean isMetadataScanned = false;
    private static final String TAG = KCBApplication.class.getSimpleName();
    private static KCBApplication instance = null;
    private static DataBundle dataBundle;
    private DeviceReceiver deviceReceiver = new DeviceReceiver();

    public static KCBApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(KCBApplication.this);
    }

    private void initConfig() {
        instance = this;
        PreferenceManager.init(instance);
        DataManager.init(this, null);
        initFrescoLoader();
        initEventListener();
        ConfigPreferenceManager.init(KCBApplication.this);
    }

    private void initEventListener() {
        deviceReceiver.setMediaStateListener(new DeviceReceiver.MediaStateListener() {

            @Override
            public void onMediaScanStarted(Intent intent) {
                if (DeviceConfig.sharedInstance(getApplicationContext()).isMediaScanSupport()) {
                    processRemovableSDCardScan();
                }
            }

            @Override
            public void onMediaMounted(Intent intent) {
                Log.w(TAG, "onMediaMounted " + intent.getData().toString());
                if (EnvironmentUtil.isRemovableSDDirectory(getApplicationContext(), intent)) {
                    processRemovableSDCardScan();
                    setMetadataScanned(true);
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

            }
        });
        deviceReceiver.enable(getApplicationContext(), true);
    }

    private void processRemovableSDCardScan() {
        ActionChain actionChain = new ActionChain();
        actionChain.addAction(new RxFileSystemScanAction(RxFileSystemScanAction.MMC_STORAGE_ID, true));
        String sdcardCid = EnvironmentUtil.getRemovableSDCardCid();
        if (StringUtils.isNotBlank(sdcardCid)) {
            actionChain.addAction(new RxFileSystemScanAction(sdcardCid, false));
        }
        actionChain.execute(getDataBundle(), null);
    }

    public static DataBundle getDataBundle() {
        if (dataBundle == null) {
            dataBundle = new DataBundle(instance);
        }
        return dataBundle;
    }

    public boolean isMetadataScanned() {
        return isMetadataScanned;
    }

    public void setMetadataScanned(boolean metadataScanned) {
        this.isMetadataScanned = metadataScanned;
    }

    private void initFrescoLoader() {
        Fresco.initialize(getInstance().getApplicationContext());
    }

}
