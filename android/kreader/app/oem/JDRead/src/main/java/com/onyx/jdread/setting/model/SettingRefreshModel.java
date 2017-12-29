package com.onyx.jdread.setting.model;

import android.content.Context;

import com.onyx.android.sdk.reader.dataprovider.LegacySdkDataUtils;
import com.onyx.android.sdk.reader.device.ReaderDeviceManager;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;

import java.util.regex.Pattern;

/**
 * Created by li on 2017/12/21.
 */

public class SettingRefreshModel {
    private String[] refreshPages;
    private String currentRefreshPage;
    private static final String REFRESH_RATE = "refresh_rate";

    public String getCurrentRefreshPage() {
        return currentRefreshPage = PreferenceManager.getStringValue(JDReadApplication.getInstance(), REFRESH_RATE, JDReadApplication.getInstance().getResources().getString(R.string.device_setting_ten_pages));
    }

    public void setCurrentPageRefreshPage(String currentPageRefreshTime) {
        PreferenceManager.setStringValue(JDReadApplication.getInstance(), REFRESH_RATE, currentPageRefreshTime);
        String refreshTime = Pattern.compile("[^0-9]").matcher(currentPageRefreshTime).replaceAll("");
        LegacySdkDataUtils.setScreenUpdateGCInterval(JDReadApplication.getInstance(), Integer.valueOf(refreshTime));
        ReaderDeviceManager.setGcInterval(Integer.valueOf(refreshTime));
    }

    public String[] getRefreshPages() {
        return refreshPages = JDReadApplication.getInstance().getResources().getStringArray(R.array.device_setting_page_refreshes);
    }
}
