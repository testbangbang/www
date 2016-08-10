package com.onyx.cloud;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.cloud.service.OnyxAccountService;
import com.onyx.cloud.store.request.BaseCloudRequest;
import retrofit2.Retrofit;

/**
 * Created by zhuzeng on 8/10/16.
 */
public class CloudManager {

    private RequestManager requestManager;

    public CloudManager() {
        requestManager = new RequestManager();
    }

    public void acquireWakeLock(Context context) {
        requestManager.acquireWakeLock(context);
    }

    public void releaseWakeLock() {
        requestManager.releaseWakeLock();
    }

    private final Runnable generateRunnable(final BaseCloudRequest request) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    request.beforeExecute(CloudManager.this);
                    request.execute(CloudManager.this);
                } catch (Throwable tr) {
                    request.setException(tr);
                } finally {
                    request.afterExecute(CloudManager.this);
                    requestManager.dumpWakelocks();
                    requestManager.removeRequest(request);
                }
            }
        };
        return runnable;
    }

    public boolean submitRequest(final Context context, final BaseCloudRequest request, final BaseCallback callback) {
        final Runnable runnable = generateRunnable(request);
        return requestManager.submitRequest(context, request, runnable, callback);
    }

    public Handler getLooperHandler() {
        return requestManager.getLooperHandler();
    }

    public static boolean isWifiConnected(final Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi == null) {
            return false;
        }
        return wifi.isConnected();
    }

    public final OnyxAccountService getAccountService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://dev.onyx-international.cn/api/1")
                .build();
        return retrofit.create(OnyxAccountService.class);
    }

}
