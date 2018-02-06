package com.onyx.jdread.reader.menu.common;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.api.ReaderChineseConvertType;
import com.onyx.jdread.main.common.JDPreferenceManager;

/**
 * Created by huxiaomao on 2018/1/6.
 */

public class ReaderConfig {
    public static final String TYPEFACE_ROOT_PATH = "/sdcard/fonts/";
    public static final int HIT_TEST_TEXT_STEP = 30;

    public static class FontSize {
        public static final int FONT_SIZE_X_SMALL = 20;
        public static final int FONT_SIZE_SMALL = 22;
        public static final int FONT_SIZE_MEDIUM = 27;
        public static final int FONT_SIZE_LARGE = 29;
        public static final int FONT_SIZE_X_LARGE = 33;
        public static final int FONT_SIZE_XX_LARGE = 40;
        public static final int DEFAULT_FONT_SIZE = FONT_SIZE_MEDIUM;
    }

    public static class Typeface {
        public static final String TYPEFACE_ONE = TYPEFACE_ROOT_PATH + "FZHT_GB18030.TTF";
        public static final String TYPEFACE_TWO = TYPEFACE_ROOT_PATH + "PTF55.otf";
        public static final String TYPEFACE_THREE = TYPEFACE_ROOT_PATH + "GEInspRg.TTF";
        public static final String TYPEFACE_FOUR = TYPEFACE_ROOT_PATH + "GEInspBd.ttf";
        public static final String DEFAULT_TYPEFACE = TYPEFACE_ONE;
    }

    public static class TypefaceColorDepth{
        public static final int LEVEL_ONE = 0;
        public static final int LEVEL_TWO = 1;
        public static final int LEVEL_THREE = 2;
        public static final int LEVEL_FOUR = 3;
        public static final int LEVEL_FIVE = 4;
        public static final int LEVEL_SIX = 5;
        public static final int DEFAULT_COLOR_DEPTH = LEVEL_ONE;
    }

     public static final int SIGN_RIGHT_MARGIN = 10;

    public static final String READER_FONTFACE_KEY = "FontFace";
    public static final String READER_FONTSIZE_KEY = "FontSize";
    public static final String READER_PARAGRAPHSPACING_KEY = "ParagraphSpacing";
    public static final String READER_LINESPACING_KEY = "LineSpacing";
    public static final String READER_PARAGRAPHINDENT_KEY = "ParagraphIndent";
    public static final String READER_LEFT_MARGIN_KEY = "LeftMargin";
    public static final String READER_TOP_MARGIN_KEY = "TopMargin";
    public static final String READER_RIGHT_MARGIN_KEY = "RightMargin";
    public static final String READER_BOTTOM_MARGIN_KEY = "BottomMargin";
    public static final String READER_CHINESE_CONVERT_TYPE_KEY = "ChineseConvertType";
    public static final String READER_EMBOLDENLEVEL_KEY = "emboldenLevel";
    public static final int CHINESE = 1;
    public static final int SIMPLIFIED = 2;

    public static void setReaderChineseConvertType(ReaderChineseConvertType type){
        if(ReaderChineseConvertType.TRADITIONAL_TO_SIMPLIFIED == type ||
                ReaderChineseConvertType.NONE == type) {
            JDPreferenceManager.setIntValue(ReaderConfig.READER_CHINESE_CONVERT_TYPE_KEY, ReaderConfig.CHINESE);
        }else{
            JDPreferenceManager.setIntValue(ReaderConfig.READER_CHINESE_CONVERT_TYPE_KEY, ReaderConfig.SIMPLIFIED);
        }
    }

    public static void setEmboldenLevel(int emboldenLevel){
        JDPreferenceManager.setIntValue(ReaderConfig.READER_EMBOLDENLEVEL_KEY, emboldenLevel);
    }

    public static void saveUserSetting(final ReaderTextStyle style){
        JDPreferenceManager.setStringValue(ReaderConfig.READER_FONTFACE_KEY,style.getFontFace());
        JDPreferenceManager.setIntValue(ReaderConfig.READER_FONTSIZE_KEY,(int)style.getFontSize().getValue());
        JDPreferenceManager.setIntValue(ReaderConfig.READER_PARAGRAPHSPACING_KEY,style.getParagraphSpacing().getPercent());

        JDPreferenceManager.setIntValue(ReaderConfig.READER_LINESPACING_KEY,style.getLineSpacing().getPercent());

        JDPreferenceManager.setIntValue(ReaderConfig.READER_PARAGRAPHINDENT_KEY,style.getIndent().getIndent());

        JDPreferenceManager.setIntValue(ReaderConfig.READER_LEFT_MARGIN_KEY,style.getPageMargin().getLeftMargin().getPercent());

        JDPreferenceManager.setIntValue(ReaderConfig.READER_TOP_MARGIN_KEY,style.getPageMargin().getTopMargin().getPercent());

        JDPreferenceManager.setIntValue(ReaderConfig.READER_RIGHT_MARGIN_KEY,style.getPageMargin().getRightMargin().getPercent());
        JDPreferenceManager.setIntValue(ReaderConfig.READER_BOTTOM_MARGIN_KEY,style.getPageMargin().getBottomMargin().getPercent());
    }

    public static ReaderChineseConvertType getReaderChineseConvertType(){
        ReaderChineseConvertType chineseConvertType;
        int type = JDPreferenceManager.getIntValue(ReaderConfig.READER_CHINESE_CONVERT_TYPE_KEY, ReaderConfig.CHINESE);
        if(type == CHINESE) {
            chineseConvertType = ReaderChineseConvertType.TRADITIONAL_TO_SIMPLIFIED;
        }else{
            chineseConvertType = ReaderChineseConvertType.SIMPLIFIED_TO_TRADITIONAL;
        }
        return chineseConvertType;
    }
}
