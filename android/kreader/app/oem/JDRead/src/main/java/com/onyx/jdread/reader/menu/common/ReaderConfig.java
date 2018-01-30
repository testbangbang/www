package com.onyx.jdread.reader.menu.common;

import com.onyx.android.sdk.data.ReaderTextStyle;

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
        public static final String BOLD_FACE_TYPEFACE = TYPEFACE_ROOT_PATH + "FZHT_GB18030.TTF";
        public static final String ARIAL_TYPEFACE = TYPEFACE_ROOT_PATH + "PTF55.otf";
        public static final String ITALICS_TYPEFACE = TYPEFACE_ROOT_PATH + "GEInspRg.TTF";
        public static final String ROUND_BODY_TYPEFACE = TYPEFACE_ROOT_PATH + "GEInspBd.ttf";
        public static final String DEFAULT_TYPEFACE = BOLD_FACE_TYPEFACE;
    }

    public static class TypefaceColorDepth{
        public static final int LEVEL_ONE = 25;
        public static final int LEVEL_TWO = 50;
        public static final int LEVEL_THREE = 75;
        public static final int LEVEL_FOUR = 100;
        public static final int LEVEL_FIVE = 125;
        public static final int LEVEL_SIX = 150;
        public static final int DEFAULT_COLOR_DEPTH = LEVEL_ONE;
    }

    public static class PageLineSpacing{
        public static final int MIN_LINE_SPACING = 0;
        public static final int MAX_LINE_SPACING = 60;
        public static final int DEFAULT_LINE_SPACING = 25 + ReaderTextStyle.SMALL_LINE_SPACING.getPercent();
    }

    public static class PageParagraphSpacing{
        public static final int MIN_PARAGRAPH_SPACING = 0;
        public static final int MAX_PARAGRAPH_SPACING = 200;
        public static final int DEFAULT_PARAGRAPH_SPACING = 30;
    }

    public static class PageLeftAndRightSpacing{
        public static final int MIN_LEFT_AND_RIGHT_SPACING = 0;
        public static final int MAX_LEFT_AND_RIGHT_SPACING = 20;
        public static final int DEFAULT_LEFT_AND_RIGHT_SPACING = 8;
    }

    public static class PageUpAndDownSpacing{
        public static final int MIN_UP_AND_DOWN_SPACING = 0;
        public static final int MAX_UP_AND_DOWN_SPACING = 20;
        public static final int DEFAULT_UP_AND_DOWN_SPACING = 8;
    }
}
