package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.data.DeviceInformation;
import com.onyx.android.dr.data.DeviceSettingData;
import com.onyx.android.dr.data.DeviceStorageInformation;
import com.onyx.android.dr.data.SystemLanguageInformation;
import com.onyx.android.dr.data.SystemVersionInformation;
import com.onyx.android.dr.interfaces.DeviceSettingView;

import java.util.List;
import java.util.Map;

public class DeviceSettingPresenter {
    DeviceSettingView deviceSettingView;
    DeviceSettingData deviceSettingData;

    public DeviceSettingPresenter(DeviceSettingView deviceSettingView) {
        this.deviceSettingView = deviceSettingView;
        this.deviceSettingData = new DeviceSettingData();
    }

    public void loadConfigData(final Context context) {
        deviceSettingData.loadConfigData(context);
    }

    public String[] getDeviceSettingTitle() {
        return deviceSettingData.getDeviceSettingTitle();
    }

    public Map<String, String> getDeviceSettingValue(Context context) {
        return deviceSettingData.getDeviceSettingValue(context);
    }

    public String[] getDeviceSettingPageRefreshes() {
        return deviceSettingData.getDeviceSettingPageRefreshes();
    }

    public String[] getDeviceSettingLockScreenTime() {
        return deviceSettingData.getDeviceSettingLockScreenTime();
    }

    public String[] getDeviceSettingLockScreenTimeValue() {
        return deviceSettingData.getDeviceSettingLockScreenTimeValue();
    }

    public String getCurrentScreenTimeout() {
        return deviceSettingData.getCurrentScreenTimeout();
    }

    public void setCurrentPageRefreshTime(final Context context, String currentPageRefreshTime) {
        deviceSettingData.setCurrentPageRefreshTime(context, currentPageRefreshTime);
    }

    public void setCurrentScreenTimeout(final Context context, String currentScreenTimeout) {
        deviceSettingData.setCurrentScreenTimeout(context, currentScreenTimeout);
    }

    public String[] getDeviceSettingAutomaticShutdownTime() {
        return deviceSettingData.getDeviceSettingAutomaticShutdownTime();
    }

    public String[] getDeviceSettingAutomaticShutdownTimeValue() {
        return deviceSettingData.getDeviceSettingAutomaticShutdownTimeValue();
    }

    public String getCurrentTimeoutValue() {
        return deviceSettingData.getCurrentTimeoutValue();
    }

    public void setCurrentTimeoutValue(final Context context, final String currentTimeoutValue) {
        deviceSettingData.setCurrentTimeoutValue(context, currentTimeoutValue);
    }

    public String[] getDeviceSettingAutomaticShutdownTimeExplain() {
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
