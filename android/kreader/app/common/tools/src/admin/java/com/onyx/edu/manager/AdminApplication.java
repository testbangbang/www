package com.onyx.edu.manager;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.utils.CloudConf;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.data.v2.ContentService;
import com.onyx.edu.manager.manager.ContentManager;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by suicheng on 2017/6/16.
 */
public class AdminApplication extends MultiDexApplication {

    private static CloudManager cloudManager;
    private static AdminApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        initConfig();
    }

    private void initConfig() {
        try {
            initDatabase(this);
            ContentManager.init(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(AdminApplication.this);
    }

    public static final AdminApplication sInstance() {
        return sInstance;
    }

    public static CloudManager getCloudManager() {
        if (cloudManager == null) {
            CloudConf cloudConf = new CloudConf(
                    Constant.CLOUD_MAIN_INDEX_SERVER_HOST,
                    Constant.CLOUD_MAIN_INDEX_SERVER_API,
                    Constant.DEFAULT_CLOUD_STORAGE);
            cloudManager = CloudStore.createCloudManager(cloudConf);
        }
        return cloudManager;
    }

    public static void updateCloudManagerToken(String newToken) {
        CloudManager cloudManager = AdminApplication.getCloudManager();
        cloudManager.setToken(newToken);
        ServiceFactory.addRetrofitTokenHeader(cloudManager.getCloudConf().getApiBase(),
                Constant.HEADER_AUTHORIZATION,
                ContentService.CONTENT_AUTH_PREFIX + cloudManager.getToken());
    }

    private void initDatabase(final Context context) {
        try {
            FlowConfig.Builder builder = new FlowConfig.Builder(context);
            FlowManager.init(builder.build());
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
    }
}
