package com.onyx.kreader;

import android.app.Application;
import android.content.Context;

import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.manager.OssManager;
import com.onyx.android.sdk.data.manager.WeChatManager;

/**
 * Created by suicheng on 2017/2/13.
 */

public class ReaderApplication extends Application {

    static public final String OSS_PUSH_KEY_ID = "LTAIn6vAi9dxw9IS";
    static public final String OSS_PUSH_KEY_SECRET = "zagMGgELcUpWwaxXkQPoUNcJBwBYRT";
    static public final String OSS_PUSH_BUCKET = "onyx-content";
    static public final String OSS_PUSH_ENDPOINT = "http://content.onyx-international.cn";

    static public final String WX_APP_ID = "wx86d74b4e0a1d83d0";
    static public final String WX_APP_SECRETE = "4bd220f283d7858ad4e071c0ea998d6b";

    static private ReaderApplication sInstance = null;
    static private CloudStore cloudStore = new CloudStore();

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        initConfig();
    }

    private void initConfig() {
        try {
            initDownloadManager();
            initWeChatManager();
        } catch (Exception e) {
        }
    }

    private void initDownloadManager() {
        CloudStore.initFileDownloader(sInstance);
    }

    private void initWeChatManager() {
        WeChatManager.sharedInstance(sInstance, WX_APP_ID, WX_APP_SECRETE);
    }

    public static CloudStore getCloudStore() {
        if (cloudStore == null) {
            cloudStore = new CloudStore();
        }
        return cloudStore;
    }

    public static ReaderApplication instance() {
        return sInstance;
    }

    public static OssManager getPushOssManger(Context context) {
        OssManager.OssConfig ossConfig = new OssManager.OssConfig();
        ossConfig.setBucketName(OSS_PUSH_BUCKET);
        ossConfig.setEndPoint(OSS_PUSH_ENDPOINT);
        ossConfig.setKeyId(OSS_PUSH_KEY_ID);
        ossConfig.setKeySecret(OSS_PUSH_KEY_SECRET);
        return new OssManager(context.getApplicationContext(), ossConfig);
    }
}
