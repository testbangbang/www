package com.onyx.android.dr.request.cloud;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.event.ApkDownloadSucceedEvent;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.utils.FileUtils;

import org.greenrobot.eventbus.EventBus;

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
        OnyxDownloadManager downloadManager = OnyxDownloadManager.getInstance();
        BaseDownloadTask download = downloadManager.download(DRApplication.getInstance(), url, Constants.APK_DOWNLOAD_PATH, Constants.APK_NAME, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    EventBus.getDefault().post(new ApkDownloadSucceedEvent());
                }
            }
        });
        downloadManager.startDownload(download);
    }
}
