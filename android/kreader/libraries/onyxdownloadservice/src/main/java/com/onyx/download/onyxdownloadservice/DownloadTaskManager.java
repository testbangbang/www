package com.onyx.download.onyxdownloadservice;

import android.content.Context;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadConnectListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadHelper;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;
import java.util.LinkedHashMap;

/**
 * Created by 12 on 2017/1/14.
 */

public class DownloadTaskManager {
    private static DownloadTaskManager instance;
    private LinkedHashMap<Integer, BaseDownloadTask> taskMap = new LinkedHashMap<>();
    private DownloadConnectListener listener;
    private FileDownloadConnectListener l;

    private DownloadTaskManager(Context context) {
        FileDownloader.init(context);
    }

    public static synchronized DownloadTaskManager getInstance(Context context) {
        if (instance == null) {
            instance = new DownloadTaskManager(context);
        }
        return instance;
    }

    public static synchronized DownloadTaskManager getInstance() {
        return getInstance(getContext());
    }

    public static synchronized Context getContext() {
        return FileDownloadHelper.getAppContext();
    }

    public DownloadRequest createDownloadRequest(final String url, final String path, final String tag) {
        DownloadRequest downloadRequest = new DownloadRequest(url, path, tag);
        downloadRequest.setTaskId(FileDownloadUtils.generateId(url, path));
        return downloadRequest;
    }

    public DownloadListener createDownloadListener(DownloadRequest request, DownloadCallback downloadCallback) {
        return new DownloadListener(request, downloadCallback);
    }

    public BaseDownloadTask createDownloadTask(DownloadRequest request, DownloadListener listener) {
        BaseDownloadTask task = FileDownloader.getImpl().create(request.getUrl())
                .setPath(request.getPath())
                .setCallbackProgressTimes(100)
                .setListener(listener);
        return task;
    }

    public void startDownload(DownloadRequest request, DownloadCallback callback) {
        DownloadListener downloadListener = createDownloadListener(request, callback);
        BaseDownloadTask downloadTask = createDownloadTask(request, downloadListener);
        downloadTask.start();
    }

    public int startDownload(BaseDownloadTask task) {
        task.start();
        return task.getId();
    }

    public void startDownload(DownloadRequest request, DownloadListener listener) {
        BaseDownloadTask downloadTask = createDownloadTask(request, listener);
        downloadTask.start();
    }

    public int addDownloadCallback(DownloadRequest request, DownloadCallback callback) {
        DownloadListener downloadListener = createDownloadListener(request, callback);
        BaseDownloadTask downloadTask = createDownloadTask(request, downloadListener);
        addTask(downloadTask.getId(), downloadTask);
        return startDownload(downloadTask);
    }

    public void pause(DownloadRequest request) {
        FileDownloader.getImpl().pause(request.getTaskId());
    }

    public void pause(int id) {
        FileDownloader.getImpl().pause(id);
    }

    public void pauseAll() {
        FileDownloader.getImpl().pauseAll();
    }

    public void delete(DownloadRequest request) {
        File path = new File(request.getPath());
        path.delete();
        File tempPath = new File(FileDownloadUtils.getTempPath(request.getPath()));
        tempPath.delete();

        if (!path.exists() && !tempPath.exists()) {
            request.setState(FileDownloadStatus.INVALID_STATUS);
        }

        removeTask(request.getTaskId());
    }

    public boolean isExist(DownloadRequest request) {
        File path = new File(request.getPath());
        File tempPath = new File(FileDownloadUtils.getTempPath(request.getPath()));

        if (!path.exists() && !tempPath.exists()) {
            return false;
        }
        return true;
    }

    public File getTempPath(DownloadRequest request) {
        return new File(FileDownloadUtils.getTempPath(request.getPath()));
    }

    public void registerServiceConnectionListener() {
        if (!FileDownloader.getImpl().isServiceConnected()) {
            FileDownloader.getImpl().bindService();

            if (l != null) {
                FileDownloader.getImpl().removeServiceConnectListener(l);
            }
            l = new FileDownloadConnectListener() {
                @Override
                public void connected() {
                    if (listener != null) {
                        listener.connected();
                    }
                }

                @Override
                public void disconnected() {
                    if (listener != null) {
                        listener.disConnected();
                    }
                }
            };
            FileDownloader.getImpl().addServiceConnectListener(l);
        }
    }

    public void unRegisterServiceConnectionListener() {
        FileDownloader.getImpl().removeServiceConnectListener(l);
        l = null;
    }

    public interface DownloadConnectListener {

        void connected();

        void disConnected();
    }

    public void setDownloadConnectListener(DownloadConnectListener listener) {
        this.listener = listener;
    }

    public void addTask(int id, BaseDownloadTask task) {
        synchronized (taskMap) {
            taskMap.put(id, task);
        }
    }

    public void removeTask(int id) {
        synchronized (taskMap) {
            taskMap.remove(id);
        }
    }

    public void clearTask() {
        synchronized (taskMap) {
            taskMap.clear();
        }
    }

    public boolean isDownloaded(int status) {
        return status == FileDownloadStatus.completed;
    }

    public boolean isDownloading(int status) {
        return status == FileDownloadStatus.progress;
    }

    public boolean isPause(int status) {
        return status == FileDownloadStatus.paused;
    }

    public boolean isStarted(int status) {
        return status == FileDownloadStatus.started;
    }

    public boolean isPending(int status) {
        return status == FileDownloadStatus.pending;
    }

    public boolean isConnected(int status) {
        return status == FileDownloadStatus.connected;
    }

    public boolean isError(int status) {
        return status == FileDownloadStatus.error;
    }

    public boolean isDelete(int status) {
        return status == FileDownloadStatus.INVALID_STATUS;
    }

    public boolean isReady() {
        return FileDownloader.getImpl().isServiceConnected();
    }

    public int getStatus(DownloadRequest request) {
        return FileDownloader.getImpl().getStatus(request.getTaskId(), request.getPath());
    }

    public long getTotal(final int id) {
        return FileDownloader.getImpl().getTotal(id);
    }

    public long getSoFar(final int id) {
        return FileDownloader.getImpl().getSoFar(id);
    }
}
