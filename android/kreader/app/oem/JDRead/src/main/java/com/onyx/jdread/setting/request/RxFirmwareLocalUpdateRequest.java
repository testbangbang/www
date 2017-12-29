package com.onyx.jdread.setting.request;

import com.onyx.android.sdk.data.manager.OTAManager;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.setting.utils.UpdateUtil;

/**
 * Created by li on 2017/12/25.
 */

public class RxFirmwareLocalUpdateRequest extends RxBaseCloudRequest {

    @Override
    public Object call() throws Exception {
        OTAManager.sharedInstance().startFirmwareUpdate(JDReadApplication.getInstance(), UpdateUtil.getUpdateZipFile().getAbsolutePath());
        return this;
    }
}
