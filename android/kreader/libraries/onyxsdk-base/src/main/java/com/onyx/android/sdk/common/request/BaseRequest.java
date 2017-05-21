package com.onyx.android.sdk.common.request;

import android.content.Context;
import com.onyx.android.sdk.utils.Benchmark;

/**
 * Created by zhuzeng on 5/31/16.
 */
public class BaseRequest {
    private int requestSequence;
    private volatile boolean abort = false;
    private volatile boolean abortPendingTasks = false;
    private boolean useWakeLock = true;
    private boolean runInBackground = true;
    private BaseCallback callback;
    private Context context;
    private Benchmark benchmark;
    private Throwable exception;
    static private volatile int globalRequestSequence;
    static private boolean enableBenchmarkDebug = true;

    static public int generateRequestSequence() {
        globalRequestSequence += 1;
        return globalRequestSequence;
    }

    public BaseRequest() {
        requestSequence = generateRequestSequence();
        abort = false;
        abortPendingTasks = false;
        runInBackground = true;
    }

    public void setContext(final Context c) {
        context = c;
    }

    public void setCallback(final BaseCallback c) {
        callback = c;
    }

    public final BaseCallback getCallback() {
        return callback;
    }

    public int getRequestSequence() {
        return requestSequence;
    }

    public final Context getContext() {
        return context;
    }

    public void setAbort() {
        abort = true;
    }

    public boolean isAbort() {
        return abort;
    }

    public void setAbortPendingTasks(boolean abort) {
        abortPendingTasks = abort;
    }

    public boolean isAbortPendingTasks() {
        return abortPendingTasks;
    }

    public void setUseWakeLock(boolean use) {
        useWakeLock = use;
    }

    public boolean isUseWakeLock() {
        return useWakeLock;
    }

    static public boolean isEnableBenchmarkDebug() {
        return enableBenchmarkDebug;
    }

    public void benchmarkStart() {
        if (!isEnableBenchmarkDebug()) {
            return;
        }
        benchmark = new Benchmark();
    }

    public long benchmarkEnd() {
        if (!isEnableBenchmarkDebug() || benchmark == null) {
            return 0;
        }
        return (benchmark.duration());
    }

    public void setRunInBackground(boolean b) {
        runInBackground = b;
    }

    public boolean isRunInBackground() {
        return runInBackground;
    }

    public void setException(final Throwable e) {
        exception = e;
    }

    public Throwable getException() {
        return exception;
    }

    public boolean hasException() {
        return (exception != null);
    }

    public String getIdentifier() {
        return null;
    }

}
