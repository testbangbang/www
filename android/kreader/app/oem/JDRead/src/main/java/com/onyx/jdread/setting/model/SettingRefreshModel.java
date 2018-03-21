package com.onyx.jdread.setting.model;

import android.databinding.ObservableBoolean;

import com.onyx.android.sdk.reader.device.ReaderDeviceManager;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ResManager;

import java.util.Observable;

/**
 * Created by li on 2017/12/21.
 */

public class SettingRefreshModel extends Observable {
    private String[] refreshPages;
    private String currentRefreshPage;
    public static final String REFRESH_RATE = "refresh_rate";
    public final ObservableBoolean isSpeedRefresh = new ObservableBoolean(false);

    public SettingRefreshModel() {
        isSpeedRefresh.set(JDPreferenceManager.getBooleanValue(R.string.speed_refresh_key, false));
    }

    public int getCurrentRefreshPage() {
        return JDPreferenceManager.getIntValue(REFRESH_RATE, ResManager.getInteger(R.integer.default_refresh_value));
    }

    public void setCurrentPageRefreshPage(int currentPageRefreshTime) {
        JDPreferenceManager.setIntValue(REFRESH_RATE, currentPageRefreshTime);
        ReaderDeviceManager.setGcInterval(currentPageRefreshTime);
    }

    public String[] getRefreshPages() {
        return refreshPages = ResManager.getStringArray(R.array.device_setting_page_refreshes);
    }

    public int[] getRefreshValues() {
        return ResManager.getIntArray(R.array.device_setting_refresh_values);
    }
}
