package com.onyx.android.sdk.data.action.push;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.action.ActionContext;
import com.onyx.android.sdk.data.common.ContentException;
import com.onyx.android.sdk.data.request.cloud.CloudFileDownloadRequest;
import com.onyx.android.sdk.data.utils.NotificationItem.*;
import com.onyx.android.sdk.dataprovider.R;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.ViewDocumentUtils;

import java.io.File;

/**
 * Created by suicheng on 2017/5/23.
 */
public class DownloadAction {

    private String url;
    private String filePath;
    private Object tag;
    private String md5;

    private boolean md5CheckResult;

    public boolean isMd5CheckSuccess() {
        return md5CheckResult;
    }

    public DownloadAction(String url, String filePath, Object tag, String md5) {
        this.url = url;
        this.filePath = filePath;
        this.tag = tag;
        this.md5 = md5;
    }

    public void execute(final ActionContext actionContext, final BaseCallback baseCallback) {
        if (StringUtils.isNullOrEmpty(url)) {
            BaseCallback.invoke(baseCallback, null, ContentException.UrlInvalidException());
            return;
        }
        if (StringUtils.isNullOrEmpty(filePath)) {
            BaseCallback.invoke(baseCallback, null, ContentException.FilePathInvalidException());
            return;
        }
        if (isTaskDownloading(tag)) {
            return;
        }
        startDownload(actionContext.context, baseCallback);
    }

    private void startDownload(final Context context, final BaseCallback baseCallback) {
        final CloudFileDownloadRequest downloadRequest = buildFileDownloadRequest(context, true);
        NotificationBean bean = buildPushNotificationBean(context, filePath);
        BaseDownloadTask task = downFromCloud(downloadRequest, bean, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                md5CheckResult = downloadRequest.isMd5Valid();
                removeDownloadingTask(tag);
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
        getDownLoaderManager().addTask(tag, task);
        getDownLoaderManager().startDownload(task);
    }

    private NotificationBean buildPushNotificationBean(Context context, String filePath) {
        NotificationBean bean = new NotificationBean();
        bean.title = FileUtils.getFileName(filePath);
        bean.icon = R.drawable.cloud_file;
        bean.pendingIntent = needIntentToProcessFile(context, new File(filePath));
        return bean;
    }

    private PendingIntent needIntentToProcessFile(Context context, File file) {
        if (file == null) {
            return null;
        }
        return intentToViewDocumentActivity(context, file);
    }

    private PendingIntent intentToViewDocumentActivity(Context context, File file) {
        Intent intent = ViewDocumentUtils.viewActionIntentWithMimeType(file);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private CloudFileDownloadRequest buildFileDownloadRequest(final Context context, final boolean md5Checksum) {
        final CloudFileDownloadRequest downloadRequest = new CloudFileDownloadRequest(url, filePath, tag) {
            @Override
            public void execute(CloudManager parent) throws Exception {
                if (md5Checksum) {
                    String md5Calculated = FileUtils.computeFullMD5Checksum(new File(filePath));
                    setMd5Valid(md5Calculated.equals(md5));
                }
            }
        };
        downloadRequest.setContext(context.getApplicationContext());
        return downloadRequest;
    }

    private BaseDownloadTask downFromCloud(CloudFileDownloadRequest request, NotificationBean bean,
                                           BaseCallback callback) {
        return OnyxDownloadManager.getInstance().downloadWithNotify(request, bean, callback);
    }

    private boolean isTaskDownloading(Object tag) {
        return getDownLoaderManager().getTask(tag) != null;
    }

    private void removeDownloadingTask(Object tag) {
        getDownLoaderManager().removeTask(tag);
    }

    private OnyxDownloadManager getDownLoaderManager() {
        return OnyxDownloadManager.getInstance();
    }
}
