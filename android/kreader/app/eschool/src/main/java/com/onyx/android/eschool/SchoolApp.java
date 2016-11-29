package com.onyx.android.eschool;

import android.app.Application;

import com.onyx.android.eschool.utils.StudentPreferenceManager;
import com.onyx.android.sdk.data.CloudStore;

/**
 * Created by zhuzeng on 14/11/2016.
 */
public class SchoolApp extends Application {

    static private SchoolApp sInstance = null;
    static private CloudStore cloudStore = new CloudStore();

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            sInstance = this;
            StudentPreferenceManager.init(this);
            initCloudStoreConfig();
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
}
