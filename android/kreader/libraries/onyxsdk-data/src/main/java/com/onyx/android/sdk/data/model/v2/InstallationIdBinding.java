package com.onyx.android.sdk.data.model.v2;

import com.onyx.android.sdk.data.model.Device;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.Serializable;

/**
 * Created by suicheng on 2017/8/2.
 */
public class InstallationIdBinding implements Serializable {
    public Device device;

    public InstallationIdBinding(Device device) {
        this.device = device;
    }

    public boolean checkBindingInfoValid() {
        if (device == null || StringUtils.isNullOrEmpty(device.macAddress)
                || CollectionUtils.isNullOrEmpty(device.installationMap)) {
            return false;
        }
        return true;
    }
}
