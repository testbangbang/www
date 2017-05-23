package com.onyx.android.eschool.action;

import android.content.Context;
import android.view.View;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.eschool.R;
import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.ui.dialog.DialogLoading;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

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

    @Override
    public void execute(LibraryDataHolder dataHolder, BaseCallback baseCallback) {
        if (StringUtils.isNullOrEmpty(url)) {
            ToastUtils.showToast(dataHolder.getContext(), R.string.download_link_invalid);
            return;
        }
        if (StringUtils.isNullOrEmpty(filePath)) {
            ToastUtils.showToast(dataHolder.getContext(), R.string.file_path_invalid);
            return;
        }
        if (isTaskDownloading(dataHolder.getContext(), tag)) {
            return;
        }
        startDownload(dataHolder.getContext(), baseCallback);
    }

    private boolean isTaskDownloading(Context context, Object tag) {
        return getDownLoaderManager(context).getTask(tag) != null;
    }

    private void startDownload(final Context context, final BaseCallback baseCallback) {
        final DialogLoading loadingDialog = showDownloadingDialog(context, FileUtils.getBaseName(filePath), tag);
        BaseDownloadTask task = getDownLoaderManager(context).download(url, filePath,
                tag, new BaseCallback() {
                    @Override
                    public void progress(BaseRequest request, ProgressInfo info) {
                        loadingDialog.setProgressMessage(String.valueOf((int) info.progress) + "%");
                    }

                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        loadingDialog.dismiss();
                        removeDownloadingTask(request.getContext(), tag);
                        if (e != null) {
                            ToastUtils.showToast(request.getContext(), R.string.download_fail);
                        }
                        BaseCallback.invoke(baseCallback, request, e);
                    }
                });
        getDownLoaderManager(context).addTask(tag, task);
        getDownLoaderManager(context).startDownload(task);
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
        BaseDownloadTask task = getDownLoaderManager(context).getTask(tag);
        removeDownloadingTask(context, tag);
        if (task != null) {
            task.pause();
        }
    }

    private void removeDownloadingTask(Context context, Object tag) {
        getDownLoaderManager(context).removeTask(tag);
    }

    private OnyxDownloadManager getDownLoaderManager(Context context) {
        return OnyxDownloadManager.getInstance(context);
    }
}
