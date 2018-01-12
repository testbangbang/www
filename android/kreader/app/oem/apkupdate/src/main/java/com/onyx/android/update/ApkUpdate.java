package com.onyx.android.update;

import android.util.Log;

import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.download.onyxdownloadservice.DownloadCallback;
import com.onyx.download.onyxdownloadservice.DownloadRequest;
import com.onyx.download.onyxdownloadservice.DownloadTaskManager;

import java.io.File;

/**
 * Created by huxiaomao on 17/10/23.
 */

public class ApkUpdate {
    private static final String TAG = ApkUpdate.class.getSimpleName();
    public static final String APK_NAME = "SuperSReader.apk";
    public static final String APK_DOWNLOAD_PATH = Device.currentDevice.getExternalStorageDirectory() + File.separator + APK_NAME;

    public static int downloadAPK(String url, DownloadCallback callback) {
        Log.i(TAG,APK_DOWNLOAD_PATH);
        FileUtils.deleteFile(APK_DOWNLOAD_PATH);
        DownloadRequest downloadRequest = DownloadTaskManager.getInstance().createDownloadRequest(url, APK_DOWNLOAD_PATH, APK_NAME);
        return DownloadTaskManager.getInstance().addDownloadCallback(downloadRequest, callback);
    }
}
