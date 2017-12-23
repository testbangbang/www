package com.onyx.jdread.shop.utils;

import com.liulishuo.filedownloader.model.FileDownloadStatus;

/**
 * Created by jackdeng on 2017/12/21.
 */

public class DownLoadHelper {

    public static final int DOWNLOAD_PERCENT_FINISH = 100;

    public static boolean isDownloaded (int status) {
        return status == FileDownloadStatus.completed;
    }

    public static boolean isDownloading (int status) {
        return status == FileDownloadStatus.progress;
    }

    public static boolean isPause (int status) {
        return status == FileDownloadStatus.paused;
    }

    public static boolean isError (int status) {
        return status == FileDownloadStatus.error;
    }

    public static boolean isStarted (int status) {
        return status == FileDownloadStatus.started;
    }

    public static boolean isConnected (int status) {
        return status == FileDownloadStatus.connected;
    }

    public static boolean canInsertBookDetail (int status){
        return isDownloaded(status) || isPause(status) || isError(status) || isStarted(status) || isConnected(status);
    }

}
