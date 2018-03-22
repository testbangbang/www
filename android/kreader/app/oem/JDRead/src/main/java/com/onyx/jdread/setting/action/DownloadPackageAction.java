package com.onyx.jdread.setting.action;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.common.ContentException;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.shop.utils.DownLoadHelper;

import java.io.IOException;
import java.net.ConnectException;

/**
 * Created by li on 2017/12/26.
 */

public class DownloadPackageAction {
    private String url;
    private String filePath;
    private Object tag;

    private boolean restart = false;

    public DownloadPackageAction(String url, String filePath, Object tag) {
        this.url = url;
        this.filePath = filePath;
        this.tag = tag;
    }

    public void setRestart(boolean restart) {
        this.restart = restart;
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
        if (isPauseOrError(tag)) {
            getTask(tag).reuse();
            getTask(tag).start();
            return;
        }
        if (restart) {
            removeDownloadTask();
        }
        if (isTaskDownloading(tag)) {
            return;
        }
        startDownload(callback);
    }

    private void startDownload(final BaseCallback callback) {
        BaseDownloadTask task = getDownLoaderManager().download(JDReadApplication.getInstance(), url, filePath, tag, new BaseCallback() {
            @Override
            public void start(BaseRequest request) {
                BaseCallback.invokeStart(callback, request);
            }

            @Override
            public void progress(BaseRequest request, ProgressInfo info) {
                BaseCallback.invokeProgress(callback, request, info);
            }

            @Override
            public void done(BaseRequest request, Throwable e) {
                removeDownloadTask();
                checkDownloadException(e);
                BaseCallback.invoke(callback, request, e);
            }
        });
        getDownLoaderManager().addTask(tag, task);
        getDownLoaderManager().startDownload(task);
    }

    private boolean checkDownloadException(Throwable e) {
        if (e == null) {
            return false;
        }
        if (e instanceof ConnectException || e instanceof IOException) {
            ToastUtil.showToast(ResManager.getString(R.string.network_exception));
        } else {
            ToastUtil.showToast(e.getMessage());
        }
        return true;
    }

    private boolean isTaskDownloading(Object tag) {
        BaseDownloadTask task = getDownLoaderManager().getTask(tag);
        return task != null && DownLoadHelper.isDownloading(task.getStatus());
    }

    public BaseDownloadTask getTask(Object tag) {
        return getDownLoaderManager().getTask(tag);
    }

    private boolean isPauseOrError(Object tag) {
        BaseDownloadTask task = getDownLoaderManager().getTask(tag);
        return task != null && (DownLoadHelper.isError(task.getStatus()) || DownLoadHelper.isPause(task.getStatus()));
    }

    private OnyxDownloadManager getDownLoaderManager() {
        return OnyxDownloadManager.getInstance();
    }

    private void removeDownloadTask() {
        OnyxDownloadManager downLoaderManager = getDownLoaderManager();
        downLoaderManager.removeTask(tag);
    }
}
