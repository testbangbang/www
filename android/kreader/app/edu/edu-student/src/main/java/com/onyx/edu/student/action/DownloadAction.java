package com.onyx.edu.student.action;

import android.content.Context;
import android.view.View;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.ui.dialog.DialogLoading;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.student.R;
import com.onyx.edu.student.events.DownloadingEvent;
import com.onyx.edu.student.holder.LibraryDataHolder;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by suicheng on 2017/5/23.
 */

public class DownloadAction extends BaseAction<LibraryDataHolder> {

    private String url;
    private String filePath;
    private Object tag;

    public DownloadAction(String url, String filePath, Object tag) {
        this.url = url;
        this.filePath = filePath;
        this.tag = tag;
    }

    public void execute(Context context, LibraryDataHolder dataHolder, BaseCallback baseCallback) {
        if (StringUtils.isNullOrEmpty(url)) {
            ToastUtils.showToast(context, R.string.download_link_invalid);
            return;
        }
        if (StringUtils.isNullOrEmpty(filePath)) {
            ToastUtils.showToast(context, R.string.file_path_invalid);
            return;
        }
        if (isTaskDownloading(tag)) {
            return;
        }
        startDownload(context, baseCallback);
    }

    private boolean isTaskDownloading(Object tag) {
        return getDownLoaderManager().getTask(tag) != null;
    }

    private void startDownload(final Context context, final BaseCallback baseCallback) {
        BaseDownloadTask task = getDownLoaderManager().download(context, url, filePath,
                tag, new BaseCallback() {

                    @Override
                    public void start(BaseRequest request) {
                        BaseCallback.invokeStart(baseCallback, request);
                        if (baseCallback == null) {
                            EventBus.getDefault().post(new DownloadingEvent());
                        }
                    }

                    @Override
                    public void progress(BaseRequest request, ProgressInfo info) {
                        BaseCallback.invokeProgress(baseCallback, request, info);
                        if (baseCallback == null) {
                            EventBus.getDefault().post(new DownloadingEvent(tag, (int) info.progress));
                        }
                    }

                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        removeDownloadingTask(tag);
                        if (e != null) {
                            ToastUtils.showToast(request.getContext(), R.string.download_fail);
                        }
                        BaseCallback.invoke(baseCallback, request, e);
                        if (baseCallback == null) {
                            EventBus.getDefault().post(new DownloadingEvent());
                        }
                    }
                });
        getDownLoaderManager().addTask(tag, task);
        getDownLoaderManager().startDownload(task);
    }

    public DialogLoading showDownloadingDialog(final Context context, final String name, final Object tag) {
        final DialogLoading dialogLoading = new DialogLoading(context, name, true);
        dialogLoading.setProgressMessage("0%");
        dialogLoading.setCancelButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLoading.dismiss();
                stopDownloadingTask(context, tag);
            }
        });
        dialogLoading.show();
        return dialogLoading;
    }

    private void stopDownloadingTask(Context context, Object tag) {
        BaseDownloadTask task = getDownLoaderManager().getTask(tag);
        removeDownloadingTask(tag);
        if (task != null) {
            task.pause();
        }
    }

    private void removeDownloadingTask(Object tag) {
        getDownLoaderManager().removeTask(tag);
    }

    private OnyxDownloadManager getDownLoaderManager() {
        return OnyxDownloadManager.getInstance();
    }
}
