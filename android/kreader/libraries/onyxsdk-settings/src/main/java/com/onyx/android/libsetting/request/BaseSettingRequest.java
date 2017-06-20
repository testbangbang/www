package com.onyx.android.libsetting.request;

import android.util.Log;

import com.onyx.android.libsetting.SettingManager;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

import static com.onyx.android.sdk.ui.data.ReaderLayerMenu.TAG;

/**
 * Created by solskjaer49 on 2017/2/11 18:40.
 */

public abstract class BaseSettingRequest extends BaseRequest {

    public void beforeExecute(final SettingManager settingManager) {
        settingManager.getRequestManager().acquireWakeLock(getContext(), getClass().getSimpleName());
        benchmarkStart();
        if (isAbort()) {

        }
        if (getCallback() == null) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getCallback().start(BaseSettingRequest.this);
            }
        };
        if (isRunInBackground()) {
            settingManager.getRequestManager().getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
    }

    public abstract void execute(final SettingManager settingManager) throws Exception;

    /**
     * must not throw out exception from the method
     *
     * @param settingManager
     */
    public void afterExecute(final SettingManager settingManager) {
        try {
            afterExecuteImpl(settingManager);
        } catch (Throwable tr) {
            Log.w(TAG, tr);
        } finally {
            invokeCallback(settingManager);
        }
    }

    private void afterExecuteImpl(final SettingManager settingManager) throws Throwable {
        dumpException();
        benchmarkEnd();
    }

    private void invokeCallback(final SettingManager settingManager) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                BaseCallback.invoke(getCallback(), BaseSettingRequest.this, getException());
                settingManager.getRequestManager().releaseWakeLock();
            }
        };

        if (isRunInBackground()) {
            settingManager.getRequestManager().getLooperHandler().post(runnable);
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
