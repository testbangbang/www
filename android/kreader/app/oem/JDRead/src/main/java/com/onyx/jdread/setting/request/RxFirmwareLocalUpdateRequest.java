package com.onyx.jdread.setting.request;

import android.os.RecoverySystem;
import android.util.Log;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.setting.event.SystemPackageDownloadEvent;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.utils.UpdateUtil;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Created by li on 2017/12/25.
 */

public class RxFirmwareLocalUpdateRequest extends RxBaseCloudRequest {
    private static final String TAG = RxFirmwareLocalUpdateRequest.class.getSimpleName();
    private SettingBundle bundle;

    public RxFirmwareLocalUpdateRequest(SettingBundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public Object call() throws Exception {
        startUpdate();
        return this;
    }

    private void startUpdate() {
        File update = UpdateUtil.getUpdateZipFile();
        if (!update.exists()) {
            return;
        }
        boolean status = false;
        try {
            RecoverySystem.verifyPackage(update, new RecoverySystem.ProgressListener() {
                @Override
                public void onProgress(int progress) {
                    bundle.getEventBus().post(new SystemPackageDownloadEvent(progress));
                    Log.i(TAG, "==========progress=========" + progress);
                }
            }, null);
            status = true;
        } catch (IOException e) {
            status = false;
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            status = false;
            e.printStackTrace();
        }
        if (!status) {
            return;
        }
        try {
            RecoverySystem.installPackage(getAppContext(), update);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
