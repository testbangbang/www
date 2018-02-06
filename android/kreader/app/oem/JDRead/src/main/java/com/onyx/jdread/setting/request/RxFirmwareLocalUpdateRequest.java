package com.onyx.jdread.setting.request;

import android.os.RecoverySystem;
import android.util.Log;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.R;
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

    private boolean success = false;
    private String failString;

    public RxFirmwareLocalUpdateRequest(SettingBundle bundle) {
        this.bundle = bundle;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getFailString() {
        return failString;
    }

    @Override
    public Object call() throws Exception {
        startUpdate();
        return this;
    }

    private void startUpdate() {
        File updateFile = UpdateUtil.getUpdateZipFile();
        if (!updateFile.exists()) {
            failString = getAppContext().getString(R.string.file_does_not_exist);
            return;
        }
        success = verifyPackage(updateFile);
        if (!success) {
            failString = getAppContext().getString(R.string.updating_package_parse_failed);
            return;
        }
        success = installPackage(updateFile);
        if (!success) {
            failString = getAppContext().getString(R.string.updating_package_install_failed);
        }
    }

    private boolean verifyPackage(File updateFile) {
        try {
            RecoverySystem.verifyPackage(updateFile, new RecoverySystem.ProgressListener() {
                @Override
                public void onProgress(int progress) {
                    bundle.getEventBus().post(new SystemPackageDownloadEvent(progress));
                    Log.i(TAG, "==========progress=========" + progress);
                }
            }, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean installPackage(File updateFile) {
        try {
            RecoverySystem.installPackage(getAppContext(), updateFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
