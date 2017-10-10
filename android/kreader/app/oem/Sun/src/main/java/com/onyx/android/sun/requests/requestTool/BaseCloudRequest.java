package com.onyx.android.sun.requests.requestTool;


/**
 * Created by huxiaomao on 2016/11/29.
 */

public class BaseCloudRequest extends BaseRequest {
    public BaseCloudRequest() {
        setAbortPendingTasks(true);
    }

    @Override
    public void beforeExecute(final SunRequestManager helper) {
        helper.acquireWakeLock(getContext(), getClass().getSimpleName());
        if (isAbort()) {

        }
        benchmarkStart();
        invokeStartCallback(helper.getRequestManager());
        RequestUtils.toggleWiFi(getContext(),true);
    }

    private void invokeStartCallback(final RequestManager requestManager) {
        if (getCallback() == null) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getCallback().start(BaseCloudRequest.this);
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
                    getCallback().done(BaseCloudRequest.this, getException());
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
