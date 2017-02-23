package com.onyx.sdk.ebookservice.request;

import android.util.Log;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.sdk.ebookservice.HtmlManager;

/**
 * Created by suicheng on 2017/2/11.
 */

public abstract class BaseHtmlRequest extends BaseRequest {

    private static final String TAG = BaseHtmlRequest.class.getSimpleName();

    public void beforeExecute(final HtmlManager parent) {
        parent.acquireWakeLock(getContext(), getClass().getSimpleName());
        benchmarkStart();
        if (isAbort()) {
        }
        if (getCallback() == null) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getCallback().start(BaseHtmlRequest.this);
            }
        };
        if (isRunInBackground()) {
            parent.getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
    }

    public abstract void execute(final HtmlManager parent) throws Exception;

    /**
     * must not throw out exception from the method
     *
     * @param parent
     */
    public void afterExecute(final HtmlManager parent) {
        try {
            afterExecuteImpl(parent);
        } catch (Throwable tr) {
            Log.w(TAG, tr);
        } finally {
            invokeCallback(parent);
        }
    }

    private void afterExecuteImpl(final HtmlManager parent) throws Throwable {
        dumpException();
        benchmarkEnd();
    }

    private void invokeCallback(final HtmlManager parent) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                BaseCallback.invoke(getCallback(), BaseHtmlRequest.this, getException());
                parent.releaseWakeLock();
            }
        };

        if (isRunInBackground()) {
            parent.getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
    }

    private void invokeCallBackProgress(final HtmlManager parent, final BaseCallback.ProgressInfo progressInfo) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getCallback().progress(BaseHtmlRequest.this, progressInfo);
            }
        };

        if (isRunInBackground()) {
            parent.getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
    }

    private void dumpException() {
        if (hasException()) {
            Log.w(TAG, getException());
        }
    }
}
