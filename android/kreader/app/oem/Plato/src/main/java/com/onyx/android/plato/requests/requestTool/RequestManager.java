package com.onyx.android.plato.requests.requestTool;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;

import com.onyx.android.sdk.device.Device;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhuzeng on 5/31/16.
 */
public class RequestManager {

    private static final String TAG = RequestManager.class.getSimpleName();
    private volatile PowerManager.WakeLock wakeLock;
    private AtomicInteger wakeLockCounting = new AtomicInteger();
    private boolean debugWakelock = false;

    private ExecutorContext executor;
    private ConcurrentHashMap<String, ExecutorContext> threadPoolMap = new ConcurrentHashMap<>();
    private Handler handler = new Handler(Looper.getMainLooper());

    public RequestManager() {
        initExecutorContext(Thread.NORM_PRIORITY);
    }

    public RequestManager(int priority) {
        initExecutorContext(priority);
    }

    private void initExecutorContext(int priority) {
        executor = new ExecutorContext(priority);
    }

    private final ExecutorContext getExecutor() {
        return executor;
    }

    public synchronized void acquireWakeLock(final Context context, final String tag) {
        try {
            if (wakeLock == null) {
                wakeLock = Device.currentDevice().newWakeLock(context, tag);
            }
            if (wakeLock != null) {
                wakeLock.acquire();
                wakeLockCounting.incrementAndGet();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void releaseWakeLock() {
        try {
            if (wakeLock != null) {
                if (wakeLock.isHeld()) {
                    wakeLock.release();
                }
                if (wakeLockCounting.decrementAndGet() <= 0) {
                    wakeLock = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void dumpWakelocks() {
        if (debugWakelock) {
            if (wakeLock != null || wakeLockCounting.get() > 0) {
                Log.w(TAG, "wake lock not released. check wake lock." + wakeLock.toString() + " counting: " + wakeLockCounting.get());
            }
        }
    }

    public void removeRequest(final BaseRequest request) {
        getExecutor().removeRequest(request);
    }

    public Handler getLooperHandler() {
        return handler;
    }

    public boolean submitRequest(final Context context, final BaseRequest request, final Runnable runnable, final BaseCallback callback) {
        if (!beforeSubmitRequest(context, getExecutor(), request, callback)) {
            return false;
        }

        return submitRequestToSingleThreadPoolImpl(getExecutor(), request, runnable);
    }

    private boolean beforeSubmitRequest(final Context context, final ExecutorContext executorContext, final BaseRequest request, final BaseCallback callback) {
        if (request == null) {
            callback.invoke(callback, null, null);
            return false;
        }
        request.setContext(context);
        request.setCallback(callback);
        if (request.isAbortPendingTasks()) {
            executorContext.abortAllRequests();
        }
        executorContext.addRequest(request);
        return true;
    }

    private boolean submitRequestToSingleThreadPoolImpl(final ExecutorContext executorContext, final BaseRequest request, final Runnable runnable) {
        if (request.isRunInBackground()) {
            executorContext.submitToSingleThreadPool(runnable);
        } else {
            runnable.run();
        }
        return true;
    }

    public boolean submitRequest(final Context context, final String identifier, final BaseRequest request, final Runnable runnable, final BaseCallback callback) {
        final ExecutorContext executorOfIdentifier = getExecutorByIdentifier(identifier);
        if (!beforeSubmitRequest(context, executorOfIdentifier, request, callback)) {
            return false;
        }
        return submitRequestToSingleThreadPoolImpl(executorOfIdentifier, request, runnable);
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

    public boolean submitRequestToMultiThreadPool(final Context context, final BaseRequest request, final Runnable runnable, final BaseCallback callback) {
        if (!beforeSubmitRequest(context, getExecutor(), request, callback)) {
            return false;
        }
        getExecutor().submitToMultiThreadPool(runnable);
        return true;
    }


}
