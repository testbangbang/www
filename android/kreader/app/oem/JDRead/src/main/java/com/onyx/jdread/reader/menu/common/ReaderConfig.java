package com.onyx.jdread.reader.menu.common;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.api.ReaderChineseConvertType;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.reader.data.SettingInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huxiaomao on 2018/1/6.
 */

public class ReaderConfig {
    public static final String TYPEFACE_ROOT_PATH = "/sdcard/fonts/";
    public static final int HIT_TEST_TEXT_STEP = 30;

    public static class Typeface {
        public static final String TYPEFACE_ONE = TYPEFACE_ROOT_PATH + "FZHei-B01.TTF";
        public static final String TYPEFACE_TWO = TYPEFACE_ROOT_PATH + "FZNewKai_GB18030-Z03.ttf";
        public static final String TYPEFACE_THREE = TYPEFACE_ROOT_PATH + "FZNewShuSong_GB18030-Z10.ttf";
        public static final String TYPEFACE_FOUR = TYPEFACE_ROOT_PATH + "FZYouH_507R.ttf";
        public static final String DEFAULT_TYPEFACE = TYPEFACE_ONE;
    }

    public static class TypefaceColorDepth {
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
    public static final String READER_CHINESE_CONVERT_TYPE_KEY = "ChineseConvertType";
    public static final String READER_EMBOLDENLEVEL_KEY = "emboldenLevel";
    public static final int CHINESE = 1;
    public static final int SIMPLIFIED = 2;


    public static final int SETTING_ONE_STYLE_KEY = 0;
    public static final int SETTING_TWO_STYLE_KEY = 1;
    public static final int SETTING_THREE_STYLE_KEY = 2;
    public static final int SETTING_FOUR_STYLE_KEY = 3;
    public static final int SETTING_FIVE_STYLE_KEY = 4;
    public static final int SETTING_SIX_STYLE_KEY = 5;
    public static final int CUSTOM_STYLE_KEY = 6;

    public static final int SETTING_TYPE_PRESET = 1;
    public static final int SETTING_TYPE_CUSTOM = 2;
    public static final int DEFAULT_SETTING_TYPE = SETTING_TYPE_PRESET;


    public static final String SETTING_TYPE_KEY = "setting_type_key";
    public static final String SETTING_STYLE_KEY = "setting_style_key";

    public static Map<Integer, ReaderTextStyle> presetStyle = new HashMap<>();
    public static final int DEFAULT_PRESET_STYLE = SETTING_THREE_STYLE_KEY;

    public static final int FONT_SIZE_X_SMALL = 20;
    public static final int FONT_SIZE_SMALL = 23;
    public static final int FONT_SIZE_MEDIUM = 26;
    public static final int FONT_SIZE_LARGE = 29;
    public static final int FONT_SIZE_X_LARGE = 34;
    public static final int FONT_SIZE_XX_LARGE = 40;

    static {
        presetStyle.put(SETTING_ONE_STYLE_KEY, new ReaderTextStyle(6, 6, 14, 24, FONT_SIZE_X_SMALL, 135, 50));
        presetStyle.put(SETTING_TWO_STYLE_KEY, new ReaderTextStyle(6, 6, 14, 24, FONT_SIZE_SMALL, 140, 60));
        presetStyle.put(SETTING_THREE_STYLE_KEY, new ReaderTextStyle(6, 6, 14, 24, FONT_SIZE_MEDIUM, 130, 60));
        presetStyle.put(SETTING_FOUR_STYLE_KEY, new ReaderTextStyle(6, 6, 14, 24, FONT_SIZE_LARGE, 130, 60));
        presetStyle.put(SETTING_FIVE_STYLE_KEY, new ReaderTextStyle(6, 6, 14, 24, FONT_SIZE_X_LARGE, 130, 60));
        presetStyle.put(SETTING_SIX_STYLE_KEY, new ReaderTextStyle(6, 6, 14, 24, FONT_SIZE_XX_LARGE, 130, 60));
        presetStyle.put(CUSTOM_STYLE_KEY, new ReaderTextStyle(0, 0, 0, 0, 0, 0, 0));
    }

    public static Map<Integer, Integer> customLineSpacing = new HashMap<>();
    public static String CUSTOM_LINE_SPACING_KEY = "customLineSpacing";
    public static final int DEFAULT_CUSTOM_LINE_SPACING = SETTING_TWO_STYLE_KEY;
    static {
        customLineSpacing.put(SETTING_ONE_STYLE_KEY, 100);
        customLineSpacing.put(SETTING_TWO_STYLE_KEY, 125);
        customLineSpacing.put(SETTING_THREE_STYLE_KEY, 150);
        customLineSpacing.put(SETTING_FOUR_STYLE_KEY, 175);
        customLineSpacing.put(SETTING_FIVE_STYLE_KEY, 200);
        customLineSpacing.put(SETTING_SIX_STYLE_KEY, 225);
    }

    public static Map<Integer, Integer> customParagraphSpacing = new HashMap<>();
    public static String CUSTOM_PARAGRAPH_SPACING_KEY = "customParagraphSpacing";
    public static final int DEFAULT_CUSTOM_PARAGRAPH_SPACING = SETTING_TWO_STYLE_KEY;
    static {
        customParagraphSpacing.put(SETTING_ONE_STYLE_KEY, 60);
        customParagraphSpacing.put(SETTING_TWO_STYLE_KEY, 70);
        customParagraphSpacing.put(SETTING_THREE_STYLE_KEY, 80);
        customParagraphSpacing.put(SETTING_FOUR_STYLE_KEY, 90);
        customParagraphSpacing.put(SETTING_FIVE_STYLE_KEY, 100);
        customParagraphSpacing.put(SETTING_SIX_STYLE_KEY, 110);
    }

    public static Map<Integer, TopAndBottom> customTopAndBottomMargin = new HashMap<>();
    public static String CUSTOM_TOPANDBOTTOM_MARGIN_KEY = "customTopAndBottomMargin";
    public static final int DEFAULT_CUSTOM_TOPANDBOTTOM_MARGIN = SETTING_SIX_STYLE_KEY;
    public static class TopAndBottom {
        public int top;
        public int bottom;

        public TopAndBottom(int top, int bottom) {
            this.top = top;
            this.bottom = bottom;
        }
    }

    static {
        customTopAndBottomMargin.put(SETTING_ONE_STYLE_KEY, new TopAndBottom(2, 11));
        customTopAndBottomMargin.put(SETTING_TWO_STYLE_KEY, new TopAndBottom(4, 14));
        customTopAndBottomMargin.put(SETTING_THREE_STYLE_KEY, new TopAndBottom(6, 17));
        customTopAndBottomMargin.put(SETTING_FOUR_STYLE_KEY, new TopAndBottom(8, 20));
        customTopAndBottomMargin.put(SETTING_FIVE_STYLE_KEY, new TopAndBottom(10, 23));
        customTopAndBottomMargin.put(SETTING_SIX_STYLE_KEY, new TopAndBottom(14, 26));
    }

    public static Map<Integer, LeftAndRight> customLeftAndRightMargin = new HashMap<>();
    public static String CUSTOM_LEFTANDRIGHT_MARGIN_KEY = "customLeftAndRightMargin";
    public static final int DEFAULT_CUSTOM_LEFTANDRIGHT_MARGIN = SETTING_FOUR_STYLE_KEY;
    public static class LeftAndRight {
        public int left;
        public int right;

        public LeftAndRight(int left, int right) {
            this.left = left;
            this.right = right;
        }
    }

    static {
        customLeftAndRightMargin.put(SETTING_ONE_STYLE_KEY, new LeftAndRight(0, 0));
        customLeftAndRightMargin.put(SETTING_TWO_STYLE_KEY, new LeftAndRight(2, 2));
        customLeftAndRightMargin.put(SETTING_THREE_STYLE_KEY, new LeftAndRight(4, 4));
        customLeftAndRightMargin.put(SETTING_FOUR_STYLE_KEY, new LeftAndRight(6, 6));
        customLeftAndRightMargin.put(SETTING_FIVE_STYLE_KEY, new LeftAndRight(8, 8));
        customLeftAndRightMargin.put(SETTING_SIX_STYLE_KEY, new LeftAndRight(10, 10));
    }

    public static void saveCustomLeftAndRightMargin(final int customLeftAndRightMargin){
        JDPreferenceManager.setIntValue(ReaderConfig.CUSTOM_LEFTANDRIGHT_MARGIN_KEY, customLeftAndRightMargin);
    }

    public static int getCustomLeftAndRightMargin(){
        return JDPreferenceManager.getIntValue(ReaderConfig.CUSTOM_LEFTANDRIGHT_MARGIN_KEY, DEFAULT_CUSTOM_LEFTANDRIGHT_MARGIN);
    }

    public static void saveCustomTopAndBottomMargin(final int customTopAndBottomMargin){
        JDPreferenceManager.setIntValue(ReaderConfig.CUSTOM_TOPANDBOTTOM_MARGIN_KEY, customTopAndBottomMargin);
    }

    public static int getCustomTopAndBottomMargin(){
        return JDPreferenceManager.getIntValue(ReaderConfig.CUSTOM_TOPANDBOTTOM_MARGIN_KEY, DEFAULT_CUSTOM_TOPANDBOTTOM_MARGIN);
    }

    public static void saveCustomParagraphSpacing(final int customParagraphSpacing){
        JDPreferenceManager.setIntValue(ReaderConfig.CUSTOM_PARAGRAPH_SPACING_KEY, customParagraphSpacing);
    }

    public static int getCustomParagraphSpacing(){
        return JDPreferenceManager.getIntValue(ReaderConfig.CUSTOM_PARAGRAPH_SPACING_KEY, DEFAULT_CUSTOM_PARAGRAPH_SPACING);
    }

    public static void saveCustomLineSpacing(final int customLineSpacing){
        JDPreferenceManager.setIntValue(ReaderConfig.CUSTOM_LINE_SPACING_KEY, customLineSpacing);
    }

    public static int getCustomLineSpacing(){
        return JDPreferenceManager.getIntValue(ReaderConfig.CUSTOM_LINE_SPACING_KEY, DEFAULT_CUSTOM_LINE_SPACING);
    }

    public static void setReaderChineseConvertType(ReaderChineseConvertType type) {
        if (ReaderChineseConvertType.TRADITIONAL_TO_SIMPLIFIED == type ||
                ReaderChineseConvertType.NONE == type) {
            JDPreferenceManager.setIntValue(ReaderConfig.READER_CHINESE_CONVERT_TYPE_KEY, ReaderConfig.CHINESE);
        } else {
            JDPreferenceManager.setIntValue(ReaderConfig.READER_CHINESE_CONVERT_TYPE_KEY, ReaderConfig.SIMPLIFIED);
        }
    }

    public static ReaderChineseConvertType getReaderChineseConvertType() {
        ReaderChineseConvertType chineseConvertType;
        int type = JDPreferenceManager.getIntValue(ReaderConfig.READER_CHINESE_CONVERT_TYPE_KEY, ReaderConfig.CHINESE);
        if (type == CHINESE) {
            chineseConvertType = ReaderChineseConvertType.TRADITIONAL_TO_SIMPLIFIED;
        } else {
            chineseConvertType = ReaderChineseConvertType.SIMPLIFIED_TO_TRADITIONAL;
        }
        return chineseConvertType;
    }

    public static void setEmboldenLevel(int emboldenLevel) {
        JDPreferenceManager.setIntValue(ReaderConfig.READER_EMBOLDENLEVEL_KEY, emboldenLevel);
    }

    public static void saveUserSetting(final ReaderTextStyle style, final SettingInfo settingInfo) {
        JDPreferenceManager.setStringValue(ReaderConfig.READER_FONTFACE_KEY, style.getFontFace());

        JDPreferenceManager.setIntValue(ReaderConfig.SETTING_TYPE_KEY, settingInfo.settingType);

        JDPreferenceManager.setIntValue(ReaderConfig.SETTING_STYLE_KEY, settingInfo.settingStyle);

        saveCustomLeftAndRightMargin(settingInfo.customLeftAndRightMargin);
        saveCustomTopAndBottomMargin(settingInfo.customTopAndBottomMargin);
        saveCustomParagraphSpacing(settingInfo.customParagraphSpacing);
        saveCustomLineSpacing(settingInfo.customLineSpacing);
    }

    public static int getSettingType(){
        return JDPreferenceManager.getIntValue(ReaderConfig.SETTING_TYPE_KEY,DEFAULT_SETTING_TYPE);
    }

    public static int getSettingStyle(){
        return JDPreferenceManager.getIntValue(ReaderConfig.SETTING_STYLE_KEY,DEFAULT_PRESET_STYLE);
    }
}
