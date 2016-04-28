package com.onyx.kreader.common;

import android.content.Context;
import com.onyx.android.cropimage.data.RefValue;
import com.onyx.kreader.host.impl.ReaderBitmapImpl;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/4/15.
 */
public abstract class BaseRequest {

    private static final String TAG = BaseRequest.class.getSimpleName();
    private int requestSequence;
    private volatile boolean abort = false;
    private volatile boolean abortPendingTasks = false;
    private boolean useWakeLock = true;
    private boolean runInBackground = true;
    private boolean saveOptions = true;
    private BaseCallback callback;
    private Context context;
    private Benchmark benchmark;
    private Exception exception;
    private ReaderBitmapImpl renderBitmap;
    private ReaderViewInfo readerViewInfo;

    static private volatile int globalRequestSequence;
    static private boolean enableBenchmarkDebug = true;

    // help to safely copy render bitmap to viewport bitmap
    private static final Object bitmapCopyLock = new Object();

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

    public boolean isSaveOptions() {
        return saveOptions;
    }

    public void setException(final Exception e) {
        exception = e;
    }

    public Exception getException() {
        return exception;
    }

    public ReaderBitmapImpl getRenderBitmap() {
        return renderBitmap;
    }

    public void useRenderBitmap(final Reader reader) {
        renderBitmap = reader.getReaderHelper().getRenderBitmap();
    }

    public void beforeExecute(final Reader reader) {
        reader.acquireWakeLock(getContext());
        benchmarkStart();
        if (isAbort()) {
            reader.getReaderHelper().setAbortFlag();
        }
        if (callback == null) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                callback.start(BaseRequest.this);
            }
        };
        if (isRunInBackground()) {
            reader.getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
    }

    public abstract void execute(final Reader reader) throws Exception;

    public void afterExecute(final Reader reader) {
        if (exception != null) {
            exception.printStackTrace();
        }
        benchmarkEnd();
        reader.getReaderHelper().clearAbortFlag();

        // store render bitmap store to local flag to avoid multi-thread problem
        final boolean needCopy = reader.isRenderBitmapDirty();
        final RefValue<Boolean> waitCopy = new RefValue<>(true);

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    if (needCopy) {
                        synchronized (bitmapCopyLock) {
                            try {
                                reader.copyRenderBitmapToViewport();
                                reader.setRenderBitmapDirty(false);
                            } catch (Exception ex) {
                                setException(ex);
                            } finally {
                                waitCopy.setValue(false);
                                bitmapCopyLock.notify();
                            }
                        }
                    }
                    callback.done(BaseRequest.this, getException());
                }
            }};

        if (isRunInBackground()) {
            reader.getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }

        if (needCopy) {
            synchronized (bitmapCopyLock) {
                try {
                    while (waitCopy.getValue()) {
                        bitmapCopyLock.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        reader.releaseWakeLock();
    }

    public final ReaderViewInfo getReaderViewInfo() {
        return readerViewInfo;
    }

    public ReaderViewInfo createReaderViewInfo() {
        readerViewInfo = new ReaderViewInfo();
        return readerViewInfo;
    }

}
