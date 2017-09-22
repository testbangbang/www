package com.onyx.android.dr.request.cloud;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.util.ApkUtils;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.manager.OTAManager;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;

/**
 * Created by hehai on 17-5-12.
 */
public class RequestFirmwareLocalCheck extends AutoNetWorkConnectionBaseCloudRequest {

    @Override
    public void execute(CloudManager helper) throws Exception {
        OTAManager.sharedInstance().startFirmwareUpdate(DRApplication.getInstance(), ApkUtils.getUpdateZipFile().getAbsolutePath());
    }
}
