package com.onyx.android.dr.devicesetting.data.util;

import android.content.Context;
import android.provider.Settings;

import com.onyx.android.dr.R;
import com.onyx.android.dr.devicesetting.data.PowerSettingTimeoutCategory;

import java.util.List;

/**
 * Created by solskjaer49 on 2016/12/9 17:26.
 */

public class PowerUtil {
    public static boolean isWakeUpFrontLightEnabled(Context context) {
        if(SettingConfig.sharedInstance(context).getSystemWakeUpFrontLightKey() != null) {
            return Settings.System.getInt(
                    context.getContentResolver(),
                    SettingConfig.sharedInstance(context).getSystemWakeUpFrontLightKey(), 0) != 0;
        }else{
            return false;
        }
    }

    public static void setWakeUpFrontLightEnabled(Context context, Boolean enabled) {
        if(SettingConfig.sharedInstance(context).getSystemWakeUpFrontLightKey() != null) {
            SystemSettingUtil.changeSystemConfigIntegerValue(context,
                    SettingConfig.sharedInstance(context).getSystemWakeUpFrontLightKey(),
                    enabled.compareTo(false));
        }
    }

    public static String[] getTimeoutEntries(Context context,
                                                   @PowerSettingTimeoutCategory.PowerSettingTimeoutCategoryDef int timeoutCategory) {
        SettingConfig config = SettingConfig.sharedInstance(context);
        List<Integer> timeoutValueList = config.getTimeoutValues(timeoutCategory);
        String[] entriesArray = new String[timeoutValueList.size()];
        for (int i = 0; i < timeoutValueList.size(); i++) {
            int ms = timeoutValueList.get(i);
            if (ms == -1) {
                entriesArray[i] = (context.getString(R.string.never_sleep));
            } else {
                entriesArray[i] = CommonUtil.msToMinuteStringWithUnit(context, ms);
            }

        }
        return entriesArray;
    }

    public static String getTimeoutDisplayName(final Context context,final int ms){
        if (ms == -1) {
            return (context.getString(R.string.never_sleep));
        } else {
            return CommonUtil.msToMinuteStringWithUnit(context, ms);
        }
    }

    public static String[] getTimeoutEntryValues(Context context,
                                                       @PowerSettingTimeoutCategory.PowerSettingTimeoutCategoryDef int timeoutCategory) {
        SettingConfig config = SettingConfig.sharedInstance(context);
        List<Integer> timeoutValueList = config.getTimeoutValues(timeoutCategory);
        String[] entryValueArray = new String[timeoutValueList.size()];
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
