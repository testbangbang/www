package com.onyx.jdread;

import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.utils.DeviceReceiver;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.common.ActionChain;
import com.onyx.jdread.common.AppBaseInfo;
import com.onyx.jdread.library.action.RxFileSystemScanAction;
import com.onyx.jdread.library.model.DataBundle;
import com.onyx.jdread.shop.model.StoreDataBundle;

/**
 * Created by hehai on 17-12-6.
 */

public class JDReadApplication extends MultiDexApplication {
    private static final String TAG = JDReadApplication.class.getSimpleName();
    private static JDReadApplication instance = null;
    private static DataBundle dataBundle;
    private static StoreDataBundle storeDataBundle;
    private DeviceReceiver deviceReceiver = new DeviceReceiver();
    private AppBaseInfo appBaseInfo;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(JDReadApplication.this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
    }

    private void initConfig() {
        instance = this;
        DataManager.init(instance, null);
        PreferenceManager.init(this);
        initEventListener();
        initAppBaseInfo();
    }

    private void initEventListener() {
        deviceReceiver.setMediaStateListener(new DeviceReceiver.MediaStateListener() {

            @Override
            public void onMediaScanStarted(Intent intent) {
                processRemovableSDCardScan();
            }

            @Override
            public void onMediaMounted(Intent intent) {
                processRemovableSDCardScan();
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

    public static JDReadApplication getInstance() {
        return instance;
    }

    public static DataBundle getDataBundle() {
        if (dataBundle == null) {
            dataBundle = new DataBundle(instance);
        }
        return dataBundle;
    }

    public static StoreDataBundle getStoreDataBundle() {
        if (storeDataBundle == null) {
            storeDataBundle = new StoreDataBundle();
        }
        return storeDataBundle;
    }

    private void initAppBaseInfo() {
        appBaseInfo = new AppBaseInfo();
    }

    public AppBaseInfo getAppBaseInfo() {
        return appBaseInfo;
    }
}
