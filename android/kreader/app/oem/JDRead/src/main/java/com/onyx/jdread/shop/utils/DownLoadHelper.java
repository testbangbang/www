package com.onyx.jdread.shop.utils;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.shop.action.DownloadAction;
import com.onyx.jdread.shop.model.ShopDataBundle;

/**
 * Created by jackdeng on 2017/12/21.
 */

public class DownLoadHelper {

    public static final int DOWNLOAD_PERCENT_FINISH = 100;

    public static boolean isDownloaded(int status) {
        return status == FileDownloadStatus.completed;
    }

    public static boolean isDownloading(int status) {
        return status == FileDownloadStatus.progress;
    }

    public static boolean isPause(int status) {
        return status == FileDownloadStatus.paused;
    }

    public static boolean isError(int status) {
        return status == FileDownloadStatus.error;
    }

    public static boolean isStarted(int status) {
        return status == FileDownloadStatus.started;
    }

    public static boolean isConnected(int status) {
        return status == FileDownloadStatus.connected;
    }

    public static byte getPausedState() {
        return FileDownloadStatus.paused;
    }

    public static boolean canInsertBookDetail(int status) {
        return isDownloaded(status) || isPause(status) || isError(status) || isStarted(status) || isDownloading(status);
    }

    public static void stopDownloadingTask(Object tag) {
        BaseDownloadTask task = OnyxDownloadManager.getInstance().getTask(tag);
        if (task != null) {
            task.pause();
        }
    }

    public static void startDownloadingTask(String downloadUrl, String path, Object tag) {
        BaseDownloadTask task = OnyxDownloadManager.getInstance().getTask(tag);
        if (task != null) {
            OnyxDownloadManager.getInstance().removeTask(tag);
        }
        OnyxDownloadManager.getInstance().removeTask(tag);
        DownloadAction downloadAction = new DownloadAction(JDReadApplication.getInstance(), downloadUrl, path, tag);
        downloadAction.execute(ShopDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        });
    }

    public static void removeDownloadingTask(Object tag) {
        OnyxDownloadManager.getInstance().removeTask(tag);
    }

    private boolean isTaskExist(Object key) {
        BaseDownloadTask task = OnyxDownloadManager.getInstance().getTask(key);
        if (task != null && !FileDownloadStatus.isOver(task.getStatus())) {
            return true;
        }
        return false;
    }


    public static String getBookStatus(int downLoadState) {
        String bookStatus = "";
        switch (downLoadState) {
            case FileDownloadStatus.paused:
                bookStatus = ResManager.getString(R.string.download_paused);
                break;
            case FileDownloadStatus.error:
                bookStatus = ResManager.getString(R.string.wait_download);
                break;
            case FileDownloadStatus.progress:
                bookStatus = ResManager.getString(R.string.is_downloading);
                break;
            case FileDownloadStatus.started:
                bookStatus = ResManager.getString(R.string.started);
                break;
            case FileDownloadStatus.completed:
            default:
                bookStatus = "";
        }
        return bookStatus;
    }

    public static boolean showProgress(int downloadStatus) {
        return isDownloading(downloadStatus) || isPause(downloadStatus) || isStarted(downloadStatus);
    }
}
