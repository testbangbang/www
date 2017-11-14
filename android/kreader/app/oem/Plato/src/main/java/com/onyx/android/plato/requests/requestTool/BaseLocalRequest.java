package com.onyx.android.plato.requests.requestTool;

/**
 * Created by huxiaomao on 2016/11/29.
 */

public class BaseLocalRequest extends BaseRequest {
    public BaseLocalRequest() {
        setAbortPendingTasks(true);
    }

    @Override
    public void beforeExecute(final SunRequestManager helper) {
        helper.acquireWakeLock(getContext(), getClass().getSimpleName());
        if (isAbort()) {

        }
        benchmarkStart();
        invokeStartCallback(helper.getRequestManager());
    }

    private void invokeStartCallback(final RequestManager requestManager) {
        if (getCallback() == null) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getCallback().start(BaseLocalRequest.this);
            }
        };
        if (isRunInBackground()) {
            requestManager.getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
    }

    @Override
    public void afterExecute(final SunRequestManager helper) {
        if (hasException()) {
            getException().printStackTrace();
        }
        benchmarkEnd();
        invokeEndCallback(helper);
    }

    private void invokeEndCallback(final SunRequestManager helper) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (getCallback() != null) {
                    getCallback().done(BaseLocalRequest.this, getException());
                }
                helper.getRequestManager().releaseWakeLock();
            }
        };

        if (isRunInBackground()) {
            helper.getRequestManager().getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
    }
}
