package com.onyx.android.libsetting;

import android.content.Context;

import com.onyx.android.libsetting.request.BaseSettingRequest;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.RequestManager;

/**
 * Created by solskjaer49 on 2017/2/11 18:44.
 */

public class SettingManager {
    public RequestManager getRequestManager() {
        return requestManager;
    }

    private RequestManager requestManager;
    private static SettingManager instance;

    private SettingManager() {
        requestManager = new RequestManager(Thread.NORM_PRIORITY);
    }

    static public SettingManager sharedInstance() {
        if (instance == null) {
            instance = new SettingManager();
        }
        return instance;
    }

    private Runnable generateRunnable(final BaseSettingRequest request) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    request.beforeExecute(SettingManager.this);
                    request.execute(SettingManager.this);
                } catch (Throwable tr) {
                    request.setException(tr);
                } finally {
                    request.afterExecute(SettingManager.this);
                    requestManager.dumpWakelocks();
                    requestManager.removeRequest(request);
                }
            }
        };
        return runnable;
    }

    public boolean submitRequest(final Context context, final BaseSettingRequest request, final BaseCallback callback) {
        return requestManager.submitRequestToMultiThreadPool(context, request, generateRunnable(request), callback);
    }
}
