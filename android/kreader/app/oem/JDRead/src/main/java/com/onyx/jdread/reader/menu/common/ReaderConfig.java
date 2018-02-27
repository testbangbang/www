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
    public static final String TYPEFACE_ROOT_PATH = "/system/fonts/";
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
        customLineSpacing.put(SETTING_TWO_STYLE_KEY, 110);
        customLineSpacing.put(SETTING_THREE_STYLE_KEY, 120);
        customLineSpacing.put(SETTING_FOUR_STYLE_KEY, 130);
        customLineSpacing.put(SETTING_FIVE_STYLE_KEY, 140);
        customLineSpacing.put(SETTING_SIX_STYLE_KEY, 150);
    }

    public static Map<Integer, Integer> customParagraphSpacing = new HashMap<>();
    public static String CUSTOM_PARAGRAPH_SPACING_KEY = "customParagraphSpacing";
    public static final int DEFAULT_CUSTOM_PARAGRAPH_SPACING = SETTING_TWO_STYLE_KEY;
    static {
        customParagraphSpacing.put(SETTING_ONE_STYLE_KEY, 0);
        customParagraphSpacing.put(SETTING_TWO_STYLE_KEY, 40);
        customParagraphSpacing.put(SETTING_THREE_STYLE_KEY, 80);
        customParagraphSpacing.put(SETTING_FOUR_STYLE_KEY, 120);
        customParagraphSpacing.put(SETTING_FIVE_STYLE_KEY, 160);
        customParagraphSpacing.put(SETTING_SIX_STYLE_KEY, 200);
    }

    public static Map<Integer, TopAndBottom> customOneTopAndBottomMargin = new HashMap<>();
    public static Map<Integer, TopAndBottom> customTwoTopAndBottomMargin = new HashMap<>();
    public static Map<Integer, TopAndBottom> customThreeTopAndBottomMargin = new HashMap<>();
    public static Map<Integer, TopAndBottom> customFourTopAndBottomMargin = new HashMap<>();
    public static Map<Integer, TopAndBottom> customFiveTopAndBottomMargin = new HashMap<>();
    public static Map<Integer, TopAndBottom> customSixTopAndBottomMargin = new HashMap<>();
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

    public static TopAndBottom getCustomTopAndBottomMargin(int settingStyle,int key){
        switch (settingStyle){
            case SETTING_ONE_STYLE_KEY:
                return customOneTopAndBottomMargin.get(key);
            case SETTING_TWO_STYLE_KEY:
                return customTwoTopAndBottomMargin.get(key);
            case SETTING_THREE_STYLE_KEY:
                return customThreeTopAndBottomMargin.get(key);
            case SETTING_FOUR_STYLE_KEY:
                return customFourTopAndBottomMargin.get(key);
            case SETTING_FIVE_STYLE_KEY:
                return customFiveTopAndBottomMargin.get(key);
            case SETTING_SIX_STYLE_KEY:
                return customSixTopAndBottomMargin.get(key);
        }
        return null;
    }

    static {
        customOneTopAndBottomMargin.put(SETTING_ONE_STYLE_KEY, new TopAndBottom(0, 11));
        customOneTopAndBottomMargin.put(SETTING_TWO_STYLE_KEY, new TopAndBottom(4, 13));
        customOneTopAndBottomMargin.put(SETTING_THREE_STYLE_KEY, new TopAndBottom(9, 17));
        customOneTopAndBottomMargin.put(SETTING_FOUR_STYLE_KEY, new TopAndBottom(13, 20));
        customOneTopAndBottomMargin.put(SETTING_FIVE_STYLE_KEY, new TopAndBottom(18, 23));
        customOneTopAndBottomMargin.put(SETTING_SIX_STYLE_KEY, new TopAndBottom(23, 26));
    }

    static {
        customTwoTopAndBottomMargin.put(SETTING_ONE_STYLE_KEY, new TopAndBottom(0, 11));
        customTwoTopAndBottomMargin.put(SETTING_TWO_STYLE_KEY, new TopAndBottom(6, 13));
        customTwoTopAndBottomMargin.put(SETTING_THREE_STYLE_KEY, new TopAndBottom(12, 17));
        customTwoTopAndBottomMargin.put(SETTING_FOUR_STYLE_KEY, new TopAndBottom(19, 20));
        customTwoTopAndBottomMargin.put(SETTING_FIVE_STYLE_KEY, new TopAndBottom(23, 23));
        customTwoTopAndBottomMargin.put(SETTING_SIX_STYLE_KEY, new TopAndBottom(28, 26));
    }

    static {
        customThreeTopAndBottomMargin.put(SETTING_ONE_STYLE_KEY, new TopAndBottom(0, 11));
        customThreeTopAndBottomMargin.put(SETTING_TWO_STYLE_KEY, new TopAndBottom(5, 13));
        customThreeTopAndBottomMargin.put(SETTING_THREE_STYLE_KEY, new TopAndBottom(11, 17));
        customThreeTopAndBottomMargin.put(SETTING_FOUR_STYLE_KEY, new TopAndBottom(16, 20));
        customThreeTopAndBottomMargin.put(SETTING_FIVE_STYLE_KEY, new TopAndBottom(22, 23));
        customThreeTopAndBottomMargin.put(SETTING_SIX_STYLE_KEY, new TopAndBottom(30, 26));
    }

    static {
        customFourTopAndBottomMargin.put(SETTING_ONE_STYLE_KEY, new TopAndBottom(0, 11));
        customFourTopAndBottomMargin.put(SETTING_TWO_STYLE_KEY, new TopAndBottom(3, 13));
        customFourTopAndBottomMargin.put(SETTING_THREE_STYLE_KEY, new TopAndBottom(11, 17));
        customFourTopAndBottomMargin.put(SETTING_FOUR_STYLE_KEY, new TopAndBottom(17, 20));
        customFourTopAndBottomMargin.put(SETTING_FIVE_STYLE_KEY, new TopAndBottom(22, 23));
        customFourTopAndBottomMargin.put(SETTING_SIX_STYLE_KEY, new TopAndBottom(30, 26));
    }

    static {
        customFiveTopAndBottomMargin.put(SETTING_ONE_STYLE_KEY, new TopAndBottom(0, 11));
        customFiveTopAndBottomMargin.put(SETTING_TWO_STYLE_KEY, new TopAndBottom(8, 13));
        customFiveTopAndBottomMargin.put(SETTING_THREE_STYLE_KEY, new TopAndBottom(12, 17));
        customFiveTopAndBottomMargin.put(SETTING_FOUR_STYLE_KEY, new TopAndBottom(18, 20));
        customFiveTopAndBottomMargin.put(SETTING_FIVE_STYLE_KEY, new TopAndBottom(25, 23));
        customFiveTopAndBottomMargin.put(SETTING_SIX_STYLE_KEY, new TopAndBottom(30, 26));
    }

    static {
        customSixTopAndBottomMargin.put(SETTING_ONE_STYLE_KEY, new TopAndBottom(0, 11));
        customSixTopAndBottomMargin.put(SETTING_TWO_STYLE_KEY, new TopAndBottom(8, 13));
        customSixTopAndBottomMargin.put(SETTING_THREE_STYLE_KEY, new TopAndBottom(14, 17));
        customSixTopAndBottomMargin.put(SETTING_FOUR_STYLE_KEY, new TopAndBottom(15, 20));
        customSixTopAndBottomMargin.put(SETTING_FIVE_STYLE_KEY, new TopAndBottom(25, 23));
        customSixTopAndBottomMargin.put(SETTING_SIX_STYLE_KEY, new TopAndBottom(30, 26));
    }

    public static Map<Integer, LeftAndRight> customOneLeftAndRightMargin = new HashMap<>();
    public static Map<Integer, LeftAndRight> customTwoLeftAndRightMargin = new HashMap<>();
    public static Map<Integer, LeftAndRight> customThreeLeftAndRightMargin = new HashMap<>();
    public static Map<Integer, LeftAndRight> customFourLeftAndRightMargin = new HashMap<>();
    public static Map<Integer, LeftAndRight> customFiveLeftAndRightMargin = new HashMap<>();
    public static Map<Integer, LeftAndRight> customSixLeftAndRightMargin = new HashMap<>();
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

    public static ReaderConfig.LeftAndRight getCustomLeftAndRightMargin(int settingStyle,int key){
        switch (settingStyle){
            case SETTING_ONE_STYLE_KEY:
                return customOneLeftAndRightMargin.get(key);
            case SETTING_TWO_STYLE_KEY:
                return customTwoLeftAndRightMargin.get(key);
            case SETTING_THREE_STYLE_KEY:
                return customThreeLeftAndRightMargin.get(key);
            case SETTING_FOUR_STYLE_KEY:
                return customFourLeftAndRightMargin.get(key);
            case SETTING_FIVE_STYLE_KEY:
                return customFiveLeftAndRightMargin.get(key);
            case SETTING_SIX_STYLE_KEY:
                return customSixLeftAndRightMargin.get(key);
        }
        return null;
    }

    static {
        customOneLeftAndRightMargin.put(SETTING_ONE_STYLE_KEY, new LeftAndRight(0, 0));
        customOneLeftAndRightMargin.put(SETTING_TWO_STYLE_KEY, new LeftAndRight(2, 2));
        customOneLeftAndRightMargin.put(SETTING_THREE_STYLE_KEY, new LeftAndRight(4, 4));
        customOneLeftAndRightMargin.put(SETTING_FOUR_STYLE_KEY, new LeftAndRight(6, 6));
        customOneLeftAndRightMargin.put(SETTING_FIVE_STYLE_KEY, new LeftAndRight(8, 8));
        customOneLeftAndRightMargin.put(SETTING_SIX_STYLE_KEY, new LeftAndRight(10, 10));
    }

    static {
        customTwoLeftAndRightMargin.put(SETTING_ONE_STYLE_KEY, new LeftAndRight(0, 0));
        customTwoLeftAndRightMargin.put(SETTING_TWO_STYLE_KEY, new LeftAndRight(2, 2));
        customTwoLeftAndRightMargin.put(SETTING_THREE_STYLE_KEY, new LeftAndRight(4, 4));
        customTwoLeftAndRightMargin.put(SETTING_FOUR_STYLE_KEY, new LeftAndRight(6, 6));
        customTwoLeftAndRightMargin.put(SETTING_FIVE_STYLE_KEY, new LeftAndRight(8, 8));
        customTwoLeftAndRightMargin.put(SETTING_SIX_STYLE_KEY, new LeftAndRight(10, 10));
    }

    static {
        customThreeLeftAndRightMargin.put(SETTING_ONE_STYLE_KEY, new LeftAndRight(0, 0));
        customThreeLeftAndRightMargin.put(SETTING_TWO_STYLE_KEY, new LeftAndRight(2, 2));
        customThreeLeftAndRightMargin.put(SETTING_THREE_STYLE_KEY, new LeftAndRight(4, 4));
        customThreeLeftAndRightMargin.put(SETTING_FOUR_STYLE_KEY, new LeftAndRight(6, 6));
        customThreeLeftAndRightMargin.put(SETTING_FIVE_STYLE_KEY, new LeftAndRight(10, 8));
        customThreeLeftAndRightMargin.put(SETTING_SIX_STYLE_KEY, new LeftAndRight(12, 10));
    }

    static {
        customFourLeftAndRightMargin.put(SETTING_ONE_STYLE_KEY, new LeftAndRight(0, 0));
        customFourLeftAndRightMargin.put(SETTING_TWO_STYLE_KEY, new LeftAndRight(2, 2));
        customFourLeftAndRightMargin.put(SETTING_THREE_STYLE_KEY, new LeftAndRight(4, 4));
        customFourLeftAndRightMargin.put(SETTING_FOUR_STYLE_KEY, new LeftAndRight(6, 6));
        customFourLeftAndRightMargin.put(SETTING_FIVE_STYLE_KEY, new LeftAndRight(10, 10));
        customFourLeftAndRightMargin.put(SETTING_SIX_STYLE_KEY, new LeftAndRight(12, 12));
    }

    static {
        customFiveLeftAndRightMargin.put(SETTING_ONE_STYLE_KEY, new LeftAndRight(0, 0));
        customFiveLeftAndRightMargin.put(SETTING_TWO_STYLE_KEY, new LeftAndRight(3, 3));
        customFiveLeftAndRightMargin.put(SETTING_THREE_STYLE_KEY, new LeftAndRight(5, 5));
        customFiveLeftAndRightMargin.put(SETTING_FOUR_STYLE_KEY, new LeftAndRight(8, 8));
        customFiveLeftAndRightMargin.put(SETTING_FIVE_STYLE_KEY, new LeftAndRight(11, 11));
        customFiveLeftAndRightMargin.put(SETTING_SIX_STYLE_KEY, new LeftAndRight(14, 14));
    }

    static {
        customSixLeftAndRightMargin.put(SETTING_ONE_STYLE_KEY, new LeftAndRight(0, 0));
        customSixLeftAndRightMargin.put(SETTING_TWO_STYLE_KEY, new LeftAndRight(3, 3));
        customSixLeftAndRightMargin.put(SETTING_THREE_STYLE_KEY, new LeftAndRight(6, 6));
        customSixLeftAndRightMargin.put(SETTING_FOUR_STYLE_KEY, new LeftAndRight(10, 10));
        customSixLeftAndRightMargin.put(SETTING_FIVE_STYLE_KEY, new LeftAndRight(14, 14));
        customSixLeftAndRightMargin.put(SETTING_SIX_STYLE_KEY, new LeftAndRight(18, 18));
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
