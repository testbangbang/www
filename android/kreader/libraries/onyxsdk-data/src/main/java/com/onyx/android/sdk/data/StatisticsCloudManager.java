package com.onyx.android.sdk.data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.request.cloud.BaseStatisticsRequest;
import com.onyx.android.sdk.data.utils.CloudConf;
import com.onyx.android.sdk.dataprovider.BuildConfig;
import com.onyx.android.sdk.utils.LocaleUtils;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by ming on 2017/2/17.
 */
public class StatisticsCloudManager {
    private RequestManager requestManager;

    public StatisticsCloudManager() {
        requestManager = new RequestManager(Thread.NORM_PRIORITY);
    }

    public void acquireWakeLock(final Context context, final String tag) {
        requestManager.acquireWakeLock(context, tag);
    }

    public void releaseWakeLock() {
        requestManager.releaseWakeLock();
    }

    private final Runnable generateRunnable(final BaseStatisticsRequest request) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    request.beforeExecute(StatisticsCloudManager.this);
                    request.execute(StatisticsCloudManager.this);
                } catch (Throwable tr) {
                    request.setException(tr);
                } finally {
                    request.afterExecute(StatisticsCloudManager.this);
                    requestManager.dumpWakelocks();
                    requestManager.removeRequest(request);
                }
            }
        };
        return runnable;
    }

    public boolean submitRequest(final Context context, final BaseStatisticsRequest request, final BaseCallback callback) {
        final Runnable runnable = generateRunnable(request);
        return requestManager.submitRequest(context, request, runnable, callback);
    }

    public Handler getLooperHandler() {
        return requestManager.getLooperHandler();
    }
}
