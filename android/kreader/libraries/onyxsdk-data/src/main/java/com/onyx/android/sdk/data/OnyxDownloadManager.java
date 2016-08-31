package com.onyx.android.sdk.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.notification.FileDownloadNotificationHelper;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.model.DownloadTask;
import com.onyx.android.sdk.data.request.CloudFileDownloadRequest;
import com.onyx.android.sdk.data.utils.DownloadListener;
import com.onyx.android.sdk.data.utils.NotificationItem;
import com.onyx.android.sdk.data.utils.NotificationListener;
import com.onyx.android.sdk.data.utils.StoreUtils;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by suicheng on 2016/8/15.
 */
public class OnyxDownloadManager {

    private static OnyxDownloadManager instance;
    private FileDownloadNotificationHelper<NotificationItem> helper = new FileDownloadNotificationHelper<>();
    private List<DownloadTask> taskList = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper());

    private OnyxDownloadManager(Context context) {
        FileDownloader.init(context);
        FileDownloader.getImpl().setMaxNetworkThreadCount(5);
        // add here maybe will slow the init
        getTaskList();
    }

    public static synchronized OnyxDownloadManager getInstance(Context context) {
        if (instance == null) {
            instance = new OnyxDownloadManager(context);
        }
        return instance;
    }

    public int download(final String url, final String path, final String tag, final BaseCallback baseCallback) {
        CloudFileDownloadRequest request = new CloudFileDownloadRequest(url, path, tag);
        return download(request, baseCallback);
    }

    public int download(final CloudFileDownloadRequest request, final BaseCallback baseCallback) {
        BaseDownloadTask task = FileDownloader.getImpl().create(request.getUrl()).setPath(request.getPath()).setTag(request.getTag());
        DownloadListener listener = new DownloadListener(request, baseCallback, handler);
        task.setListener(listener);
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
            taskList = new Select().from(DownloadTask.class).queryList();
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

}
