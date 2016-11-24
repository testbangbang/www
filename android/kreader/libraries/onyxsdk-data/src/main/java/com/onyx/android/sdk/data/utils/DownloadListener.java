package com.onyx.android.sdk.data.utils;

import android.os.Handler;
import android.os.Looper;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.notification.BaseNotificationItem;
import com.liulishuo.filedownloader.notification.FileDownloadNotificationHelper;
import com.liulishuo.filedownloader.notification.FileDownloadNotificationListener;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.request.cloud.CloudFileDownloadRequest;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by suicheng on 2016/8/17.
 */
public class DownloadListener extends FileDownloadNotificationListener {
    private BaseCallback.ProgressInfo progressInfo;
    private CloudFileDownloadRequest downloadRequest;
    private BaseCallback baseCallback;
    private Handler handler = new Handler(Looper.getMainLooper());

    private NotificationItem.NotificationBean bean;

    public DownloadListener(final CloudFileDownloadRequest downloadRequest, final BaseCallback baseCallback,
                            FileDownloadNotificationHelper helper) {
        super(helper);
        this.downloadRequest = downloadRequest;
        this.baseCallback = baseCallback;
        this.progressInfo = new BaseCallback.ProgressInfo();
    }

    /**
     * if need notification, NotificationBean must be new not-null instance
     */
    public void setNotificationBean(NotificationItem.NotificationBean bean) {
        this.bean = bean;
    }

    @Override
    protected BaseNotificationItem create(BaseDownloadTask task) {
        if (bean == null) {
            return null;
        }
        bean.id = task.getId();
        if (StringUtils.isNullOrEmpty(bean.title)) {
            bean.title = task.getFilename();
        }
        return new NotificationItem(bean);
    }

    @Override
    protected boolean interceptCancel(BaseDownloadTask task, BaseNotificationItem notificationItem) {
        if (bean != null) {
            return true;
        }
        return super.interceptCancel(task, notificationItem);
    }

    @Override
    protected boolean disableNotification(BaseDownloadTask task) {
        if (bean == null) {
            return true;
        }
        return super.disableNotification(task);
    }

    @Override
    public void destroyNotification(BaseDownloadTask task) {
        super.destroyNotification(task);
    }

    @Override
    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        super.pending(task, soFarBytes, totalBytes);
        processUpdate(task);
    }

    @Override
    protected void started(final BaseDownloadTask task) {
        super.started(task);
        processStart(task);
    }

    @Override
    protected void connected(BaseDownloadTask task, String tag, boolean isContinue, int soFarBytes, int totalBytes) {
        super.connected(task, tag, isContinue, soFarBytes, totalBytes);
        processUpdate(task);
    }

    @Override
    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        super.progress(task, soFarBytes, totalBytes);
        progressInfo.soFarBytes = soFarBytes;
        progressInfo.totalBytes = totalBytes;
        progressInfo.progress = soFarBytes * 1.0f * 100 / (float) totalBytes;
        processUpdate(task);
    }

    /**
     * It work on WorkThread
     */
    @Override
    protected void blockComplete(BaseDownloadTask task) {
        super.blockComplete(task);
        updateDownloadRequest(task);
        executeDownloadRequest();
    }

    @Override
    protected void completed(final BaseDownloadTask task) {
        super.completed(task);
        processException(task, downloadRequest.getException());
    }

    @Override
    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        super.paused(task, soFarBytes, totalBytes);
        processUpdate(task);
    }

    @Override
    protected void error(final BaseDownloadTask task, final Throwable e) {
        super.error(task, e);
        processException(task, e);
    }

    @Override
    protected void warn(BaseDownloadTask task) {
        super.warn(task);
        processUpdate(task);
    }

    private void processStart(final BaseDownloadTask task) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                updateDownloadRequest(task);
                BaseCallback.invokeStart(baseCallback, downloadRequest);
            }
        });
    }

    private void processException(final BaseDownloadTask task, final Throwable e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                updateDownloadRequest(task);
                BaseCallback.invoke(baseCallback, downloadRequest, e);
            }
        });
    }

    private void processUpdate(final BaseDownloadTask task) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                updateDownloadRequest(task);
                BaseCallback.invokeProgress(baseCallback, downloadRequest, progressInfo);
            }
        });
    }

    private void updateDownloadRequest(BaseDownloadTask task) {
        downloadRequest.setState(task.getStatus());
        downloadRequest.setTaskId(task.getId());
    }

    private void executeDownloadRequest() {
        try {
            downloadRequest.execute(null);
        } catch (Exception e) {
            downloadRequest.setException(e);
            throw new RuntimeException(e);
        }
    }

    public CloudFileDownloadRequest getDownloadRequest() {
        return downloadRequest;
    }

    public void setDownloadRequest(CloudFileDownloadRequest request) {
        downloadRequest = request;
    }

    public BaseCallback getBaseCallback() {
        return baseCallback;
    }

    public void setBaseCallback(BaseCallback callback) {
        this.baseCallback = callback;
    }
}
