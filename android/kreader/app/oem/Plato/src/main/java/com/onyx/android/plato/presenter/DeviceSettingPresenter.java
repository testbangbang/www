package com.onyx.android.plato.presenter;

import android.content.Context;


import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.data.DeviceInformation;
import com.onyx.android.plato.data.DeviceSettingData;
import com.onyx.android.plato.data.DeviceStorageInformation;
import com.onyx.android.plato.data.RequestDeviceSettingData;
import com.onyx.android.plato.data.SystemVersionInformation;
import com.onyx.android.plato.devicesetting.SystemLanguageInformation;
import com.onyx.android.plato.event.LoadConfigDataEvent;
import com.onyx.android.plato.interfaces.DeviceSettingView;
import com.onyx.android.plato.requests.requestTool.BaseCallback;
import com.onyx.android.plato.requests.requestTool.BaseRequest;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

/**
 * Created by huxiaomao on 2016/12/13.
 */

public class DeviceSettingPresenter {
    DeviceSettingView deviceSettingView;
    DeviceSettingData deviceSettingData;

    public DeviceSettingPresenter(DeviceSettingView deviceSettingView) {
        this.deviceSettingView = deviceSettingView;
        this.deviceSettingData = new DeviceSettingData();
    }

    public void loadConfigData(final Context context) {
        deviceSettingData.loadConfigData(SunApplication.getInstance());

        RequestDeviceSettingData rq = new RequestDeviceSettingData(deviceSettingData);
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                EventBus.getDefault().post(new LoadConfigDataEvent());
            }
        });
    }

    public String[] getDeviceSettingTitle() {
        return deviceSettingData.getDeviceSettingTitle();
    }

    public Map<String, CharSequence> getDeviceSettingValue(Context context) {
        return deviceSettingData.getDeviceSettingValue(context);
    }

    public CharSequence[] getDeviceSettingLockScreenTime() {
        return deviceSettingData.getDeviceSettingLockScreenTime();
    }

    public CharSequence[] getDeviceSettingLockScreenTimeValue() {
        return deviceSettingData.getDeviceSettingLockScreenTimeValue();
    }

    public CharSequence getCurrentScreenTimeout() {
        return deviceSettingData.getCurrentScreenTimeout();
    }

    public void setCurrentScreenTimeout(final Context context, CharSequence currentScreenTimeout) {
        deviceSettingData.setCurrentScreenTimeout(context, currentScreenTimeout);
    }

    public CharSequence[] getDeviceSettingAutomaticShutdownTime() {
        return deviceSettingData.getDeviceSettingAutomaticShutdownTime();
    }

    public CharSequence[] getDeviceSettingAutomaticShutdownTimeValue() {
        return deviceSettingData.getDeviceSettingAutomaticShutdownTimeValue();
    }

    public CharSequence getCurrentTimeoutValue() {
        return deviceSettingData.getCurrentTimeoutValue();
    }

    public void setCurrentTimeoutValue(final Context context, final CharSequence currentTimeoutValue) {
        deviceSettingData.setCurrentTimeoutValue(context, currentTimeoutValue);
    }

    public CharSequence[] getDeviceSettingAutomaticShutdownTimeExplain() {
        return deviceSettingData.getDeviceSettingAutomaticShutdownTimeExplain();
    }

    public List<DeviceInformation> getDeviceSettingDeviceInformation() {
        return deviceSettingData.getDeviceSettingDeviceInformation();
    }

    public List<DeviceStorageInformation> getDeviceStorageInformationList() {
        return deviceSettingData.getDeviceStorageInformationList();
    }

    public List<SystemVersionInformation> getSystemVersionInformationList() {
        return deviceSettingData.getSystemVersionInformationList();
    }

    public SystemLanguageInformation getLocaleLanguageInfoList() {
        return deviceSettingData.getLocaleLanguageInfoList();
    }

    public void updateDeviceInformation(final Context context) {
        deviceSettingData.updateDeviceInformation(context);
    }
}
