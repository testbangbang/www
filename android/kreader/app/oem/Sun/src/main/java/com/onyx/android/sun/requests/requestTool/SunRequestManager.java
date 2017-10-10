package com.onyx.android.sun.requests.requestTool;

import android.content.Context;


/**
 * Created by huxiaomao on 2016/11/28.
 */

public class SunRequestManager {
    private RequestManager requestManager = null;
    private boolean isUseMultiThreadPool = true;
    private static class SingletonFactory {
        private static SunRequestManager jdRequestManager = new SunRequestManager();
    }

    public static SunRequestManager getInstance() {
        return SingletonFactory.jdRequestManager;
    }

    private SunRequestManager() {
        init();
    }

    private void init(){
        requestManager = new RequestManager();
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

    public void acquireWakeLock(final Context context, final String tag) {
        requestManager.acquireWakeLock(context, tag);
    }

    public void releaseWakeLock(final String tag) {
        requestManager.releaseWakeLock();
    }

    private final Runnable generateRunnable(final BaseRequest request) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    request.beforeExecute(SunRequestManager.this);
                    request.execute(SunRequestManager.this);
                } catch (Throwable tr) {
                    request.setException(tr);
                } finally {
                    request.afterExecute(SunRequestManager.this);
                    requestManager.dumpWakelocks();
                    requestManager.removeRequest(request);
                }
            }
        };
        return runnable;
    }

    public boolean submitRequest(final Context context, final BaseRequest request, final BaseCallback callback) {
        final Runnable runnable = generateRunnable(request);
        if(isUseMultiThreadPool){
            return requestManager.submitRequestToMultiThreadPool(context, request, runnable, callback);
        }
        return requestManager.submitRequest(context, request, runnable, callback);
    }
}
