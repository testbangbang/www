package com.onyx.android.sdk.data.model.common;

import android.content.Context;

import com.onyx.android.sdk.data.model.Device;
import com.onyx.android.sdk.utils.DeviceInfoUtil;

import java.io.Serializable;

/**
 * Created by suicheng on 2017/12/15.
 */
public class PanelInfo implements Serializable {
    public String vcom;
    public String nvmVersion;
    public String partNo;
    public String waveformVersion;
    public String barCode;

    public Device deviceInfo;

    public static PanelInfo create(Context context) {
        PanelInfo panelInfo = new PanelInfo();
        panelInfo.barCode = DeviceInfoUtil.getBarCode();
        panelInfo.nvmVersion = DeviceInfoUtil.getPanelNvmVersion();
        panelInfo.partNo = DeviceInfoUtil.getPanelPartNo();
        panelInfo.vcom = DeviceInfoUtil.getPanelVCom();
        panelInfo.waveformVersion = DeviceInfoUtil.getPanelWaveFormVersion();
        panelInfo.deviceInfo = Device.updateCurrentDeviceInfo(context);
        return panelInfo;
    }
}
