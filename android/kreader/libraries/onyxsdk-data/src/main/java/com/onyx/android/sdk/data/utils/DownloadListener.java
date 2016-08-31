package com.onyx.android.sdk.data.utils;

import android.os.Handler;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.request.CloudFileDownloadRequest;

/**
 * Created by suicheng on 2016/8/17.
 */
public class DownloadListener extends FileDownloadListener {
    BaseCallback.ProgressInfo progressInfo;
    private CloudFileDownloadRequest downloadRequest;
    private BaseCallback baseCallback;
    private Handler handler;

    public DownloadListener(final CloudFileDownloadRequest downloadRequest, final BaseCallback baseCallback, final Handler h) {
        this.downloadRequest = downloadRequest;
        this.baseCallback = baseCallback;
        this.progressInfo = new BaseCallback.ProgressInfo();
        this.handler = h;
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
        progressInfo.soFarBytes = soFarBytes;
        progressInfo.totalBytes = totalBytes;
        progressInfo.progress = soFarBytes * 1.0f * 100 / (float)totalBytes;
        update(task);
    }

    @Override
    protected void blockComplete(BaseDownloadTask task) {
    }

    @Override
    protected void completed(final BaseDownloadTask task) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                baseCallback.done(downloadRequest, null);
            }
        });
    }

    @Override
    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        update(task);
    }

    @Override
    protected void error(BaseDownloadTask task, final Throwable e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                baseCallback.done(downloadRequest, e);
            }
        });
    }

    @Override
    protected void warn(BaseDownloadTask task) {
        update(task);
    }

    private void update(final BaseDownloadTask task) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                baseCallback.invokeProgress(baseCallback, downloadRequest, progressInfo);
            }
        });
    }

}
