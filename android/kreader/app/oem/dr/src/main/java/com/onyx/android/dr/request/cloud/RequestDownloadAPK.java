package com.onyx.android.dr.request.cloud;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.download.onyxdownloadservice.DownloadCallback;
import com.onyx.download.onyxdownloadservice.DownloadRequest;
import com.onyx.download.onyxdownloadservice.DownloadTaskManager;

/**
 * Created by hehai on 17-5-31.
 */

public class RequestDownloadAPK extends BaseCloudRequest {
    private String url;

    public RequestDownloadAPK(String url) {
        this.url = url;
    }

    @Override
    public void execute(CloudManager helper) throws Exception {
        FileUtils.deleteFile(Constants.APK_DOWNLOAD_PATH);
        DownloadRequest downloadRequest = DownloadTaskManager.getInstance().createDownloadRequest(url, Constants.APK_DOWNLOAD_PATH, Constants.APK_NAME);
        int reference = DownloadTaskManager.getInstance().addDownloadCallback(downloadRequest, new DownloadCallback() {
            @Override
            public void progressChanged(int reference, String title, String remoteUri, String localUri, int state, long finished, long total, long percentage) {

            }
        });
        DRApplication.getInstance().setApkDownloadReference(reference);
    }
}
