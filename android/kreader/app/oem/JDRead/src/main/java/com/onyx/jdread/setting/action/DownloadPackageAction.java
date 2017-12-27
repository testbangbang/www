package com.onyx.jdread.setting.action;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.common.ContentException;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;

/**
 * Created by li on 2017/12/26.
 */

public class DownloadPackageAction {
    private String url;
    private String filePath;
    private Object tag;

    public DownloadPackageAction(String url, String filePath, Object tag) {
        this.url = url;
        this.filePath = filePath;
        this.tag = tag;
    }

    public void execute(BaseCallback callback) {
        if (StringUtils.isNullOrEmpty(url)) {
            BaseCallback.invoke(callback, null, ContentException.UrlInvalidException());
            return;
        }
        if (StringUtils.isNullOrEmpty(filePath)) {
            BaseCallback.invoke(callback, null, ContentException.FilePathInvalidException());
            return;
        }
        if (isTaskDownloading(tag)) {
            return;
        }
        startDownload(callback);
    }

    private void startDownload(BaseCallback callback) {
        BaseDownloadTask task = getDownLoaderManager().download(JDReadApplication.getInstance(), url, filePath, tag, callback);
        getDownLoaderManager().addTask(tag, task);
        getDownLoaderManager().startDownload(task);
    }

    private boolean isTaskDownloading(Object tag) {
        return getDownLoaderManager().getTask(tag) != null;
    }

    private OnyxDownloadManager getDownLoaderManager() {
        return OnyxDownloadManager.getInstance();
    }
}
