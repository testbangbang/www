package com.onyx.android.sun.data;


import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.requests.requestTool.BaseLocalRequest;
import com.onyx.android.sun.requests.requestTool.SunRequestManager;

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
