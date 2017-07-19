package com.onyx.android.dr.reader.common;

import android.content.Context;

import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.sdk.utils.PreferenceManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huxiaomao on 17/5/15.
 */

public class ReadSettingTtsConfig {
    public static final String READ_SETTING_FONT_SIZE_KEY = "ReadSettingTtsKey";
    public static final int SPEED_RATE_ONE = 0;
    public static final int SPEED_RATE_TWO = 1;
    public static final int SPEED_RATE_THREE = 2;
    public static final int SPEED_RATE_FOUR = 3;
    public static final int SPEED_RATE_FIVE = 4;
    public static final Map<Integer, Float> SpeedRateList = new HashMap<>();

    public static final float SLOWEST_SPEED = 0.5f;
    public static final float SLOWER_SPEED = 0.75f;
    public static final float NORMAL_SPEED = 1.0f;
    public static final float FASTER_SPEED = 1.5f;
    public static final float FASTEST_SPEED = 2.0f;

    static {
        SpeedRateList.put(SPEED_RATE_ONE, SLOWEST_SPEED);
        SpeedRateList.put(SPEED_RATE_TWO, SLOWER_SPEED);
        SpeedRateList.put(SPEED_RATE_THREE, NORMAL_SPEED);
        SpeedRateList.put(SPEED_RATE_FOUR, FASTER_SPEED);
        SpeedRateList.put(SPEED_RATE_FIVE, FASTEST_SPEED);
    }

    public static void saveSpeechRate(ReaderPresenter readerPresenter, int value) {
        PreferenceManager.setIntValue(readerPresenter.getReaderView().getViewContext(),
                READ_SETTING_FONT_SIZE_KEY, value);
    }

    public static int getSpeechRate(Context context) {
        return PreferenceManager.getIntValue(context, READ_SETTING_FONT_SIZE_KEY, SPEED_RATE_THREE);
    }

    public static float getSpeechRateValue(int value) {
        return SpeedRateList.get(value);
    }

    public static float getSaveSpeechRate(Context context) {
        return getSpeechRateValue(getSpeechRate(context));
    }
}
