package com.onyx.android.eschool;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.onyx.android.eschool.device.DeviceConfig;
import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.eschool.utils.StudentPreferenceManager;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.data.request.data.db.FilesAddToMetadataRequest;
import com.onyx.android.sdk.data.request.data.db.FilesDiffFromMetadataRequest;
import com.onyx.android.sdk.data.request.data.db.MetadataRequest;
import com.onyx.android.sdk.data.request.data.db.FilesRemoveFromMetadataRequest;
import com.onyx.android.sdk.data.request.data.fs.FileSystemScanRequest;
import com.onyx.android.sdk.data.utils.CloudConf;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.ui.compat.AppCompatImageViewCollection;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.DeviceReceiver;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.TestUtils;
import com.raizlabs.android.dbflow.sql.language.Condition;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by zhuzeng on 14/11/2016.
 */
public class SchoolApp extends Application {
    static private final String MMC_STORAGE_ID = "flash";
    static public final String E_BOOK_HOST = "http://192.168.11.104:8082/";
    static public final String E_BOOK_API = "http://192.168.11.104:8082/api/";

    static private SchoolApp sInstance = null;
    static private CloudStore cloudStore = new CloudStore();
    static private CloudStore schoolCloudStore;
    static private LibraryDataHolder libraryDataHolder;

    private DeviceReceiver deviceReceiver = new DeviceReceiver();

    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
    }

    @Override
    public void onTerminate() {
        terminateCloudDatabase();
        deviceReceiver.enable(this, false);
        super.onTerminate();
    }

    private void initConfig() {
        try {
            sInstance = this;
            StudentPreferenceManager.init(this);
            initCloudStoreConfig();
            initDeviceConfig();
            initEventListener();
            initFrescoLoader();
            initSystemInBackground();
            startFileSystemScan();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    private List<String> getBookDirList(boolean isFlash) {
        List<String> dirList = new ArrayList<>();
        if (isFlash) {
            dirList.add(EnvironmentUtil.getExternalStorageDirectory().getAbsolutePath());
        } else {
            dirList.add(EnvironmentUtil.getRemovableSDCardDirectory().getAbsolutePath());
        }
        return dirList;
    }

    private void startFileSystemScan() {
        startFileSystemScan(MMC_STORAGE_ID, getBookDirList(true));
        if (StringUtils.isNullOrEmpty(getSdcardCid())) {
            return;
        }
        startFileSystemScan(getSdcardCid(), getBookDirList(false));
    }

    private void startFileSystemScan(final String storageId, List<String> bookDirList) {
        final FileSystemScanRequest fileSystemScanRequest = new FileSystemScanRequest(storageId, bookDirList, true);
        fileSystemScanRequest.setExtensionFilterSet(TestUtils.defaultContentTypes());
        getDataManager().submit(getApplicationContext(), fileSystemScanRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                HashSet<String> hashSet = fileSystemScanRequest.getResult();
                Iterator<String> iterator = hashSet.iterator();
                while (iterator.hasNext()) {
                    Log.e("##scanFile", String.valueOf(iterator.next()));
                }
                String tmp = storageId;
                if (tmp.equals(MMC_STORAGE_ID)) {
                    tmp = null;
                }
                startMetadataPathScan(tmp, hashSet);
            }
        });
    }

    private void startMetadataPathScan(final String storageId, final HashSet<String> filePathSet) {
        QueryArgs queryArgs = QueryBuilder.allBooksQuery(SortBy.CreationTime, SortOrder.Desc);
        queryArgs.propertyList.add(Metadata_Table.nativeAbsolutePath);
        Condition condition;
        if (StringUtils.isNullOrEmpty(storageId)) {
            condition = Metadata_Table.storageId.isNull();
        } else {
            condition = Metadata_Table.storageId.eq(storageId);
        }
        queryArgs.conditionGroup.and(condition);
        final MetadataRequest metadataRequest = new MetadataRequest(queryArgs);
        getDataManager().submit(getApplicationContext(), metadataRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                processMetaSnapshot(storageId, filePathSet, metadataRequest.getPathList());
            }
        });
    }

    private void processMetaSnapshot(final String storageId, HashSet<String> fileList, HashSet<String> snapshotList) {
        final FilesDiffFromMetadataRequest filesFromMetadataRequest = new FilesDiffFromMetadataRequest(
                fileList, snapshotList);
        getDataManager().submit(getApplicationContext(), filesFromMetadataRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                HashSet<String> addedSet = filesFromMetadataRequest.getDiffSet();
                Log.e("##addedSet", JSON.toJSONString(addedSet));
                if (CollectionUtils.isNullOrEmpty(addedSet)) {
                    return;
                }
                addFilesToMetaData(storageId, addedSet);
            }
        });
    }

    private void addFilesToMetaData(String storageId, Set<String> addedSet) {
        FilesAddToMetadataRequest addRequest = new FilesAddToMetadataRequest(storageId, addedSet);
        getDataManager().submitToMulti(getApplicationContext(), addRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                getDataManager().getCacheManager().clearMetadataCache();
            }
        });
    }

    private void removeFilesFromMetadata(Set<String> removedSet) {
        FilesRemoveFromMetadataRequest removeRequest = new FilesRemoveFromMetadataRequest(removedSet);
        getDataManager().submitToMulti(getApplicationContext(), removeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                getDataManager().getCacheManager().clearMetadataCache();
            }
        });
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

    private void initEventListener() {
        deviceReceiver.setMediaStateListener(new DeviceReceiver.MediaStateListener() {
            @Override
            public void onMediaMounted(Intent intent) {
                Log.e("##onMediaMounted", intent.getData().toString());
                if (EnvironmentUtil.isRemovableSDDirectory(getApplicationContext(), intent)) {
                    String sdcardCid = EnvironmentUtil.getRemovableSDCardCid();
                    if (StringUtils.isNullOrEmpty(sdcardCid)) {
                        return;
                    }
                    startFileSystemScan(sdcardCid, getBookDirList(false));
                }
            }

            @Override
            public void onMediaUnmounted(Intent intent) {
                Log.e("##onMediaUnmounted", intent.getData().toString());
                if (EnvironmentUtil.isRemovableSDDirectory(getApplicationContext(), intent)) {
                }
            }

            @Override
            public void onMediaBadRemoval(Intent intent) {
                Log.e("##onMediaBadRemoval", intent.getData().toString());
                if (EnvironmentUtil.isRemovableSDDirectory(getApplicationContext(), intent)) {
                }
            }

            @Override
            public void onMediaRemoved(Intent intent) {
                Log.e("##onMediaRemoved", intent.getData().toString());

            }
        });
        deviceReceiver.enable(getApplicationContext(), true);
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

    private void initFrescoLoader() {
        Fresco.initialize(singleton().getApplicationContext());
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

    static public CloudStore getSchoolCloudStore() {
        if (schoolCloudStore == null) {
            schoolCloudStore = new CloudStore();
            CloudManager cloudManager = schoolCloudStore.getCloudManager();
            CloudConf cloudConf = new CloudConf(
                    E_BOOK_HOST,
                    E_BOOK_API,
                    Constant.DEFAULT_CLOUD_STORAGE);
            cloudManager.setChinaCloudConf(cloudConf);
            cloudManager.setGlobalCloudConf(cloudConf);
        }
        return schoolCloudStore;
    }

    static public DataManager getDataManager() {
        return getLibraryDataHolder().getDataManager();
    }

    public static LibraryDataHolder getLibraryDataHolder() {
        if (libraryDataHolder == null) {
            libraryDataHolder = new LibraryDataHolder(sInstance);
        }
        return libraryDataHolder;
    }

    public String getSdcardCid() {
        return EnvironmentUtil.getRemovableSDCardCid();
    }
}