package com.onyx.cloud.utils;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.cloud.OnyxDownloadManager.DownloadStatusUpdater;
import com.onyx.cloud.store.request.BaseCloudRequest;

/**
 * Created by suicheng on 2016/8/17.
 */
public class DownloadListener extends FileDownloadListener {
    BaseCallback.ProgressInfo progressInfo;
    private BaseCloudRequest baseCloudRequest;
    private BaseCallback baseCallback;
    private DownloadStatusUpdater statusUpdater;

    public DownloadListener(BaseCloudRequest baseCloudRequest, BaseCallback baseCallback) {
        this.baseCloudRequest = baseCloudRequest;
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
            baseCallback.progress(baseCloudRequest, progressInfo);
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
            baseCallback.done(baseCloudRequest, null);
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
            baseCallback.done(baseCloudRequest, e);
        }
        update(task);
    }

    @Override
    protected void warn(BaseDownloadTask task) {
        update(task);
    }

    private void update(BaseDownloadTask task) {
        if (statusUpdater != null) {
            statusUpdater.update(task);
        }
    }

    public void setDownloadStatusUpdater(DownloadStatusUpdater updater) {
        this.statusUpdater = updater;
    }
}
