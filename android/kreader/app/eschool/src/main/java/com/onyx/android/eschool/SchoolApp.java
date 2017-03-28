package com.onyx.android.eschool;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import com.onyx.android.eschool.device.DeviceConfig;
import com.onyx.android.eschool.utils.StudentPreferenceManager;
import com.onyx.android.libsetting.SettingConfig;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.manager.OTAManager;
import com.onyx.android.sdk.data.manager.OssManager;
import com.onyx.android.sdk.data.request.data.FileCollectionRequest;
import com.onyx.android.sdk.data.request.data.MediaDeletedFileRemoveRequest;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.ui.compat.AppCompatImageViewCollection;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.DeviceReceiver;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.MimeTypeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zhuzeng on 14/11/2016.
 */
public class SchoolApp extends Application {
    static private final String TAG = SchoolApp.class.getSimpleName();
    static public final String ERROR_REPORT_ACTION = "onyx.eschool.intent.action.ERROR_REPORT";

    static public final String OSS_LOG_KEY_ID = "LTAIXvqBXTJUKEf0";
    static public final String OSS_LOG_KEY_SECRET = "tKRDXDOPGBm9wK0GHHaJG2HaqfKWbY";
    static public final String OSS_LOG_BUCKET = "onyx-log-collection";
    static public final String OSS_LOG_ENDPOINT = "http://onyx-log-collection.onyx-international.cn";

    static private SchoolApp sInstance = null;
    static private DataManager dataManager;

    DeviceReceiver receiver = new DeviceReceiver();
    private HashSet<String> mediaFilesSet = new LinkedHashSet<>();

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
            initDeviceReceiver();
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

    private void initDeviceReceiver() {
        receiver.setMediaStateListener(new DeviceReceiver.MediaStateListener() {

            public void onMediaScanStarted(final Intent intent) {
                if (DeviceConfig.sharedInstance(sInstance).supportMediaScan()) {
                    scanMedia(true);
                }
            }
        });
        receiver.enable(sInstance.getApplicationContext(), true);
    }

    public void turnOffLed() {
        Device.currentDevice().led(this, false);
    }

    @Override
    public void onTerminate() {
        terminateCloudDatabase();
        receiver.enable(this, false);
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

    public void enableMediaScanner(final Context context, boolean enable) {
        Intent intent = new Intent();
        intent.setAction("onyx.media.scanner.control");
        intent.putExtra("enable", enable);
        context.sendBroadcast(intent);
    }

    private List<String> generatePath(final List<String> fileName) {
        List<String> list = new ArrayList<>();
        for (String s : fileName) {
            File path = new File(EnvironmentUtil.getExternalStorageDirectory(), s);
            if (path.exists()) {
                list.add(path.getAbsolutePath());
            }
            path = new File(EnvironmentUtil.getRemovableSDCardDirectory(), s);
            if (path.exists()) {
                list.add(path.getAbsolutePath());
            }
        }
        return list;
    }

    private Set<String> mediaContentTypes() {
        Set<String> set = new HashSet<>();
        set.addAll(MimeTypeUtils.getImageExtension());
        set.addAll(MimeTypeUtils.getAudioExtension());
        return set;
    }

    public void scanMedia(boolean forceFullUpdate) {
        final List<String> musicDir = DeviceConfig.sharedInstance(this).getMusicDir();
        final List<String> imageDir = DeviceConfig.sharedInstance(this).getGalleryDir();
        List<String> list = new ArrayList<>();
        if (!CollectionUtils.isNullOrEmpty(musicDir)) {
            list.addAll(musicDir);
        }
        if (!CollectionUtils.isNullOrEmpty(imageDir)) {
            list.addAll(imageDir);
        }
        scanMediaFolders(list, forceFullUpdate);
    }

    private void scanMediaFolders(final List<String> dir, final boolean forceFullUpdate) {
        Log.w(TAG, "collect media files: " + dir.toString());
        final FileCollectionRequest collectionRequest = new FileCollectionRequest(generatePath(dir), mediaContentTypes());
        collectionRequest.setAbortPendingTasks(false);
        getDataManager().submit(sInstance.getApplicationContext(), collectionRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (CollectionUtils.isNullOrEmpty(collectionRequest.getResultFileList())) {
                    enableMediaScanner(sInstance.getApplicationContext(), false);
                    return;
                }
                buildScanListAndSendScanAction(collectionRequest.getResultFileList(), forceFullUpdate);
            }
        });
    }

    private void buildScanListAndSendScanAction(final List<String> fileList, boolean forceFullUpdate) {
        List<String> removeList = new ArrayList<>();
        Iterator<String> iterator = mediaFilesSet.iterator();
        while (iterator.hasNext()) {
            String path = iterator.next();
            if (!FileUtils.fileExist(path)) {
                removeList.add(path);
                iterator.remove();
                forceFullUpdate = true;
            }
        }

        for (String path : fileList) {
            if (mediaFilesSet.add(path)) {
                forceFullUpdate = true;
            }
        }

        if (removeList.size() > 0) {
            cleanNonExistentMediaFiles(removeList);
        }

        if (forceFullUpdate) {
            scanFileByMediaScanner();
        }
    }

    private void cleanNonExistentMediaFiles(final List<String> list) {
        if (CollectionUtils.isNullOrEmpty(list)) {
            return;
        }
        MediaDeletedFileRemoveRequest deletedFileRemoveRequest = new MediaDeletedFileRemoveRequest(list);
        deletedFileRemoveRequest.setAbortPendingTasks(false);
        getDataManager().submit(sInstance.getApplicationContext(), deletedFileRemoveRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    list.clear();
                }
            }
        });
    }

    private void scanFileByMediaScanner() {
        final String[] fileArray = mediaFilesSet.toArray(new String[mediaFilesSet.size()]);
        MediaScannerConnection.scanFile(sInstance.getApplicationContext(), fileArray, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i(TAG, "Scan finished: " + path);
                    }
                });
    }
}
