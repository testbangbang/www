package com.onyx.cloud;

import android.content.Context;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.cloud.store.request.CloudFileRequest;
import com.onyx.cloud.utils.DownloadListener;

/**
 * Created by suicheng on 2016/8/15.
 */
public class OnyxDownloadManager {
    private static OnyxDownloadManager fileDownloadManager;

    private OnyxDownloadManager(Context context) {
        FileDownloader.init(context);
        FileDownloader.getImpl().setMaxNetworkThreadCount(5);
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

    public int download(final CloudFileRequest request, final BaseCallback baseCallback) {
        BaseDownloadTask task = FileDownloader.getImpl().create(request.url).setPath(request.path).setTag(request.tag);
        DownloadListener listener = new DownloadListener(request, baseCallback);
        task.setListener(listener);
        return task.start();
    }
}
