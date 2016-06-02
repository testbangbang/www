package com.onyx.kreader.common;

import android.content.Context;

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
    private Exception exception;
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

    public void setAbortPendingTasks() {
        abortPendingTasks = true;
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

    public void setException(final Exception e) {
        exception = e;
    }

    public Exception getException() {
        return exception;
    }

    public boolean hasException() {
        return (exception != null);
    }


}
