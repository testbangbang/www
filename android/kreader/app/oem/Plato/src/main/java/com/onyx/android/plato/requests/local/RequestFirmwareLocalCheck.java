package com.onyx.android.plato.requests.local;

import com.onyx.android.sdk.data.manager.OTAManager;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.requests.requestTool.BaseLocalRequest;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;
import com.onyx.android.plato.utils.ApkUtils;

/**
 * Created by hehai on 17-5-12.
 */

public class RequestFirmwareLocalCheck extends BaseLocalRequest {
    @Override
    public void execute(SunRequestManager helper) throws Exception {
        OTAManager.sharedInstance().startFirmwareUpdate(SunApplication.getInstance(), ApkUtils.getUpdateZipFile().getAbsolutePath());
    }
}
