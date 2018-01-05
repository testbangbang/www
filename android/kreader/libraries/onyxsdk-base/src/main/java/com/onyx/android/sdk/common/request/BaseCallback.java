package com.onyx.android.sdk.common.request;

import android.os.Handler;
import android.util.Log;

/**
 * Created by zhuzeng on 10/4/15.
 */
public abstract class BaseCallback {

    public static class ProgressInfo {
        public long soFarBytes;
        public long totalBytes;
        public double progress;
    }

    public void start(final BaseRequest request) {
    }

    public void progress(final BaseRequest request, final ProgressInfo info) {
    }

    public void beforeDone(final BaseRequest request, final Throwable e) {
    }

    public abstract void done(final BaseRequest request, final Throwable e);

    public static void invokeProgress(final BaseCallback callback, final BaseRequest request, final ProgressInfo progressInfo) {
        if (callback != null) {
            callback.progress(request, progressInfo);
        }
    }

    public static void invokeProgress(final Handler handler, final BaseCallback callback, final BaseRequest request, final ProgressInfo progressInfo) {
        if (callback != null && handler != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.progress(request, progressInfo);
                }
            });
        }
    }

    public static void invokeStart(final BaseCallback callback, final BaseRequest request) {
        if (callback != null) {
            callback.start(request);
        }
    }

    public static void invoke(final BaseCallback callback, final BaseRequest request, final Throwable e) {
        if (callback != null) {
            callback.done(request, e);
        }
    }

    public static void invokeBeforeDone(final BaseCallback callback, final BaseRequest request, final Throwable e) {
        if (callback != null) {
            callback.beforeDone(request, e);
        }
    }

}
