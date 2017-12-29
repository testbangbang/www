package com.onyx.jdread.setting.event;

import com.onyx.jdread.setting.model.DeviceConfigData;

/**
 * Created by li on 2017/12/25.
 */

public class DeviceConfigEvent {
    private DeviceConfigData deviceConfigData;

    public DeviceConfigData getDeviceConfigData() {
        return deviceConfigData;
    }

    public void setDeviceConfigData(DeviceConfigData deviceConfigData) {
        this.deviceConfigData = deviceConfigData;
    }
}
