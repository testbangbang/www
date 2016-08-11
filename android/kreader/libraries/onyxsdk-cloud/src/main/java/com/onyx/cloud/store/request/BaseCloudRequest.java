package com.onyx.cloud.store.request;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.cloud.CloudManager;
import org.json.JSONObject;

import android.util.Log;

import com.onyx.cloud.BuildConfig;

/**
 * Created by zhuzeng on 11/21/15.
 */
public abstract class BaseCloudRequest extends BaseRequest {

    private static final String TAG = BaseCloudRequest.class.getSimpleName();
    public static final String RESPONSE_KEY_ERROR = "error";
    private boolean saveToLocal = true;

    public BaseCloudRequest() {
        super();
    }

    public boolean isSaveToLocal() {
        return saveToLocal;
    }

    public void setSaveToLocal(boolean save) {
        saveToLocal = save;
    }

    public void beforeExecute(final CloudManager parent) {
        parent.acquireWakeLock(getContext());
        benchmarkStart();
        if (isAbort()) {
        }
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
            parent.getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
    }

    public abstract void execute(final CloudManager parent) throws Exception;

    /**
     * must not throw out exception from the method
     *
     * @param parent
     */
    public void afterExecute(final CloudManager parent) {
        try {
            afterExecuteImpl(parent);
        } catch (Throwable tr) {
            Log.w(TAG, tr);
        } finally {
            invokeCallback(parent);
        }
    }

    private void afterExecuteImpl(final CloudManager parent) throws Throwable {
        dumpException();
        benchmarkEnd();
    }

    private void invokeCallback(final CloudManager parent) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                BaseCallback.invoke(getCallback(), BaseCloudRequest.this, getException());
                parent.releaseWakeLock();
            }};

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

    public void dumpMessage(final String tag, final String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

    public void dumpMessage(final String tag, Throwable throwable, JSONObject errorResponse) {
        if (throwable != null && throwable.getMessage() != null) {
            dumpMessage(tag, throwable.getMessage());
        }
        if (errorResponse != null) {
            dumpMessage(tag, errorResponse.toString());
        }
    }
}
