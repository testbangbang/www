package com.onyx.demo.push.manager;

import android.content.Context;
import android.text.TextUtils;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.request.cloud.CloudFileDownloadRequest;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;

/**
 * Created by suicheng on 2017/3/3.
 */
public class PushManager {

    public static File getDownloadFile(String fileName) {
        fileName = FileUtils.fixNotAllowFileName(fileName);
        if (StringUtils.isNullOrEmpty(fileName)) {
            return null;
        }
        File fileDir = new File(EnvironmentUtil.getExternalStorageDirectory() + "/Download/bot/");
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return new File(fileDir, fileName);
    }

    public static BaseDownloadTask downFromCloud(final Context context, String url, String fileName, final BaseCallback customCallback) {
        final File file = getDownloadFile(fileName);
        if (file == null) {
            return null;
        }

        if (!TextUtils.isEmpty(url)) {
            final CloudFileDownloadRequest downloadRequest = buildFileDownloadRequest(context, url, file, url);
            BaseDownloadTask task = downFromCloud(downloadRequest, new BaseCallback() {
                @Override
                public void start(BaseRequest request) {
                    if (customCallback != null) {
                        customCallback.start(request);
                    }
                }

                @Override
                public void done(BaseRequest request, Throwable e) {
                    BaseCallback.invoke(customCallback, request, e);
                }

                @Override
                public void progress(final BaseRequest request, final ProgressInfo info) {
                    BaseCallback.invokeProgress(customCallback, request, info);
                }
            });
            task.setForceReDownload(true);
            return task;
        }
        return null;
    }

    public static BaseDownloadTask downFromCloud(CloudFileDownloadRequest request, BaseCallback callback) {
        return OnyxDownloadManager.getInstance().download(request, callback);
    }

    public static CloudFileDownloadRequest buildFileDownloadRequest(Context context, String url, final File file, Object tag) {
        final CloudFileDownloadRequest downloadRequest = new CloudFileDownloadRequest(url, file.getAbsolutePath(), tag);
        downloadRequest.setContext(context);
        return downloadRequest;
    }
}
