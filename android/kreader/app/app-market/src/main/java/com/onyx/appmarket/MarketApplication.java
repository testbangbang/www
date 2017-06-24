package com.onyx.appmarket;

import android.app.Application;

import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.OnyxDownloadManager;

/**
 * Created by suicheng on 2017/2/27.
 */
public class MarketApplication extends Application {
    static public MarketApplication sInstance;
    static private CloudStore cloudStore;

    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
    }

    private void initConfig() {
        try {
            sInstance = this;
            initDownloadManager();
            initStoreDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDownloadManager() {
        OnyxDownloadManager.init(sInstance.getApplicationContext());
        OnyxDownloadManager.getInstance();
    }

    private void initStoreDatabase() {
        CloudStore.init(sInstance.getApplicationContext());
    }

    public static CloudStore getCloudStore() {
        if (cloudStore == null) {
            cloudStore = new CloudStore();
        }
        return cloudStore;
    }
}
