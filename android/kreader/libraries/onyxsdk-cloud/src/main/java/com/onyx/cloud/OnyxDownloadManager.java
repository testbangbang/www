package com.onyx.cloud;

import android.content.Context;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.notification.FileDownloadNotificationHelper;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.cloud.db.query.DownloadTaskDbQuery;
import com.onyx.cloud.model.DownloadTask;
import com.onyx.cloud.store.request.CloudFileRequest;
import com.onyx.cloud.utils.DownloadListener;
import com.onyx.cloud.utils.NotificationItem;
import com.onyx.cloud.utils.NotificationListener;
import com.onyx.cloud.utils.StoreUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by suicheng on 2016/8/15.
 */
public class OnyxDownloadManager {
    private static OnyxDownloadManager fileDownloadManager;
    private FileDownloadNotificationHelper<NotificationItem> helper = new FileDownloadNotificationHelper<>();
    private ArrayList<DownloadStatusUpdater> updaterList = new ArrayList<>();
    private List<DownloadTask> taskList = new ArrayList<>();

    private OnyxDownloadManager(Context context) {
        FileDownloader.init(context);
        FileDownloader.getImpl().setMaxNetworkThreadCount(5);
        // add here maybe will slow the init
        getTaskList();
    }

    public static OnyxDownloadManager getInstance(Context context) {
        if (fileDownloadManager == null) {
            synchronized (OnyxDownloadManager.class) {
                if (fileDownloadManager == null) {
                    fileDownloadManager = new OnyxDownloadManager(context);
                }
            }
        }
        return fileDownloadManager;
    }

    public int download(final String url, final String path, final String tag, final BaseCallback baseCallback) {
        CloudFileRequest request = new CloudFileRequest(url, path, tag);
        return download(request, baseCallback);
    }

    public int download(final String url, final String path, final String tag, final BaseCallback baseCallback, final DownloadStatusUpdater updater) {
        CloudFileRequest request = new CloudFileRequest(url, path, tag);
        return download(request, baseCallback, updater);
    }

    public int download(final CloudFileRequest request, final BaseCallback baseCallback) {
        BaseDownloadTask task = FileDownloader.getImpl().create(request.url).setPath(request.path).setTag(request.tag);
        DownloadListener listener = new DownloadListener(request, baseCallback);
        task.setListener(listener);
        return startDownload(task);
    }

    public int download(final CloudFileRequest request, final BaseCallback baseCallback, DownloadStatusUpdater updater) {
        BaseDownloadTask task = FileDownloader.getImpl().create(request.url).setPath(request.path).setTag(request.tag);
        DownloadListener listener = new DownloadListener(request, baseCallback);
        task.setListener(listener);
        if (updater != null) {
            listener.setDownloadStatusUpdater(updater);
            addUpdater(updater);
        }
        return startDownload(task);
    }

    public int downloadWithNotify(Context context, final String url, final String path, final String tag, NotificationItem.NotificationBean bean) {
        BaseDownloadTask task = FileDownloader.getImpl().create(url).setPath(path).setTag(tag);
        NotificationListener listener = new NotificationListener(context, bean, helper);
        task.setListener(listener);
        return startDownload(task);
    }

    private int startDownload(BaseDownloadTask task) {
        getTaskModel(task);
        return task.start();
    }

    private List<DownloadTask> getTaskList() {
        if (taskList.size() <= 0) {
            DownloadTaskDbQuery query = new DownloadTaskDbQuery();
            taskList = query.andNullFinishedAt().startQueryList();
        }
        return taskList;
    }

    private DownloadTask getTaskModel(BaseDownloadTask task) {
        DownloadTask downloadTask = getTaskModelById(task.getId());
        if (downloadTask != null) {
            return downloadTask;
        }
        downloadTask = createDownloadTask(task);
        addTaskModel(downloadTask);
        return downloadTask;
    }

    public void addTaskModel(DownloadTask downloadTask) {
        downloadTask.async().save();
        getTaskList().add(downloadTask);
    }

    public DownloadTask getTaskModelById(final int id) {
        for (DownloadTask model : getTaskList()) {
            if (model.taskId == id) {
                return model;
            }
        }
        return null;
    }

    public void saveTaskModel(BaseDownloadTask task) {
        DownloadTask downloadTask = getTaskModelById(task.getId());
        if (downloadTask != null) {
            downloadTask.finishedAt = new Date(System.currentTimeMillis());
            downloadTask.async().update();
        }
    }

    public void saveTaskList() {
        if (taskList.size() > 0) {
            StoreUtils.updateToLocalFast(taskList, DownloadTask.class);
        }
    }

    private DownloadTask createDownloadTask(BaseDownloadTask task) {
        String url = task.getUrl();
        String path = task.getPath();
        int taskId = FileDownloadUtils.generateId(url, path);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.url = url;
        downloadTask.path = path;
        downloadTask.name = task.getFilename();
        downloadTask.taskId = taskId;
        return downloadTask;
    }

    public void addUpdater(DownloadStatusUpdater updater) {
        if (!updaterList.contains(updater)) {
            updaterList.add(updater);
        }
    }

    // must been called in onDestroy/taskCompleted, prevent memory leak
    public boolean removeUpdater(DownloadStatusUpdater updater) {
        return updaterList.remove(updater);
    }

    public interface DownloadStatusUpdater {
        void update(BaseDownloadTask task);
    }
}
