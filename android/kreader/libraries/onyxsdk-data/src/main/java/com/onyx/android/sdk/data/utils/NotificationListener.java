package com.onyx.android.sdk.data.utils;

import android.content.Context;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.notification.BaseNotificationItem;
import com.liulishuo.filedownloader.notification.FileDownloadNotificationHelper;
import com.liulishuo.filedownloader.notification.FileDownloadNotificationListener;

/**
 * Created by suicheng on 2016/8/22.
 */
public class NotificationListener extends FileDownloadNotificationListener {
    private Context context;
    private NotificationItem.NotificationBean bean;

    public NotificationListener(Context context, NotificationItem.NotificationBean bean, FileDownloadNotificationHelper helper) {
        super(helper);
        this.context = context;
        this.bean = bean;
    }

    @Override
    protected BaseNotificationItem create(BaseDownloadTask task) {
        bean.id = task.getId();
        bean.title = task.getFilename();
        return new NotificationItem(bean);
    }

    @Override
    public void addNotificationItem(BaseDownloadTask task) {
        super.addNotificationItem(task);
    }

    @Override
    public void destroyNotification(BaseDownloadTask task) {
        super.destroyNotification(task);
    }

    @Override
    protected boolean interceptCancel(BaseDownloadTask task,
                                      BaseNotificationItem n) {
        // I don't want to cancel the notification, just show for the test
        // so return true
        return true;
    }

    @Override
    protected boolean disableNotification(BaseDownloadTask task) {
        return super.disableNotification(task);
    }

    @Override
    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        super.pending(task, soFarBytes, totalBytes);
    }

    @Override
    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        super.progress(task, soFarBytes, totalBytes);
    }

    @Override
    protected void completed(BaseDownloadTask task) {
        super.completed(task);
    }
}
