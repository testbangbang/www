package com.onyx.einfo;

import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.action.ActionContext;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.im.IMConfig;
import com.onyx.android.sdk.im.IMManager;
import com.onyx.android.sdk.im.event.MessageEvent;
import com.onyx.android.sdk.im.push.LeanCloudManager;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.einfo.action.ContentImportAction;
import com.onyx.einfo.action.FileSystemScanAction;
import com.onyx.einfo.action.MediaScanAction;
import com.onyx.einfo.device.DeviceConfig;
import com.onyx.einfo.events.DataRefreshEvent;
import com.onyx.einfo.holder.LibraryDataHolder;
import com.onyx.einfo.manager.ConfigPreferenceManager;
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
import com.onyx.einfo.manager.EventManager;
import com.onyx.einfo.manager.PushManager;
import com.squareup.leakcanary.LeakCanary;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;


/**
 * Created by zhuzeng on 14/11/2016.
 */
public class InfoApp extends MultiDexApplication {
    private static final String TAG = "InfoApp";

    public static boolean checkedOnBootComplete = false;
    static private final String MMC_STORAGE_ID = "flash";

    static private InfoApp sInstance = null;
    static private CloudStore cloudStore;
    static private LibraryDataHolder libraryDataHolder;
    private PushManager messageManager;
    private EventManager eventManager;

    private DeviceReceiver deviceReceiver = new DeviceReceiver();
    private HashSet<String> mediaFilesSet = new LinkedHashSet<>();

    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
        afterConfig();
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
        IMManager.getInstance().getEventBus().unregister(this);
        EventBus.getDefault().unregister(eventManager);
        super.onTerminate();
    }

    private void afterConfig() {
        EventBus.getDefault().register(eventManager = new EventManager(this));
        cloudContentImportFirstBoot();
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
        boolean supportColor = DeviceConfig.sharedInstance(this).isDeviceSupportColor();
        AppCompatUtils.setColorSupport(supportColor);
        AppCompatImageViewCollection.setAlignView(supportColor);
    }

    private void initEventListener() {
        deviceReceiver.setMediaStateListener(new DeviceReceiver.MediaStateListener() {

            @Override
            public void onMediaScanStarted(Intent intent) {
                if (DeviceConfig.sharedInstance(getApplicationContext()).supportMediaScan()) {
                    processMediaScan();
                }
            }

            @Override
            public void onMediaMounted(Intent intent) {
                Log.w(TAG, "onMediaMounted " + intent.getData().toString());
                if (EnvironmentUtil.isRemovableSDDirectory(getApplicationContext(), intent)) {
                    //processRemovableSDCardScan();
                    processRemovableSDCardContentImport();
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

    private void processRemovableSDCardScan() {
        String sdcardCid = EnvironmentUtil.getRemovableSDCardCid();
        if (StringUtils.isNullOrEmpty(sdcardCid)) {
            return;
        }
        FileSystemScanAction systemScanAction = new FileSystemScanAction(sdcardCid, false);
        systemScanAction.execute(getLibraryDataHolder(), null);
    }

    private void processRemovableSDCardContentImport() {
        String jsonFilePath = DeviceConfig.sharedInstance(getApplicationContext()).getCloudContentImportJsonFilePath();
        if (StringUtils.isNullOrEmpty(jsonFilePath)) {
            return;
        }
        final File file = new File(EnvironmentUtil.getRemovableSDCardDirectory(), jsonFilePath);
        if (!file.exists()) {
            return;
        }
        ContentImportAction importAction = new ContentImportAction(file.getAbsolutePath(), true);
        importAction.execute(getLibraryDataHolder(), null);
    }

    private void processMediaScan() {
        MediaScanAction mediaScanAction = new MediaScanAction(
                DeviceConfig.sharedInstance(getApplicationContext()).getMediaDir(),
                mediaFilesSet, true);
        mediaScanAction.execute(getLibraryDataHolder(), null);
    }

    public void turnOffLed() {
        Device.currentDevice().led(this, false);
    }

    private void initLeanCloud() {
        String leanCloudAppId = DeviceConfig.sharedInstance(this).getLeanCloudApplicationId();
        String leanCloudClientKey = DeviceConfig.sharedInstance(this).getLeanCloudClientKey();
        initIMManager(leanCloudAppId, leanCloudClientKey);
    }

    private void initIMManager(String appId, String clientKey) {
        final IMConfig imInitArgs = new IMConfig(appId, clientKey);
        IMManager.getInstance().init(imInitArgs).startPushService(getApplicationContext());
        IMManager.getInstance().getEventBus().register(this);
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

    public PushManager getPushMessageManager() {
        if (messageManager == null) {
            messageManager = new PushManager(ActionContext.create(getApplicationContext(),
                    getCloudStore().getCloudManager(), getDataManager()));
        }
        return messageManager;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPushMessageEvent(MessageEvent messageEvent) {
        Debug.d(InfoApp.class, String.valueOf(JSONObjectParseUtils.toJson(messageEvent)));
        getPushMessageManager().processMessage(messageEvent.message);
    }

    private void cloudContentImportFirstBoot() {
        if (checkedOnBootComplete || ConfigPreferenceManager.hasImportContent(this)) {
            return;
        }
        String jsonFilePath = DeviceConfig.sharedInstance(getApplicationContext()).getCloudContentImportJsonFilePath();
        if (StringUtils.isNullOrEmpty(jsonFilePath)) {
            return;
        }
        File file = new File(EnvironmentUtil.getExternalStorageDirectory(), jsonFilePath);
        if (!file.exists()) {
            return;
        }
        ContentImportAction importAction = new ContentImportAction(file.getAbsolutePath());
        importAction.execute(getLibraryDataHolder(), null);
    }
}