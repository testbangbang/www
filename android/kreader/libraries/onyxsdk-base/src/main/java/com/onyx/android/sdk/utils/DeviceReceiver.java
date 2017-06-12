package com.onyx.android.sdk.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.FileObserver;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.device.EnvironmentUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhuzeng on 9/9/14.

 01-06 11:01:31.500    8083-8083/com.onyx.content.browser I/DeviceReceiver﹕ Received[0]: android.intent.action.MEDIA_UNMOUNTED data file:///mnt/sdcard
 01-06 11:01:31.510    8083-8083/com.onyx.content.browser I/DeviceReceiver﹕ Received[1]: android.intent.action.MEDIA_UNMOUNTED data file:///mnt/sdcard
 01-06 11:01:32.350    8083-8083/com.onyx.content.browser I/DeviceReceiver﹕ Received[2]: android.intent.action.MEDIA_UNMOUNTED data file:///mnt/sdcard
 01-06 11:01:32.400    8083-8083/com.onyx.content.browser I/DeviceReceiver﹕ Received[3]: android.intent.action.MEDIA_UNMOUNTED data file:///mnt/sdcard
 01-06 11:01:32.420    8083-8083/com.onyx.content.browser I/DeviceReceiver﹕ Received[4]: android.intent.action.MEDIA_SHARED data file:///mnt/sdcard

 01-06 11:01:46.910    8083-8083/com.onyx.content.browser I/DeviceReceiver﹕ Received[5]: android.intent.action.MEDIA_UNMOUNTED data file:///mnt/sdcard
 01-06 11:01:47.030    8083-8083/com.onyx.content.browser I/DeviceReceiver﹕ Received[6]: android.intent.action.MEDIA_UNMOUNTED data file:///mnt/sdcard
 01-06 11:01:47.230    8083-8083/com.onyx.content.browser I/DeviceReceiver﹕ Received[7]: android.intent.action.MEDIA_MOUNTED data file:///mnt/sdcard
 01-06 11:01:48.000    8083-8083/com.onyx.content.browser I/DeviceReceiver﹕ Received[8]: android.intent.action.MEDIA_MOUNTED data file:///mnt/sdcard

 */

public class DeviceReceiver extends BroadcastReceiver {

    private static final String TAG = DeviceReceiver.class.getSimpleName();
    public static final String TRIGGER = "trigger_notification";
    public static final String PARSE_PUSH_NOTIFICATION = "com.onyx.parsePushNotification";

    public static final String WIFI_STATE = "android.net.wifi.STATE_CHANGE";
    public static final String WIFI_STATE_CHANGED = "android.net.wifi.WIFI_STATE_CHANGED";
    public static final String START_ONYX_SETTINGS = "start_onyx_settings";

    public static final String OPEN_DOCUMENT_ACTION = "com.onyx.open";

    public static int count = 0;
    static private FileObserver observer;
    private Map<String, String> storageState = new HashMap<String, String>();

    static public class BootCompleteListener {
        public void onReceivedBootComplete(Intent intent){
        }
    }

    static public class PushNotificationListener {
        public void onReceivedPushNotification(Intent intent){

        }
    }

    static public class WifiStateListener {
        public void onWifiStateChanged(Intent intent){
        }

        public void onWifiConnected(Intent intent){}
    }

    static public class BluetoothStateListener {
        public void onBluetoothStateChanged(Intent intent) {

        }
    }

    static public class UmsStateListener {
        public void onUmsStateChanged(Intent intent){
        }
    }

    static public class MediaStateListener {
        public void onMediaBadRemoval(final Intent intent) {
        }

        public void onMediaMounted(final Intent intent) {
        }

        public void onMediaUnmounted(final Intent intent) {
        }

        public void onMediaRemoved(final Intent intent) {
        }

        public void onMediaChecking(final Intent intent) {
        }

        public void onMediaShared(final Intent intent) {
        }

        public void onMediaScanStarted(final Intent intent) {
        }

        public void onMediaScanFinished(final Intent intent) {
        }
    }

    static public class FileSystemListener {
        public void onFileRemoved(final String path) {
        }

        public void onFileUpdated(final String path) {
        }

        public void onFileAdded(final String path) {
        }

        public void onFileMoved(final String path){
        }
    }

    static public class SettingsListener {
        public void onSystemSettingsClicked(Intent intent) {
        }
    }

    static public class OpenDocumentListener {
        public void onOpenDocumentAction(Intent intent, final String path) {
        }
    }

    static public class LocaleChangedListener {
        public void onLocaleChanged() {
        }
    }

    private BootCompleteListener bootCompleteListener;
    private PushNotificationListener pushNotificationListener;
    private WifiStateListener wifiStateListener;
    private UmsStateListener umsStateListener;
    private MediaStateListener mediaStateListener;
    private FileSystemListener fileSystemListener;
    private SettingsListener settingsListener;
    private OpenDocumentListener openDocumentListener;
    private LocaleChangedListener localeChangedListener;
    private BluetoothStateListener bluetoothStateListener;

    public void initReceiver(Context context) {
        enable(context, true);
    }

    public IntentFilter fileFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction(PARSE_PUSH_NOTIFICATION);
        filter.addAction(WIFI_STATE);
        filter.addAction(WIFI_STATE_CHANGED);
        filter.addAction(Intent.ACTION_UMS_CONNECTED);
        filter.addAction(Intent.ACTION_UMS_DISCONNECTED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_MEDIA_CHECKING);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_MEDIA_SHARED);
        filter.addDataScheme("file");

        return filter;
    }

    public IntentFilter systemFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction(PARSE_PUSH_NOTIFICATION);
        filter.addAction(WIFI_STATE);
        filter.addAction(WIFI_STATE_CHANGED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        return filter;
    }

    public IntentFilter settingsFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(TRIGGER);
        filter.addAction(START_ONYX_SETTINGS);
        return filter;
    }

    public IntentFilter openDocumentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(OPEN_DOCUMENT_ACTION);
        return filter;
    }

    // should re-create the observer.
    // the observer is not recursive, so have to create new observer for each folder
    private void startFileObserver() {
        if (observer != null) {
            observer.stopWatching();
        }
        observer = new FileObserver("/mnt/sdcard/Books") {
            @Override
            public void onEvent(int i, String s) {
                Log.i(TAG, "Received file changed event: " + i + " "  + s);
            }

        };
        observer.startWatching();
        Log.i(TAG, "start file observer");
    }

    // it's necessary to install default callback, otherwise, apps will
    // not receive push notifcation.
    public void installPushCallback(Context context) {
    }

    public void enable(Context context, boolean enable) {
        try {
            if (enable) {
                context.registerReceiver(this, fileFilter());
                context.registerReceiver(this, settingsFilter());
                context.registerReceiver(this, systemFilter());
                context.registerReceiver(this, openDocumentFilter());
                installPushCallback(context);
            } else {
                context.unregisterReceiver(this);
            }
        } catch (Exception e) {
        }
        storageState.clear();
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        Log.i(TAG, "Received[" + count + "]: " + intent.getAction() + " data " + intent.getData());
        ++count;

        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            onReceiveBootComplete(intent);
        } else if (action.equals(PARSE_PUSH_NOTIFICATION)) {
            onReceiveParsePushNotification(intent);
        } else if (action.equals(WIFI_STATE) || action.equals(WIFI_STATE_CHANGED)) {
            onWifiStateChanged(intent);
            ConnectivityManager connectMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiNetInfo = connectMgr
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifiNetInfo != null && wifiNetInfo.isConnected()) {
                onWifiConnected(intent);
            }
        } else if (action.equals(Intent.ACTION_UMS_CONNECTED) || action.equals(Intent.ACTION_UMS_DISCONNECTED)) {
            onUmsStateChanged(intent);
        } else if (action.equals(Intent.ACTION_POWER_CONNECTED) || action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
            onPowerStateChanged(intent);
        } else if (action.equals(Intent.ACTION_MEDIA_BAD_REMOVAL)) {
            onMediaBadRemoval(intent);
        } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            onMediaMounted(intent);
        } else if (action.equals(Intent.ACTION_MEDIA_REMOVED)) {
            onMediaRemoved(intent);
        } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
            onMediaUnmounted(intent);
        } else if (action.equals(Intent.ACTION_MEDIA_CHECKING)) {
            onMediaChecking(intent);
        } else if (action.equals(Intent.ACTION_MEDIA_SHARED)) {
            onMediaShared(intent);
        } else if (action.equals(Intent.ACTION_MEDIA_SCANNER_STARTED)) {
            onMediaScanStarted(intent);
        } else if (action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
            onMediaScanFinished(intent);
        } else if (action.equals(Intent.ACTION_MEDIA_BAD_REMOVAL) || action.equals(Intent.ACTION_MEDIA_MOUNTED) || action.equals(Intent.ACTION_MEDIA_REMOVED)) {
            onMediaStateChanged(intent);
        } else if (action.equals(START_ONYX_SETTINGS)) {
            onStartSettings(intent);
        } else if (action.equals(OPEN_DOCUMENT_ACTION)) {
            onOpenDocumentAction(intent);
        } else if (action.equals(Intent.ACTION_LOCALE_CHANGED)) {
            onLocaleChanged();
        }
    }

    public void setBootCompleteListener(final BootCompleteListener l) {
        bootCompleteListener = l;
    }

    public void setPushNotificationListener(final PushNotificationListener l) {
        pushNotificationListener = l;
    }

    public void setWifiStateListener(final WifiStateListener l) {
        wifiStateListener = l;
    }

    public void setUmsStateListener(final UmsStateListener l) {
        umsStateListener = l;
    }

    public void setMediaStateListener(final MediaStateListener l) {
        mediaStateListener = l;
    }

    public void setSettingsListener(final SettingsListener l) {
        settingsListener = l;
    }

    public void setOpenDocumentListener(final OpenDocumentListener l) {
        openDocumentListener = l;
    }

    public void setLocaleChangedListener(final LocaleChangedListener l) {
        localeChangedListener = l;
    }

    public void setBluetoothStateListener(final BluetoothStateListener l) {
        bluetoothStateListener = l;
    }


    public void onReceiveBootComplete(Intent intent) {
        if (bootCompleteListener != null) {
            bootCompleteListener.onReceivedBootComplete(intent);
        }
    }

    public void onReceiveParsePushNotification(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            Log.i(TAG, "ignore null bundle");
        }
        final String data = bundle.getString("com.parse.Data");
        Map<String, Object> map = JSON.parseObject(data, Map.class);
        Log.i(TAG, "map: " + map.toString());

        if (pushNotificationListener != null) {
            pushNotificationListener.onReceivedPushNotification(intent);
        }
    }

    public void onWifiStateChanged(Intent intent) {
        if (wifiStateListener != null) {
            wifiStateListener.onWifiStateChanged(intent);
        }
    }

    public void onWifiConnected(Intent intent){
        if (wifiStateListener != null){
            wifiStateListener.onWifiConnected(intent);
        }
    }

    public void onUmsStateChanged(Intent intent) {
        if (umsStateListener != null) {
            umsStateListener.onUmsStateChanged(intent);
        }
    }

    public void onPowerStateChanged(Intent intent) {
    }

    public void onMediaBadRemoval(Intent intent) {
        if (mediaStateListener != null) {
            mediaStateListener.onMediaBadRemoval(intent);
        }
    }

    public void onMediaMounted(Intent intent) {
        if (mediaStateListener != null) {
            mediaStateListener.onMediaMounted(intent);
        }
    }

    public void onMediaRemoved(Intent intent) {
        if (mediaStateListener != null) {
            mediaStateListener.onMediaRemoved(intent);
        }
    }

    public void onMediaUnmounted(Intent intent) {
        if (mediaStateListener != null) {
            mediaStateListener.onMediaUnmounted(intent);
        }
    }

    public void onMediaChecking(Intent intent) {
        if (mediaStateListener != null) {
            mediaStateListener.onMediaChecking(intent);
        }
    }

    public void onMediaShared(Intent intent) {
        if (mediaStateListener != null) {
            mediaStateListener.onMediaShared(intent);
        }
    }

    public void onMediaScanStarted(Intent intent) {
        if (mediaStateListener != null) {
            mediaStateListener.onMediaScanStarted(intent);
        }
    }

    public void onMediaScanFinished(Intent intent) {
        if (mediaStateListener != null) {
            mediaStateListener.onMediaScanFinished(intent);
        }
    }

    public void onMediaStateChanged(Intent intent) {
        if (mediaStateListener != null) {
            mediaStateListener.onMediaScanStarted(intent);
        }
    }

    public void setStorageState(final String mount, final String state) {
        storageState.put(mount, state);
    }

    public String getStorageState(final String mount) {
        if (storageState == null) {
            return null;
        }
        return storageState.get(mount);
    }

    static public boolean isExternalStorageEvent(final Context context, Intent intent) {
        final String string = FileUtils.getRealFilePathFromUri(context, intent.getData());
        if (EnvironmentUtil.getExternalStorageDirectory().getAbsolutePath().contains(string)) {
            return true;
        }
        return false;
    }

    public boolean isStorageShared(final String mount) {
        final String value = storageState.get(mount);
        if (StringUtils.isNullOrEmpty(value)) {
            return false;
        }
        return value.equals(Intent.ACTION_MEDIA_SHARED);
    }

    public void onStartSettings(Intent intent) {
        if (settingsListener != null) {
            settingsListener.onSystemSettingsClicked(intent);
        }
    }

    public void onOpenDocumentAction(Intent intent) {
        if (openDocumentListener != null) {
            String path = intent.getStringExtra("path");
            openDocumentListener.onOpenDocumentAction(intent, path);
        }
    }

    public void onLocaleChanged() {
        if (localeChangedListener != null) {
            localeChangedListener.onLocaleChanged();   
        }
    }

    /**
     * if there is any storage still in shared state. the shared state is used to ignore
     * reloading request when mount/umount.
     * @return
     */
    public boolean isAnyStorageShared() {
        return isStorageShared(EnvironmentUtil.getExternalStorageDirectory().getAbsolutePath()) ||
                isStorageShared(EnvironmentUtil.getRemovableSDCardDirectory().getAbsolutePath());
    }

    public void updateStorageState(final Context context, final Intent intent) {
        final String string = FileUtils.getRealFilePathFromUri(context, intent.getData());
        setStorageState(string, intent.getAction());
    }

}
