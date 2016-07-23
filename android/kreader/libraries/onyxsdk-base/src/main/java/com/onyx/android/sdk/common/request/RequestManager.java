package com.onyx.android.sdk.common.request;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhuzeng on 5/31/16.
 */
public class RequestManager {

    private static final String TAG = RequestManager.class.getSimpleName();
    private PowerManager.WakeLock wakeLock;
    private int wakeLockCounting = 0;
    private boolean debugWakelock = false;

    private ExecutorContext singleThreadExecutor;
    private ConcurrentHashMap<String, ExecutorContext> threadPoolMap = new ConcurrentHashMap<>();
    private Handler handler = new Handler(Looper.getMainLooper());

    public RequestManager() {
        initExecutorContext(Thread.NORM_PRIORITY);
    }

    public RequestManager(int priority) {
        initExecutorContext(priority);
    }

    private void initExecutorContext(int priority) {
        singleThreadExecutor = new ExecutorContext(priority);
    }

    private final ExecutorContext getSingleThreadExecutor() {
        return singleThreadExecutor;
    }

    public void acquireWakeLock(Context context) {
        try {
            if (wakeLock == null) {
                PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
                wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Reader: ");
            }
            wakeLock.acquire();
            ++wakeLockCounting;
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }

    public void releaseWakeLock() {
        try {
            if (wakeLock != null) {
                if (wakeLock.isHeld()) {
                    wakeLock.release();
                }
                if (--wakeLockCounting <= 0) {
                    wakeLock = null;
                }
            }
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }

    public void dumpWakelocks() {
        if (debugWakelock) {
            if (wakeLock != null || wakeLockCounting > 0) {
                Log.w(TAG, "wake lock not released. check wake lock." + wakeLock.toString() + " counting: " + wakeLockCounting);
            }
        }
    }

    public void removeRequest(final BaseRequest request) {
        getSingleThreadExecutor().removeRequest(request);
    }

    public Handler getLooperHandler() {
        return handler;
    }

    public boolean submitRequest(final Context context, final BaseRequest request, final Runnable runnable, final BaseCallback callback) {
        if (!beforeSubmitRequest(context, request, callback)) {
            return false;
        }

        if (request.isRunInBackground()) {
            getSingleThreadExecutor().submitToSingleThreadPool(runnable);
        } else {
            runnable.run();
        }
        return true;
    }

    private boolean beforeSubmitRequest(final Context context, final BaseRequest request, final BaseCallback callback) {
        if (request == null) {
            if (callback != null) {
                callback.done(null,  null);
            }
            return false;
        }
        request.setContext(context);
        request.setCallback(callback);
        if (request.isAbortPendingTasks()) {
            getSingleThreadExecutor().abortAllRequests();
        }
        getSingleThreadExecutor().addRequest(request);
        return true;
    }

    public boolean submitRequest(final Context context, final String identifier, final BaseRequest request, final Runnable runnable, final BaseCallback callback) {
        final ExecutorContext executor = getExecutorByIdentifier(identifier);
        if (!beforeSubmitRequestToExecutor(executor, context, request, callback)) {
            return false;
        }

        if (request.isRunInBackground()) {
            executor.submitToSingleThreadPool(runnable);
        } else {
            runnable.run();
        }
        return true;
    }

    private boolean beforeSubmitRequestToExecutor(final ExecutorContext executor, final Context context, final BaseRequest request, final BaseCallback callback) {
        if (request == null) {
            if (callback != null) {
                callback.done(null,  null);
            }
            return false;
        }
        request.setContext(context);
        request.setCallback(callback);
        if (request.isAbortPendingTasks()) {
            executor.abortAllRequests();
        }
        executor.addRequest(request);
        return true;
    }

    private final ExecutorContext getExecutorByIdentifier(final String identifier) {
        if (threadPoolMap.containsKey(identifier)) {
            return threadPoolMap.get(identifier);
        }
        synchronized (threadPoolMap) {
            final ExecutorContext executorContext = new ExecutorContext();
            threadPoolMap.put(identifier, executorContext);
            return executorContext;
        }
    }


}
