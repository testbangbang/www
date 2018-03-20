package com.onyx.android.update.upgrade;

import android.os.RecoverySystem;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.update.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by suicheng on 2018/3/20.
 */
public class RxFirmwareLocalUpdateRequest extends RxBaseCloudRequest {

    private boolean success = false;
    private boolean failDelete = true;
    private String failString;
    private String filePath;

    public RxFirmwareLocalUpdateRequest(String filePath) {
        this.filePath = filePath;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getFailString() {
        return failString;
    }

    public void setFailDelete(boolean delete) {
        this.failDelete = delete;
    }

    @Override
    public Object call() throws Exception {
        startUpdate();
        return this;
    }

    private void startUpdate() {
        if (StringUtils.isNullOrEmpty(filePath) || !FileUtils.fileExist(filePath)) {
            failString = String.format(getAppContext().getString(R.string.file_not_exist), filePath);
            return;
        }
        success = installPackage(new File(filePath));
        if (!success) {
            failString = getAppContext().getString(R.string.update_fail);
            if (failDelete) {
                FileUtils.deleteFile(filePath);
            }
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
