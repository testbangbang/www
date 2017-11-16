package com.onyx.android.plato.requests.cloud;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.request.cloud.CloudFileDownloadRequest;
import com.onyx.android.sdk.data.utils.DownloadUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.plato.common.Constants;
import com.onyx.android.plato.event.ApkDownloadSucceedEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by hehai on 17-10-20.
 */

public class DownloadApkRequest extends BaseCloudRequest {
    private DownloadUtils.DownloadCallback downloadCallback;

    private String url;

    public DownloadApkRequest(String url,DownloadUtils.DownloadCallback downloadCallback) {
        this.url = url;
        this.downloadCallback = downloadCallback;
    }

    @Override
    public void execute(CloudManager helper) throws Exception {
        FileUtils.deleteFile(Constants.APK_DOWNLOAD_PATH);
        CloudFileDownloadRequest downloadRequest = OnyxDownloadManager.getInstance().createDownloadRequest(url, Constants.APK_DOWNLOAD_PATH, Constants.APK_NAME);
        BaseDownloadTask download = OnyxDownloadManager.getInstance().download(downloadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if(e == null){
                    EventBus.getDefault().post(new ApkDownloadSucceedEvent());
                }
            }

            @Override
            public void progress(BaseRequest request, ProgressInfo info) {
                if(downloadCallback != null){
                    downloadCallback.stateChanged(0,info.soFarBytes,info.totalBytes,(long)info.progress);
                }
            }
        });
        OnyxDownloadManager.getInstance().startDownload(download);
    }
}
