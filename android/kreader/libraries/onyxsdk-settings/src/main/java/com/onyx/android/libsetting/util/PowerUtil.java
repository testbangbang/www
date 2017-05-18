package com.onyx.android.libsetting.util;

import android.content.Context;
import android.provider.Settings;

import com.onyx.android.libsetting.R;
import com.onyx.android.libsetting.SettingConfig;
import com.onyx.android.libsetting.data.PowerSettingTimeoutCategory;

import java.util.List;

/**
 * Created by solskjaer49 on 2016/12/9 17:26.
 */

public class PowerUtil {
    public static boolean isWakeUpFrontLightEnabled(Context context) {
        return Settings.System.getInt(
                context.getContentResolver(),
                SettingConfig.sharedInstance(context).getSystemWakeUpFrontLightKey(), 0) != 0;
    }

    public static void setWakeUpFrontLightEnabled(Context context, Boolean enabled) {
        SystemSettingUtil.changeSystemConfigIntegerValue(context,
                SettingConfig.sharedInstance(context).getSystemWakeUpFrontLightKey(),
                enabled.compareTo(false));
    }

    public static CharSequence[] getTimeoutEntries(Context context,
                                                   @PowerSettingTimeoutCategory.PowerSettingTimeoutCategoryDef int timeoutCategory) {
        SettingConfig config = SettingConfig.sharedInstance(context);
        List<Integer> timeoutValueList = config.getTimeoutValues(timeoutCategory);
        CharSequence[] entriesArray = new CharSequence[timeoutValueList.size()];
        for (int i = 0; i < timeoutValueList.size(); i++) {
            int ms = timeoutValueList.get(i);
            if (ms == -1) {
                entriesArray[i] = (context.getString(R.string.never_sleep));
            } else {
                entriesArray[i] = CommonUtil.msToTimeStringWithUnit(context, ms);
            }

        }
        return entriesArray;
    }

    public static CharSequence[] getTimeoutEntryValues(Context context,
                                                       @PowerSettingTimeoutCategory.PowerSettingTimeoutCategoryDef int timeoutCategory) {
        SettingConfig config = SettingConfig.sharedInstance(context);
        List<Integer> timeoutValueList = config.getTimeoutValues(timeoutCategory);
        CharSequence[] entryValueArray = new CharSequence[timeoutValueList.size()];
        for (int i = 0; i < timeoutValueList.size(); i++) {
            int ms = timeoutValueList.get(i);
            entryValueArray[i] = Integer.toString(ms);
        }
        return entryValueArray;
    }

    public static String getCurrentTimeoutValue(Context context,
                                                @PowerSettingTimeoutCategory.PowerSettingTimeoutCategoryDef int timeoutCategory) {
        return Integer.toString(Settings.System.getInt(
                context.getContentResolver(), getTimeoutDataKeyByCategory(context, timeoutCategory), -1));
    }

    public static int getCurrentTimeoutIntValue(Context context,
                                                @PowerSettingTimeoutCategory.PowerSettingTimeoutCategoryDef int timeoutCategory) {
        return Settings.System.getInt(
                context.getContentResolver(), getTimeoutDataKeyByCategory(context, timeoutCategory), -1);
    }

    private static String getTimeoutDataKeyByCategory(Context context,
                                                      @PowerSettingTimeoutCategory.PowerSettingTimeoutCategoryDef int timeoutCategory) {
        String key = null;
        SettingConfig config = SettingConfig.sharedInstance(context);
        switch (timeoutCategory) {
            case PowerSettingTimeoutCategory.POWER_OFF_TIMEOUT:
                key = config.getSystemAutoPowerOffKey();
                break;
            case PowerSettingTimeoutCategory.SCREEN_TIMEOUT:
                key = config.getSystemScreenOffKey();
                break;
            case PowerSettingTimeoutCategory.WIFI_INACTIVITY_TIMEOUT:
                key = config.getSystemWifiInactivityKey();
                break;
        }
        return key;
    }

    public static void setCurrentTimeoutValue(Context context,
                                              @PowerSettingTimeoutCategory.PowerSettingTimeoutCategoryDef int timeoutCategory, int value) {
        SystemSettingUtil.changeSystemConfigIntegerValue(context,
                getTimeoutDataKeyByCategory(context, timeoutCategory),
                value);
    }
}
