package com.onyx.android.sdk.data.request.cloud;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.common.ContentException;
import com.onyx.android.sdk.data.model.OnyxAccount;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.data.utils.ResultCode;
import com.onyx.android.sdk.data.utils.RetrofitUtils;
import com.onyx.android.sdk.dataprovider.BuildConfig;

import org.json.JSONObject;

import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by zhuzeng on 11/21/15.
 */
public abstract class BaseCloudRequest extends BaseRequest {

    private static final String TAG = BaseCloudRequest.class.getSimpleName();
    private volatile boolean saveToLocal = true;

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
            }
        };

        if (isRunInBackground()) {
            parent.getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
    }

    private void invokeCallBackProgress(final CloudManager parent, final BaseCallback.ProgressInfo progressInfo) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getCallback().progress(BaseCloudRequest.this, progressInfo);
            }
        };

        if (isRunInBackground()) {
            parent.getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
    }

    protected boolean writeFileToDisk(CloudManager parent, ResponseBody body, File localFile, BaseCallback callback) throws java.io.IOException {
        try (InputStream inputStream = body.byteStream();
             OutputStream outputStream = new FileOutputStream(localFile)) {

            byte[] fileReader = new byte[4096];
            long fileSize = body.contentLength();
            long fileSizeDownloaded = 0;

            BaseCallback.ProgressInfo progressInfo = new BaseCallback.ProgressInfo();
            while (true) {
                int read = inputStream.read(fileReader);
                if (read == -1) {
                    break;
                }
                outputStream.write(fileReader, 0, read);
                fileSizeDownloaded += read;
                if (callback != null) {
                    if (fileSize > 0) {
                        progressInfo.progress = fileSizeDownloaded * 1.0 / fileSize * 100;
                        invokeCallBackProgress(parent, progressInfo);
                    }
                }
                dumpMessage(TAG, "file download: " + (fileSizeDownloaded * 1.0 / fileSize * 100) + "%");
            }
            outputStream.flush();
            return true;
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

    protected String getAccountSessionToken() {
        return OnyxAccount.loadAccountSessionToken(getContext());
    }

    protected <T> Response<T> executeCall(Call<T> call) throws Exception {
        return RetrofitUtils.executeCall(call);
    }

    public String getIdentifier() {
        return "cloudContent";
    }
}
