package com.onyx.download.onyxdownloadservice;

import android.os.Handler;
import android.os.Looper;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by 12 on 2017/1/17.
 */

public class DownloadListener extends FileDownloadSampleListener {

    private final DownloadRequest request;
    private DownloadCallback callback;
    private Handler handler = new Handler(Looper.getMainLooper());
    private static final int DOWNLOADED_STATUS = -3;

    public DownloadListener(DownloadRequest request, DownloadCallback callback) {
        this.callback = callback;
        this.request = request;
    }

    @Override
    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        if (task.getListener() != this) {
            return;
        }
        updateLoading(task, soFarBytes, totalBytes);
    }

    @Override
    protected void started(BaseDownloadTask task) {
        if (task.getListener() != this) {
            return;
        }
        updateLoading(task, task.getSmallFileSoFarBytes(), task.getSmallFileTotalBytes());
    }

    @Override
    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        if (task.getListener() != this) {
            return;
        }
        updateLoading(task, soFarBytes, totalBytes);
    }

    @Override
    protected void blockComplete(BaseDownloadTask task) {
        if (task.getListener() != this) {
            return;
        }
        updateLoading(task, task.getSmallFileSoFarBytes(), task.getSmallFileTotalBytes());
    }

    @Override
    protected void completed(BaseDownloadTask task) {
        if (task.getListener() != this) {
            return;
        }
        updateLoading(task, task.getSmallFileSoFarBytes(), task.getSmallFileTotalBytes());
        DownloadTaskManager.getInstance().removeTask(task.getId());
    }

    @Override
    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
        if (task.getListener() != this) {
            return;
        }
        updateLoading(task, soFarBytes, totalBytes);
    }

    @Override
    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        if (task.getListener() != this) {
            return;
        }
        updateLoading(task, soFarBytes, totalBytes);
    }

    @Override
    protected void error(BaseDownloadTask task, Throwable e) {
        if (task.getListener() != this) {
            return;
        }
        EventBus.getDefault().post(new DownloadErrorEvent(e.getMessage()));
        updateLoading(task, task.getSmallFileSoFarBytes(), task.getSmallFileTotalBytes());
        DownloadTaskManager.getInstance().removeTask(task.getId());
    }

    private void updateLoading(final BaseDownloadTask task, final int soFarBytes, final int totalBytes) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                updateDownloadRequest(task);
                final float percent = (float) soFarBytes / (float) totalBytes;
                if (callback != null) {
                    if (task.getStatus() == DOWNLOADED_STATUS) {
                        EventBus.getDefault().post(new FinishEvent(task.getPath()));
                    }
                    if (totalBytes > 0) {
                        callback.progressChanged(task.getId(), request.getTag(), task.getUrl(), task.getPath(),
                                task.getStatus(), soFarBytes, totalBytes, (long) (percent * 100));

                        EventBus.getDefault().post(new ReportDownloadProcessEvent(task.getId(), request.getTag(),
                                task.getUrl(), task.getPath(), task.getStatus(), soFarBytes, totalBytes,
                                (long) (percent * 100)));
                    } else {
                        callback.progressChanged(task.getId(), request.getTag(), task.getUrl(), task.getPath(),
                                task.getStatus(), soFarBytes, totalBytes, 0);
                        EventBus.getDefault().post(new ReportDownloadProcessEvent(task.getId(), request.getTag(),
                                task.getUrl(), task.getPath(), task.getStatus(), soFarBytes, totalBytes, 0));
                    }
                }
            }
        });
    }

    private void updateDownloadRequest(BaseDownloadTask task) {
        request.setState(task.getStatus());
        request.setTaskId(task.getId());
    }
}
