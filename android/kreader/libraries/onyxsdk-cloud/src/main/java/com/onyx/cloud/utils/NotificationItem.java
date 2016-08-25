package com.onyx.cloud.utils;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.notification.BaseNotificationItem;
import com.liulishuo.filedownloader.util.FileDownloadHelper;

/**
 * Created by suicheng on 2016/8/22.
 */
public class NotificationItem extends BaseNotificationItem {

    private PendingIntent pendingIntent;
    private Intent errorIntent;
    private NotificationCompat.Builder builder;

    public static class NotificationBean {
        public int id;
        public int icon;
        public String title;
        public String desc;
        public PendingIntent pendingIntent;
        public Intent errorIntent;
    }

    public NotificationItem(NotificationBean bean) {
        super(bean.id, bean.title, bean.desc);

        builder = new NotificationCompat.
                Builder(FileDownloadHelper.getAppContext());

        builder.setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentTitle(getTitle())
                .setContentText(bean.desc)
                .setSmallIcon(bean.icon);

        pendingIntent = bean.pendingIntent;
        errorIntent = bean.errorIntent;
    }

    @Override
    public void show(boolean statusChanged, int status, boolean isShowProgress) {

        String desc = getDesc();
        switch (status) {
            case FileDownloadStatus.pending:
                desc += " pending";
                break;
            case FileDownloadStatus.started:
                desc += " started";
                break;
            case FileDownloadStatus.progress:
                desc += " progress";
                break;
            case FileDownloadStatus.retry:
                desc += " retry";
                break;
            case FileDownloadStatus.error:
                desc += " error";
                if (errorIntent != null) {
                    FileDownloadHelper.getAppContext().sendBroadcast(errorIntent);
                }
                break;
            case FileDownloadStatus.paused:
                desc += " paused";
                break;
            case FileDownloadStatus.completed:
                desc += " completed";
                if (pendingIntent != null) {
                    builder.setContentIntent(pendingIntent);
                }
                break;
            case FileDownloadStatus.warn:
                desc += " warn";
                break;
        }

        builder.setContentTitle(getTitle())
                .setContentText(desc);

        if (statusChanged) {
            builder.setTicker(desc);
        }

        builder.setProgress(getTotal(), getSofar(), !isShowProgress);
        getManager().notify(getId(), builder.build());
    }

}
