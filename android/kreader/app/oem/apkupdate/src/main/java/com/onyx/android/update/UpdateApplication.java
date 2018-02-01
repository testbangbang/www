package com.onyx.android.update;

import android.app.Application;
import android.content.Context;

import com.onyx.android.sdk.data.manager.OssManager;

/**
 * Created by huxiaomao on 17/10/23.
 */
public class UpdateApplication extends Application {

    static public final String OSS_LOG_KEY_ID = "LTAIXvqBXTJUKEf0";
    static public final String OSS_LOG_KEY_SECRET = "tKRDXDOPGBm9wK0GHHaJG2HaqfKWbY";
    static public final String OSS_LOG_BUCKET = "onyx-log-collection";
    static public final String OSS_LOG_ENDPOINT = "http://onyx-log-collection.onyx-international.cn";

    private static UpdateApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static OssManager getLogOssManger(Context context) {
        OssManager.OssConfig ossConfig = new OssManager.OssConfig();
        ossConfig.setBucketName(OSS_LOG_BUCKET);
        ossConfig.setEndPoint(OSS_LOG_ENDPOINT);
        ossConfig.setKeyId(OSS_LOG_KEY_ID);
        ossConfig.setKeySecret(OSS_LOG_KEY_SECRET);
        return new OssManager(context.getApplicationContext(), ossConfig);
    }

    public static UpdateApplication singleInstance() {
        return sInstance;
    }
}
