package com.onyx.cloud.utils;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.cloud.store.request.BaseCloudRequest;

/**
 * Created by suicheng on 2016/8/17.
 */
public class DownloadListener extends FileDownloadListener {
    BaseCallback.ProgressInfo progressInfo;
    private BaseCloudRequest baseCloudRequest;
    private BaseCallback baseCallback;

    public DownloadListener(BaseCloudRequest baseCloudRequest, BaseCallback baseCallback) {
        this.baseCloudRequest = baseCloudRequest;
        this.baseCallback = baseCallback;
        this.progressInfo = new BaseCallback.ProgressInfo();
    }

    @Override
    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
    }

    @Override
    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        if (baseCallback != null) {
            progressInfo.progress = soFarBytes * 1.0f / totalBytes * 100;
            baseCallback.progress(baseCloudRequest, progressInfo);
        }
    }

    @Override
    protected void completed(final BaseDownloadTask task){
        if (baseCallback != null) {
            baseCallback.done(baseCloudRequest, null);
        }
    }

    @Override
    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
    }

    @Override
    protected void error(BaseDownloadTask task, Throwable e) {
        if (baseCallback != null) {
            baseCallback.done(baseCloudRequest, e);
        }
    }

    @Override
    protected void warn(BaseDownloadTask task) {
    }
}
