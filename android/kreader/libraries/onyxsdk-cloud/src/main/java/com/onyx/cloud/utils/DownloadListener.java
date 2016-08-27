package com.onyx.cloud.utils;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.cloud.store.request.CloudFileDownloadRequest;

/**
 * Created by suicheng on 2016/8/17.
 */
public class DownloadListener extends FileDownloadListener {
    BaseCallback.ProgressInfo progressInfo;
    private CloudFileDownloadRequest downloadRequest;
    private BaseCallback baseCallback;

    public DownloadListener(CloudFileDownloadRequest downloadRequest, BaseCallback baseCallback) {
        this.downloadRequest = downloadRequest;
        this.baseCallback = baseCallback;
        this.progressInfo = new BaseCallback.ProgressInfo();
    }

    @Override
    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        update(task);
    }

    @Override
    protected void connected(BaseDownloadTask task, String tag, boolean isContinue, int soFarBytes, int totalBytes) {
        update(task);
    }

    @Override
    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        if (baseCallback != null) {
            progressInfo.progress = soFarBytes * 1.0f / totalBytes * 100;
            baseCallback.progress(downloadRequest, progressInfo);
        }
        update(task);
    }

    @Override
    protected void blockComplete(BaseDownloadTask task) {
        update(task);
    }

    @Override
    protected void completed(final BaseDownloadTask task) {
        if (baseCallback != null) {
            baseCallback.done(downloadRequest, null);
        }
        update(task);
    }

    @Override
    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        update(task);
    }

    @Override
    protected void error(BaseDownloadTask task, Throwable e) {
        if (baseCallback != null) {
            baseCallback.done(downloadRequest, e);
        }
        update(task);
    }

    @Override
    protected void warn(BaseDownloadTask task) {
        update(task);
    }

    private void update(BaseDownloadTask task) {
        baseCallback.invokeProgress(baseCallback, downloadRequest, progressInfo);
    }

}
