package com.onyx.android.sdk.data;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.utils.CloudConf;
import com.onyx.android.sdk.dataprovider.BuildConfig;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by zhuzeng on 11/21/15. Wrapper for Onyx Cloud Store. Mainly used to
 * retrieve metadata. The data will be downloaded either by image loader or
 * download manager.
 */
public class CloudStore {

    private CloudManager requestManager = new CloudManager();

    public CloudStore() {
    }

    public static void init(final Context appContext) {
        initDatabase(appContext);
        initFileDownloader(appContext);
    }

    public static void terminate() {
        terminateCloudDatabase();
    }

    public boolean submitRequest(final Context context, final BaseCloudRequest request, final BaseCallback callback) {
        return requestManager.submitRequest(context, request, callback);
    }

    public boolean submitRequestToSingle(final Context context, final BaseCloudRequest request, final BaseCallback callback) {
        return requestManager.submitRequestToSingle(context, request, callback);
    }

    public final CloudConf getCloudConf() {
        return requestManager.getCloudConf();
    }

    private static void initDatabase(final Context context) {
        try {
            FlowConfig.Builder builder = new FlowConfig.Builder(context);
            FlowManager.init(builder.build());
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    private static OnyxDownloadManager initFileDownloader(final Context context) {
        OnyxDownloadManager.init(context);
        return OnyxDownloadManager.getInstance();
    }

    private static void terminateCloudDatabase() {
        FlowManager.destroy();
    }

    public CloudManager getCloudManager() {
        return this.requestManager;
    }

    public CloudStore setCloudConf(final CloudConf cloudConf) {
        getCloudManager().setAllCloudConf(cloudConf);
        return this;
    }

    public static CloudManager createCloudManager(final CloudConf cloudConf) {
        return new CloudStore().setCloudConf(cloudConf).getCloudManager();
    }
}
