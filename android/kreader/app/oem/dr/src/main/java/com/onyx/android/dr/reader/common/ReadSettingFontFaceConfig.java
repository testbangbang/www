package com.onyx.android.dr.reader.common;


import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.utils.PreferenceManager;

/**
 * Created by huxiaomao on 17/5/12.
 */

public class ReadSettingFontFaceConfig {
    public static final String FONT_FACE_ROOT_PATH = "/mnt/sdcard/font/";
    public static final String FANGZHENG_FANGSONG = "fangzheng_fangsong.ttf";
    public static final String FANGZHENG_KAITI = "fangzheng_kaiti.ttf";
    public static final String FANGZHENG_LANTING_HEI = "fangzheng_lanting_hei.ttf";
    public static final String FANGZHENG_MIAO_WUHEI = "fangzheng_miao_wuhei.ttf";
    public static final String FANGZHENG_LANTING_SIX = "fangzheng_lanting_hei.ttf";

    public static final int READ_SETTING_FONT_FACE_ONE = 1;
    public static final int READ_SETTING_FONT_FACE_TWO = 2;
    public static final int READ_SETTING_FONT_FACE_THREE = 3;
    public static final int READ_SETTING_FONT_FACE_FOUR = 4;
    public static final int READ_SETTING_FONT_FACE_FIVE = 5;
    public static final int READ_SETTING_FONT_FACE_SIX = 6;

    public static String getReadSettingFontFaceValue(int value) {
        String fontPath = FONT_FACE_ROOT_PATH + FANGZHENG_FANGSONG;
        switch (value) {
            case READ_SETTING_FONT_FACE_ONE:
                fontPath = "";
                break;
            case READ_SETTING_FONT_FACE_TWO:
                fontPath = FONT_FACE_ROOT_PATH + FANGZHENG_KAITI;
                break;
            case READ_SETTING_FONT_FACE_THREE:
                fontPath = FONT_FACE_ROOT_PATH + FANGZHENG_LANTING_HEI;
                break;
            case READ_SETTING_FONT_FACE_FOUR:
                fontPath = FONT_FACE_ROOT_PATH + FANGZHENG_MIAO_WUHEI;
                break;
            case READ_SETTING_FONT_FACE_FIVE:
                fontPath = FONT_FACE_ROOT_PATH + FANGZHENG_FANGSONG;
                break;
            case READ_SETTING_FONT_FACE_SIX:
                fontPath = FONT_FACE_ROOT_PATH + FANGZHENG_LANTING_SIX;
                break;
        }
        return fontPath;
    }

    public static void saveReadSettingFontFace(ReaderPresenter readerPresenter, int value) {
        //book md5 save font type
        if (readerPresenter.getReaderViewInfo().isTextPages()) {
            String bookMd5 = readerPresenter.getReader().getDocumentMd5();
            PreferenceManager.setIntValue(readerPresenter.getReaderView().getViewContext(),
                    bookMd5, value);
            ReaderTextStyle readerTextStyle = readerPresenter.getReaderViewInfo().readerTextStyle;
            if (readerTextStyle != null) {
                String fontPath = getReadSettingFontFaceValue(value);
                readerTextStyle.setFontFace(fontPath);
                readerPresenter.getBookOperate().updateReaderStyle();
            }
        }
    }

    public static int getReadSettingFontFace(ReaderPresenter readerPresenter) {
        String bookMd5 = readerPresenter.getReader().getDocumentMd5();
        return PreferenceManager.getIntValue(readerPresenter.getReaderView().getViewContext(), bookMd5,
                READ_SETTING_FONT_FACE_ONE);
    }

    public static void setDefaultReadSettingFontFace(ReaderPresenter readerPresenter) {
        if (readerPresenter.getReaderViewInfo().isTextPages()) {
            int value = getReadSettingFontFace(readerPresenter);
            String fontPath = getReadSettingFontFaceValue(value);
            ReaderTextStyle readerTextStyle = readerPresenter.getReaderViewInfo().readerTextStyle;
            readerTextStyle.setFontFace(fontPath);
        }
    }
}
