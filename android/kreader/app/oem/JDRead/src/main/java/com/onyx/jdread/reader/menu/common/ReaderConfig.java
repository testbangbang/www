package com.onyx.jdread.reader.menu.common;

/**
 * Created by huxiaomao on 2018/1/6.
 */

public class ReaderConfig {
    public static final String TYPEFACE_ROOT_PATH = "/sdcard/fonts/";
    public static final int HIT_TEST_TEXT_STEP = 30;

    public static class FontSize {
        public static final int FONT_SIZE_X_SMALL = 12;
        public static final int FONT_SIZE_SMALL = 18;
        public static final int FONT_SIZE_MEDIUM = 24;
        public static final int FONT_SIZE_LARGE = 30;
        public static final int FONT_SIZE_X_LARGE = 36;
        public static final int FONT_SIZE_XX_LARGE = 42;
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
}
