package com.onyx.android.sdk.common.request;

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
    private volatile WakeLockHolder wakeLockHolder = new WakeLockHolder();
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

    public void acquireWakeLock(final Context context, final String tag) {
        wakeLockHolder.acquireWakeLock(context, tag);
    }

    public void releaseWakeLock() {
        wakeLockHolder.releaseWakeLock();
    }

    public void dumpWakelocks() {
        if (debugWakelock) {
            wakeLockHolder.dumpWakelocks(TAG);
        }
    }

    public void removeRequest(final BaseRequest request) {
        getExecutor().removeRequest(request);
    }

    public void removeRequest(final BaseRequest request, String identifier) {
        getExecutorByIdentifier(identifier).removeRequest(request);
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
