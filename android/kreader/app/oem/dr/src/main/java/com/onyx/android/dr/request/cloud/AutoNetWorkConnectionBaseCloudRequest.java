package com.onyx.android.dr.request.cloud;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.util.Utils;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by hehai on 17-9-8.
 */

public class AutoNetWorkConnectionBaseCloudRequest extends BaseCloudRequest {
    @Override
    public void execute(CloudManager parent) throws Exception {

    }

    @Override
    public void beforeExecute(CloudManager parent) {
        super.beforeExecute(parent);
        if (StringUtils.isNullOrEmpty(parent.getToken())) {
            ActivityManager.startLoginActivity(DRApplication.getInstance());
        }
        if (!NetworkUtil.isWiFiConnected(DRApplication.getInstance())) {
            if (0 == Utils.getConfiguredNetworks(DRApplication.getInstance())) {
                ActivityManager.startWifiActivity(DRApplication.getInstance());
            } else {
                Device.currentDevice().enableWifiDetect(DRApplication.getInstance());
                NetworkUtil.enableWiFi(DRApplication.getInstance(), true);
            }
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(R.string.please_connect_to_the_network_first));
        }
    }
}
