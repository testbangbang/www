package com.onyx.android.dr.reader.common;

import android.content.Context;

import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.utils.PreferenceManager;

/**
 * Created by huxiaomao on 17/5/12.
 */

public class ReadSettingMarginConfig {
    public static final String READ_SETTING_MARGIN_KEY = "ReadSettingMarginKey";
    public static final int READ_SETTING_MARGIN_SMALL = 0;
    public static final int READ_SETTING_MARGIN_MID = 1;
    public static final int READ_SETTING_MARGIN_BIG = 2;

    public static ReaderTextStyle.PageMargin getReadSettingMarginValue(int vaule) {
        ReaderTextStyle.PageMargin currentPageMargin;
        switch (vaule) {
            case READ_SETTING_MARGIN_SMALL:
                currentPageMargin = ReaderTextStyle.PageMargin.copy(ReaderTextStyle.SMALL_PAGE_MARGIN);
                break;
            case READ_SETTING_MARGIN_MID:
                currentPageMargin = ReaderTextStyle.PageMargin.copy(ReaderTextStyle.NORMAL_PAGE_MARGIN);
                break;
            case READ_SETTING_MARGIN_BIG:
                currentPageMargin = ReaderTextStyle.PageMargin.copy(ReaderTextStyle.LARGE_PAGE_MARGIN);
                break;
            default:
                currentPageMargin = ReaderTextStyle.PageMargin.copy(ReaderTextStyle.NORMAL_PAGE_MARGIN);
        }
        return currentPageMargin;
    }

    public static void saveReadSettingMargin(ReaderPresenter readerPresenter, int value) {
        if(readerPresenter.getReaderViewInfo().isTextPages()) {
            PreferenceManager.setIntValue(readerPresenter.getReaderView().getViewContext(),
                    ReadSettingMarginConfig.READ_SETTING_MARGIN_KEY, value);
            ReaderTextStyle readerTextStyle = readerPresenter.getReaderViewInfo().readerTextStyle;
            ReaderTextStyle.PageMargin currentPageMargin = getReadSettingMarginValue(value);
            readerTextStyle.setPageMargin(currentPageMargin);
            readerPresenter.getBookOperate().updateReaderStyle();
        }
    }

    public static int getReadSettingMargin(Context context) {
        return PreferenceManager.getIntValue(context, ReadSettingMarginConfig.READ_SETTING_MARGIN_KEY,
                ReadSettingMarginConfig.READ_SETTING_MARGIN_SMALL);
    }

    public static void setDefaultReadSettingMargin(ReaderPresenter readerPresenter){
        if(readerPresenter.getReaderViewInfo().isTextPages()) {
            int value = getReadSettingMargin(readerPresenter.getReaderView().getViewContext());
            ReaderTextStyle readerTextStyle = readerPresenter.getReaderViewInfo().getReaderTextStyle();
            ReaderTextStyle.PageMargin currentPageMargin = getReadSettingMarginValue(value);
            readerTextStyle.setPageMargin(currentPageMargin);
        }
    }
}
