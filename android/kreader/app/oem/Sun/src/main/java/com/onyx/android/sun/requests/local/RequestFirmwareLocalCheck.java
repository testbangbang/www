package com.onyx.android.sun.requests.local;

import com.onyx.android.sdk.data.manager.OTAManager;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.requests.requestTool.BaseLocalRequest;
import com.onyx.android.sun.requests.requestTool.SunRequestManager;
import com.onyx.android.sun.utils.ApkUtils;

/**
 * Created by hehai on 17-5-12.
 */

public class RequestFirmwareLocalCheck extends BaseLocalRequest {
    @Override
    public void execute(SunRequestManager helper) throws Exception {
        OTAManager.sharedInstance().startFirmwareUpdate(SunApplication.getInstance(), ApkUtils.getUpdateZipFile().getAbsolutePath());
    }
}
