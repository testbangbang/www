package com.onyx.android.dr.event;

/**
 * Created by yangzhongping on 17-5-5.
 */
public class DeviceSettingViewBaseEvent {
    public static class DeviceSettingBaseEvent{}

    public static class DeviceSettingPageRefreshEvent extends DeviceSettingBaseEvent{}

    public static class DeviceSettingLockScreenTimeEvent extends DeviceSettingBaseEvent{}

    public static class DeviceSettingAutomaticShutDownEvent extends DeviceSettingBaseEvent{}

    public static class DeviceSettingLanguageSettingsEvent extends DeviceSettingBaseEvent{}

    public static class DeviceSettingDeviceInformationEvent extends DeviceSettingBaseEvent{}

    public static class DeviceSettingViewDeviceStorageEvent extends DeviceSettingBaseEvent{}

    public static class DeviceSettingCheckUpdateEvent extends DeviceSettingBaseEvent{}

    public static class DeviceSettingViewSystemVersionHistoryEvent extends DeviceSettingBaseEvent{}

    public static class DeviceResetInformationEvent extends DeviceSettingBaseEvent {}

    public static class OpenSystemSettingEvent extends DeviceSettingBaseEvent {}
}
