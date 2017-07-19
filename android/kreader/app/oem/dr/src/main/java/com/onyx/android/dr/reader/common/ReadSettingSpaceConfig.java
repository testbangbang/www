package com.onyx.android.dr.reader.common;

import android.content.Context;

import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.utils.PreferenceManager;

/**
 * Created by huxiaomao on 17/5/12.
 */

public class ReadSettingSpaceConfig {
    public static final String READ_SETTING_SPACE_KEY = "ReadSettingSpaceKey";
    public static final int READ_SETTING_SPACE_SMALL = 0;
    public static final int READ_SETTING_SPACE_MID = 1;
    public static final int READ_SETTING_SPACE_BIG = 2;

    public static void saveReadSettingLineSpace(ReaderPresenter readerPresenter, int value) {
        if(readerPresenter.getReaderViewInfo().isTextPages()) {
            PreferenceManager.setIntValue(readerPresenter.getReaderView().getViewContext(),
                    ReadSettingSpaceConfig.READ_SETTING_SPACE_KEY, value);
            ReaderTextStyle readerTextStyle = readerPresenter.getReaderViewInfo().readerTextStyle;
            ReaderTextStyle.Percentage percentage = getReadSettingLineSpaceValue(readerTextStyle, value);
            readerTextStyle.setLineSpacing(percentage);
            readerPresenter.getBookOperate().updateReaderStyle();
        }
    }

    public static int getReadSettingLineSpace(Context context) {
        return PreferenceManager.getIntValue(context, ReadSettingSpaceConfig.READ_SETTING_SPACE_KEY,
                ReadSettingSpaceConfig.READ_SETTING_SPACE_MID);
    }

    public static void setDefaultReadSettingLineSpace(ReaderPresenter readerPresenter){
        if(readerPresenter.getReaderViewInfo().isTextPages()) {
            int value = getReadSettingLineSpace(readerPresenter.getReaderView().getViewContext());
            ReaderTextStyle readerTextStyle = readerPresenter.getReaderViewInfo().getReaderTextStyle();
            ReaderTextStyle.Percentage percentage = getReadSettingLineSpaceValue(readerTextStyle, value);
            readerTextStyle.setLineSpacing(percentage);
        }
    }

    private static ReaderTextStyle.Percentage getReadSettingLineSpaceValue(ReaderTextStyle readerTextStyle,int value){
        ReaderTextStyle.Percentage percentage = readerTextStyle.getLineSpacing();
        switch (value){
            case READ_SETTING_SPACE_SMALL:
                percentage.setPercent(ReaderTextStyle.SMALL_LINE_SPACING.getPercent());
                break;
            case READ_SETTING_SPACE_MID:
                percentage.setPercent(ReaderTextStyle.NORMAL_LINE_SPACING.getPercent());
                break;
            case READ_SETTING_SPACE_BIG:
                percentage.setPercent(ReaderTextStyle.LARGE_LINE_SPACING.getPercent());
                break;
            default:
                percentage.setPercent(ReaderTextStyle.NORMAL_LINE_SPACING.getPercent());
        }
        return percentage;
    }
}
