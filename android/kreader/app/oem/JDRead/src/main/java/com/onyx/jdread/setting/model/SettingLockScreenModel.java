package com.onyx.jdread.setting.model;

import android.content.Context;

import com.onyx.android.libsetting.data.PowerSettingTimeoutCategory;
import com.onyx.android.libsetting.util.PowerUtil;
import com.onyx.jdread.JDReadApplication;

/**
 * Created by li on 2017/12/21.
 */

public class SettingLockScreenModel {
    private CharSequence[] lockScreenTimes;
    private CharSequence[] lockScreenValues;
    private String currentTime;

    public CharSequence[] getLockScreenTimes() {
        return lockScreenTimes = PowerUtil.getTimeoutEntries(JDReadApplication.getInstance(),
                PowerSettingTimeoutCategory.SCREEN_TIMEOUT);
    }

    public CharSequence[] getLockScreenValues() {
        return lockScreenValues = PowerUtil.getTimeoutEntryValues(JDReadApplication.getInstance(),
                PowerSettingTimeoutCategory.SCREEN_TIMEOUT);
    }

    public String getCurrentTime() {
        return currentTime = PowerUtil.getCurrentTimeoutValue(JDReadApplication.getInstance(),
                PowerSettingTimeoutCategory.SCREEN_TIMEOUT);
    }

    public void setCurrentTime(String currentScreenTime) {
        PowerUtil.setCurrentTimeoutValue(JDReadApplication.getInstance(),
                PowerSettingTimeoutCategory.SCREEN_TIMEOUT, Integer.parseInt(currentScreenTime));
    }
}
