package com.onyx.android.dr.reader.common;

import android.content.Context;

import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.utils.PreferenceManager;

/**
 * Created by huxiaomao on 17/5/12.
 */

public class ReadSettingFontSizeConfig {
    public static final String READ_SETTING_FONT_SIZE_KEY = "ReadSettingFontSizeKey";
    public static final int READ_SETTING_FONT_SIZE_ONE = 1;
    public static final int READ_SETTING_FONT_SIZE_TWO = 2;
    public static final int READ_SETTING_FONT_SIZE_THREE = 3;
    public static final int READ_SETTING_FONT_SIZE_FOUR = 4;
    public static final int READ_SETTING_FONT_SIZE_FIVE = 5;
    public static final int READ_SETTING_FONT_SIZE_SIX = 6;
    public static final int READ_SETTING_FONT_SIZE_SEVEN = 7;

    public static void saveReadSettingFontSize(ReaderPresenter readerPresenter, int value) {
        if(readerPresenter.getReaderViewInfo().isTextPages()) {
            PreferenceManager.setIntValue(readerPresenter.getReaderView().getViewContext(), READ_SETTING_FONT_SIZE_KEY, value);
            ReaderTextStyle readerTextStyle = readerPresenter.getReaderViewInfo().getReaderTextStyle();
            final ReaderTextStyle.SPUnit size = readerTextStyle.DEFAULT_FONT_SIZE_LIST[value];
            readerTextStyle.setFontSize(size);
            readerPresenter.getBookOperate().updateReaderStyle();
        }
    }

    public static int getReadSettingFontSize(Context context) {
        return PreferenceManager.getIntValue(context, READ_SETTING_FONT_SIZE_KEY,
                READ_SETTING_FONT_SIZE_THREE);
    }

    public static void setDefaultReadSettingFontSize(ReaderPresenter readerPresenter){
        if(readerPresenter.getReaderViewInfo().isTextPages()) {
            int value = getReadSettingFontSize(readerPresenter.getReaderView().getViewContext());
            ReaderTextStyle readerTextStyle = readerPresenter.getReaderViewInfo().getReaderTextStyle();
            final ReaderTextStyle.SPUnit size = readerTextStyle.DEFAULT_FONT_SIZE_LIST[value];
            readerTextStyle.setFontSize(size);
        }
    }
}
