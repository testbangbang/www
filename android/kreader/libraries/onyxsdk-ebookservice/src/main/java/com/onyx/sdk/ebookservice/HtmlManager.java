package com.onyx.sdk.ebookservice;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.sdk.ebookservice.request.BaseHtmlRequest;

/**
 * Created by suicheng on 2017/2/13.
 */

public class HtmlManager {

    private RequestManager requestManager;

    public HtmlManager() {
        requestManager = new RequestManager(Thread.NORM_PRIORITY);
    }


    public void acquireWakeLock(final Context context, final String tag) {
        requestManager.acquireWakeLock(context, tag);
    }

    public void releaseWakeLock() {
        requestManager.releaseWakeLock();
    }

    private final Runnable generateRunnable(final BaseHtmlRequest request) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    request.beforeExecute(HtmlManager.this);
                    request.execute(HtmlManager.this);
                } catch (Throwable tr) {
                    request.setException(tr);
                } finally {
                    request.afterExecute(HtmlManager.this);
                    requestManager.dumpWakelocks();
                    requestManager.removeRequest(request);
                }
            }
        };
        return runnable;
    }

    public boolean submitRequest(final Context context, final BaseHtmlRequest request, final BaseCallback callback) {
        final Runnable runnable = generateRunnable(request);
        return requestManager.submitRequestToMultiThreadPool(context, request, runnable, callback);
    }

    public Handler getLooperHandler() {
        return requestManager.getLooperHandler();
    }

    public static boolean isWifiConnected(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi == null) {
            return false;
        }
        return wifi.isConnected();
    }
}
