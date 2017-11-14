package com.onyx.android.plato.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;

import com.onyx.android.libsetting.data.PowerSettingTimeoutCategory;
import com.onyx.android.libsetting.util.PowerUtil;
import com.onyx.android.libsetting.util.StorageSizeUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.devicesetting.SystemLanguage;
import com.onyx.android.plato.devicesetting.SystemLanguageInformation;
import com.onyx.android.plato.event.DeviceSettingViewBaseEvent;
import com.onyx.android.plato.utils.SystemUtils;
import com.onyx.android.plato.utils.Utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Created by huxiaomao on 2016/12/13.
 */

public class DeviceSettingData {
    private String[] deviceSettingTitle;
    private Map<String, CharSequence> deviceSettingValue = new HashMap<>();
    private static Map<String, DeviceSettingViewBaseEvent.DeviceSettingBaseEvent> deviceSettingViewEvents = new HashMap<>();
    private CharSequence[] deviceSettingLockScreenTime;
    private CharSequence[] deviceSettingLockScreenTimeValue;
    private CharSequence currentScreenTimeout;

    private CharSequence[] deviceSettingAutomaticShutdownTime;
    private CharSequence[] deviceSettingAutomaticShutdownTimeValue;
    private CharSequence[] deviceSettingAutomaticShutdownTimeExplain;
    private CharSequence currentTimeoutValue;
    private static final int DEFAULT_TIME_VALUE = 0;
    private List<DeviceInformation> deviceSettingDeviceInformation;
    private List<DeviceStorageInformation> deviceStorageInformationList;
    private List<SystemVersionInformation> systemVersionInformationList;
    private SystemLanguageInformation systemLanguageInformation;

    public void loadConfigData(final Context context) {
        deviceSettingTitle = context.getResources().getStringArray(R.array.device_setting_title);
        initDeviceSettingViewEvent(context);

        initLockScreenTime(context);
        initPowerManager(context);

        initDeviceSettingDeviceInformationExplain(context);
        //initDeviceSettingDeviceStorageInformation(context);
        initDeviceSettingSystemVersionInformation(context);
        initLanguageSettingsInformation(context);
    }

    public void initLockScreenTime(final Context context) {
        currentScreenTimeout = PowerUtil.getCurrentTimeoutValue(context,
                PowerSettingTimeoutCategory.SCREEN_TIMEOUT);
        deviceSettingLockScreenTime = PowerUtil.getTimeoutEntries(context,
                PowerSettingTimeoutCategory.SCREEN_TIMEOUT);
        deviceSettingLockScreenTimeValue = PowerUtil.getTimeoutEntryValues(context,
                PowerSettingTimeoutCategory.SCREEN_TIMEOUT);
    }

    public void initPowerManager(final Context context) {
        currentTimeoutValue = PowerUtil.getCurrentTimeoutValue(context,
                PowerSettingTimeoutCategory.POWER_OFF_TIMEOUT);
        deviceSettingAutomaticShutdownTime = PowerUtil.getTimeoutEntries(context,
                PowerSettingTimeoutCategory.POWER_OFF_TIMEOUT);
        deviceSettingAutomaticShutdownTimeValue = PowerUtil.getTimeoutEntryValues(context,
                PowerSettingTimeoutCategory.POWER_OFF_TIMEOUT);

        boolean isInvalided = false;
        for (CharSequence timeValue : deviceSettingAutomaticShutdownTimeValue) {
            if (timeValue.equals(currentTimeoutValue)) {
                isInvalided = true;
                break;
            }
        }
        if (!isInvalided && deviceSettingAutomaticShutdownTimeValue.length > 0) {
            currentTimeoutValue = deviceSettingAutomaticShutdownTimeValue[DEFAULT_TIME_VALUE];
            PowerUtil.setCurrentTimeoutValue(context,
                    PowerSettingTimeoutCategory.POWER_OFF_TIMEOUT, Integer.parseInt(currentTimeoutValue.toString()));
        }

        deviceSettingAutomaticShutdownTimeExplain = context.getResources().getStringArray(R.array.device_setting_automatic_shutdown_time_explain);
    }

    public void initDeviceSettingViewEvent(final Context context) {
        deviceSettingViewEvents.put(context.getString(R.string.device_setting_lock_screen_time), new DeviceSettingViewBaseEvent.DeviceSettingLockScreenTimeEvent());
        deviceSettingViewEvents.put(context.getString(R.string.device_setting_automatic_shut_down), new DeviceSettingViewBaseEvent.DeviceSettingAutomaticShutDownEvent());
        deviceSettingViewEvents.put(context.getString(R.string.device_setting_language_settings), new DeviceSettingViewBaseEvent.DeviceSettingLanguageSettingsEvent());
        deviceSettingViewEvents.put(context.getString(R.string.device_setting_device_information), new DeviceSettingViewBaseEvent.DeviceSettingDeviceInformationEvent());
        deviceSettingViewEvents.put(context.getString(R.string.reset), new DeviceSettingViewBaseEvent.DeviceResetInformationEvent());

        deviceSettingViewEvents.put(context.getString(R.string.device_setting_stores_information), new DeviceSettingViewBaseEvent.DeviceSettingViewDeviceStorageEvent());
        deviceSettingViewEvents.put(context.getString(R.string.device_setting_system_update), new DeviceSettingViewBaseEvent.DeviceSettingCheckUpdateEvent());

        deviceSettingViewEvents.put(context.getString(R.string.device_setting_version_update_record), new DeviceSettingViewBaseEvent.DeviceSettingViewSystemVersionHistoryEvent());
        deviceSettingViewEvents.put(context.getString(R.string.device_setting_model), new DeviceSettingViewBaseEvent.OpenSystemSettingEvent());
    }

    private void initDeviceSettingDeviceInformationExplain(final Context context) {
        deviceSettingDeviceInformation = new ArrayList<>();
        //model
        DeviceInformation deviceInformation = new DeviceInformation();
        deviceInformation.title = context.getString(R.string.device_setting_model);
        deviceInformation.deviceInformation = Build.MODEL;
        deviceInformation.isHideFunction = true;
        deviceSettingDeviceInformation.add(deviceInformation);
        //system version
        deviceInformation = new DeviceInformation();
        deviceInformation.title = context.getString(R.string.device_setting_version_number);
        deviceInformation.deviceInformation = Build.DISPLAY;
        deviceSettingDeviceInformation.add(deviceInformation);

        //Serial number
        deviceInformation = new DeviceInformation();
        deviceInformation.title = context.getString(R.string.device_setting_serial_number);
        deviceInformation.deviceInformation = Build.SERIAL;
        deviceSettingDeviceInformation.add(deviceInformation);

        //wifi mac address
        deviceInformation = new DeviceInformation();
        String macAddress = SystemUtils.getMacAddress();
        deviceInformation.title = context.getString(R.string.device_setting_wifi_mac_address);
        deviceInformation.deviceInformation = macAddress;
        deviceSettingDeviceInformation.add(deviceInformation);

        deviceInformation = new DeviceInformation();
        deviceInformation.title = context.getString(R.string.device_setting_stores_information);
        deviceInformation.deviceInformation = context.getString(R.string.device_setting_view_current_storage);
        deviceInformation.isChild = true;
        deviceSettingDeviceInformation.add(deviceInformation);

        deviceInformation = new DeviceInformation();
        deviceInformation.title = context.getString(R.string.device_setting_system_update);
        deviceInformation.deviceInformation = context.getString(R.string.device_setting_check_update);
        deviceInformation.isChild = true;
        deviceSettingDeviceInformation.add(deviceInformation);
    }

    private void initDeviceSettingDeviceStorageInformation(final Context context) {
        deviceStorageInformationList = new ArrayList<>();

        float total = StorageSizeUtil.getTotalStorageAmountInGB();
        total = Utils.formatStorageSize(total);
        BigDecimal free = StorageSizeUtil.getFreeStorageInGB();
        float inUse = new BigDecimal(total - free.floatValue()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();

        DeviceStorageInformation deviceStorageInformation = new DeviceStorageInformation();
        deviceStorageInformation.setTitle(context.getString(R.string.device_setting_local_storage));
        deviceStorageInformation.setInformation(total + context.getString(R.string.storage_gb));//
        deviceStorageInformationList.add(deviceStorageInformation);

        deviceStorageInformation = new DeviceStorageInformation();
        deviceStorageInformation.setTitle(context.getString(R.string.device_setting_available_storage));
        deviceStorageInformation.setInformation(free.floatValue() + context.getString(R.string.storage_gb));//
        deviceStorageInformationList.add(deviceStorageInformation);

        total = StorageSizeUtil.getExtsdStorageAmount();
        String tfCardDetected = context.getString(R.string.device_setting_no_tf_card_detected);
        String tfStorageSpace = context.getString(R.string.device_setting_no);
        String tfStorageFreeSpace = context.getString(R.string.device_setting_no);
        if (total > 0.0f) {
            total = StorageSizeUtil.getTotalStorageAmountInGB();
            tfCardDetected = context.getString(R.string.device_setting_tf_card_detected);
            tfStorageSpace = total + context.getString(R.string.storage_gb);
            free = StorageSizeUtil.getFreeStorageInGB();
            tfStorageFreeSpace = free.floatValue() + context.getString(R.string.storage_gb);
        }


        deviceStorageInformation = new DeviceStorageInformation();
        deviceStorageInformation.setTitle(context.getString(R.string.device_setting_tf_memory_card));
        deviceStorageInformation.setInformation(tfCardDetected);//
        deviceStorageInformationList.add(deviceStorageInformation);

        deviceStorageInformation = new DeviceStorageInformation();
        deviceStorageInformation.setTitle(context.getString(R.string.device_setting_tf_memory_card_storage));
        deviceStorageInformation.setInformation(tfStorageSpace);//
        deviceStorageInformationList.add(deviceStorageInformation);

        deviceStorageInformation = new DeviceStorageInformation();
        deviceStorageInformation.setTitle(context.getString(R.string.device_setting_tf_available_memory_card_storage));
        deviceStorageInformation.setInformation(tfStorageFreeSpace);//
        deviceStorageInformationList.add(deviceStorageInformation);
    }

    public void initDeviceSettingSystemVersionInformation(final Context context) {
        systemVersionInformationList = new ArrayList<>();

        SystemVersionInformation systemVersionInformation = new SystemVersionInformation();
        systemVersionInformation.title = context.getString(R.string.device_setting_current_version);
        systemVersionInformation.information = Build.DISPLAY;
        systemVersionInformationList.add(systemVersionInformation);

//        systemVersionInformation = new SystemVersionInformation();
//        systemVersionInformation.title = context.getString(R.string.device_setting_version_update_record);
//        systemVersionInformation.information = context.getString(R.string.device_setting_version_update_record_history);
//        systemVersionInformation.isChild = true;
//        systemVersionInformationList.add(systemVersionInformation);

        systemVersionInformation = new SystemVersionInformation();
        systemVersionInformation.title = context.getResources().getString(R.string.system_update);
        systemVersionInformation.information = context.getResources().getString(R.string.update_the_system_firmware);
        systemVersionInformation.isChild = true;
        systemVersionInformationList.add(systemVersionInformation);

        systemVersionInformation = new SystemVersionInformation();
        int version = SystemUtils.getAPPVersionCode(context);
        String currentVersion = String.format(SunApplication.getInstance().getString(R.string.current_version), version);
        systemVersionInformation.title = context.getResources().getString(R.string.bookstore_update);
        systemVersionInformation.information = context.getResources().getString(R.string.update_bookstore_application) + currentVersion;
        systemVersionInformation.isChild = true;
        systemVersionInformationList.add(systemVersionInformation);
    }

    public void initLanguageSettingsInformation(final Context context) {
        systemLanguageInformation = SystemLanguage.getSystemLanguageList(context);
    }

    @SuppressLint("StringFormatInvalid")
    public Map<String, CharSequence> getDeviceSettingValue(final Context context) {
        String message = context.getString(R.string.close_screen_prompt);
        deviceSettingValue.put(context.getString(R.string.device_setting_lock_screen_time), currentScreenTimeout);

        message = context.getString(R.string.close_device_prompt);
        deviceSettingValue.put(context.getString(R.string.device_setting_automatic_shut_down), currentScreenTimeout);
        Locale locale = SystemLanguage.getCurrentLanguage(context);
        String currentSystemLanguage = locale.getDisplayName();
        for (int i = 0; i < systemLanguageInformation.localeLanguageInfoList.size(); i++) {
            SystemLanguage.LocaleLanguageInfo localeLanguageInfo = systemLanguageInformation.localeLanguageInfoList.get(i);
            if (locale.getCountry().equals(localeLanguageInfo.getLocale().getCountry())) {
                currentSystemLanguage = localeLanguageInfo.getLabel();
                break;
            }
        }
        deviceSettingValue.put(context.getString(R.string.device_setting_language_settings), currentSystemLanguage);

        deviceSettingValue.put(context.getString(R.string.device_setting_device_information), context.getString(R.string.device_setting_device_information_value));

        deviceSettingValue.put(context.getString(R.string.reset), context.getString(R.string.reset_device_information));

        return deviceSettingValue;
    }

    public static DeviceSettingViewBaseEvent.DeviceSettingBaseEvent getDeviceSettingViewEvent(final String item) {
        if (StringUtils.isNullOrEmpty(item)) {
            return null;
        }
        return deviceSettingViewEvents.get(item);
    }

    public void updateDeviceInformation(final Context context) {
        initDeviceSettingDeviceStorageInformation(context);
    }

    public String[] getDeviceSettingTitle() {
        return deviceSettingTitle;
    }

    public CharSequence[] getDeviceSettingLockScreenTime() {
        return deviceSettingLockScreenTime;
    }

    public CharSequence[] getDeviceSettingLockScreenTimeValue() {
        return deviceSettingLockScreenTimeValue;
    }

    public CharSequence getCurrentScreenTimeout() {
        return currentScreenTimeout;
    }

    public void setCurrentScreenTimeout(final Context context, CharSequence currentScreenTimeout) {
        this.currentScreenTimeout = currentScreenTimeout;

        PowerUtil.setCurrentTimeoutValue(context,
                PowerSettingTimeoutCategory.SCREEN_TIMEOUT, Integer.parseInt((String) currentScreenTimeout));
    }

    public CharSequence[] getDeviceSettingAutomaticShutdownTime() {
        return deviceSettingAutomaticShutdownTime;
    }

    public CharSequence getCurrentTimeoutValue() {
        return currentTimeoutValue;
    }

    public void setCurrentTimeoutValue(final Context context, CharSequence currentTimeoutValue) {
        this.currentTimeoutValue = currentTimeoutValue;
        PowerUtil.setCurrentTimeoutValue(context,
                PowerSettingTimeoutCategory.POWER_OFF_TIMEOUT, Integer.parseInt(currentTimeoutValue.toString()));
    }

    public CharSequence[] getDeviceSettingAutomaticShutdownTimeValue() {
        return deviceSettingAutomaticShutdownTimeValue;
    }

    public CharSequence[] getDeviceSettingAutomaticShutdownTimeExplain() {
        return deviceSettingAutomaticShutdownTimeExplain;
    }

    public List<DeviceInformation> getDeviceSettingDeviceInformation() {
        return deviceSettingDeviceInformation;
    }

    public List<DeviceStorageInformation> getDeviceStorageInformationList() {
        return deviceStorageInformationList;
    }

    public List<SystemVersionInformation> getSystemVersionInformationList() {
        return systemVersionInformationList;
    }

    public SystemLanguageInformation getLocaleLanguageInfoList() {
        return systemLanguageInformation;
    }
}
