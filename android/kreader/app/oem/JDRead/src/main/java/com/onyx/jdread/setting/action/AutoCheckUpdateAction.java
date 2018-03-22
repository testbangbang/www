package com.onyx.jdread.setting.action;

import android.util.Log;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.model.ApplicationUpdate;
import com.onyx.android.sdk.data.model.Firmware;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.setting.event.BackToDeviceConfigFragment;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.request.RxCheckApkUpdateRequest;
import com.onyx.jdread.setting.request.RxFirmwareUpdateRequest;
import com.onyx.jdread.setting.utils.UpdateUtil;
import com.onyx.jdread.shop.utils.DownLoadHelper;
import com.onyx.jdread.util.TimeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by li on 2017/12/25.
 */

public class AutoCheckUpdateAction extends BaseAction {
    private String tag;
    private String url;
    private String path;

    @Override
    public void execute(final SettingBundle bundle, final RxCallback callback) {
        if (isUpdateExist()) {
            return;
        }

        long lastCheckTime = JDPreferenceManager.getLongValue(ResManager.getString(R.string.check_update_key), 0);
        if (System.currentTimeMillis() - lastCheckTime < Constants.CHECK_UPDATE_INTERVAL) {
            return;
        }
        JDPreferenceManager.setLongValue(ResManager.getString(R.string.check_update_key), System.currentTimeMillis());
        final RxFirmwareUpdateRequest rq = UpdateUtil.cloudFirmwareCheckRequest(JDReadApplication.getInstance(), bundle.getCloudManager());
        rq.execute(new RxCallback<RxFirmwareUpdateRequest>() {
            @Override
            public void onNext(RxFirmwareUpdateRequest firmwareUpdateRequest) {
                boolean resultFirmwareValid = firmwareUpdateRequest.isResultFirmwareValid();
                Firmware resultFirmware = firmwareUpdateRequest.getResultFirmware();
                if (resultFirmwareValid) {
                    url = resultFirmware.getUrl();
                    path = UpdateUtil.getUpdateZipFile().getAbsolutePath();
                    tag = UpdateUtil.SYSTEM_UPDATE_TAG;
                    downloadUpdate();
                } else {
                    checkApkUpdate(bundle);
                }
            }

            @Override
            public void onError(Throwable e) {
                RxCallback.invokeError(callback, e);
            }
        });
    }

    private boolean isUpdateExist() {
        return FileUtils.fileExist(UpdateUtil.getApkUpdateFile()) || UpdateUtil.getUpdateZipFile().exists();
    }

    private void checkApkUpdate(SettingBundle bundle) {
        ApplicationUpdate queryAppUpdate = UpdateUtil.getQueryAppUpdate();
        List<ApplicationUpdate> list = new ArrayList<>();
        list.add(queryAppUpdate);
        final RxCheckApkUpdateRequest request = new RxCheckApkUpdateRequest(bundle.getCloudManager(), list);
        request.execute(new RxCallback<RxCheckApkUpdateRequest>() {
            @Override
            public void onNext(RxCheckApkUpdateRequest checkApkUpdateRequest) {
                ApplicationUpdate applicationUpdate = checkApkUpdateRequest.getApplicationUpdate();
                if (applicationUpdate != null) {
                    String[] downloadUrlList = applicationUpdate.downloadUrlList;
                    if (downloadUrlList.length > 0) {
                        url = downloadUrlList[0];
                        path = UpdateUtil.getApkUpdateFile();
                        tag = UpdateUtil.APK_UPDATE_TAG;
                        downloadUpdate();
                    }
                }
            }
        });
    }

    private void downloadUpdate() {
        DownloadPackageAction downloadPackageAction = new DownloadPackageAction(url, path, tag);
        downloadPackageAction.execute(null);
    }
}
