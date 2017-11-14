package com.onyx.android.plato.data;


import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.requests.requestTool.BaseLocalRequest;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

/**
 * Created by li on 2017/7/3.
 */

public class RequestDeviceSettingData extends BaseLocalRequest {
    private DeviceSettingData deviceSettingData;

    public RequestDeviceSettingData(DeviceSettingData deviceSettingData) {
        this.deviceSettingData = deviceSettingData;
    }

    @Override
    public void execute(SunRequestManager helper) throws Exception {
        deviceSettingData.updateDeviceInformation(SunApplication.getInstance());
    }
}
