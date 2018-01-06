package com.onyx.jdread.reader.menu.common;

import retrofit2.http.PUT;

/**
 * Created by huxiaomao on 2018/1/6.
 */

public class ReaderConfig {
    public static final String TYPEFACE_ROOT_PATH = "/sdcard/fonts/";

    public static class FontSize {
        public static final int ONE_FONT_SIZE = 12;
        public static final int TWO_FONT_SIZE = 18;
        public static final int THREE_FONT_SIZE = 24;
        public static final int FOUR_FONT_SIZE = 30;
        public static final int FIVE_FONT_SIZE = 36;
        public static final int SIX_FONT_SIZE = 42;
    }

    public static class Typeface {
        public static final String BOLD_FACE_TYPEFACE = TYPEFACE_ROOT_PATH + "GEInspBI.TTF";
        public static final String ARIAL_TYPEFACE = TYPEFACE_ROOT_PATH + "PTF55.otf";
        public static final String ITALICS_TYPEFACE = TYPEFACE_ROOT_PATH + "GEInspRg.TTF";
        public static final String ROUND_BODY_TYPEFACE = TYPEFACE_ROOT_PATH + "xingguangyijiu.ttf";
    }

    public static class TypefaceColorDepth{
        public static final int ONE_LEVEL = 25;
        public static final int TWO_LEVEL = 50;
        public static final int THREE_LEVEL = 75;
        public static final int FOUR_LEVEL = 100;
        public static final int FIVE_LEVEL = 125;
        public static final int SIX_LEVEL = 150;
    }

    public static class PageLineSpacing{
        public static final int MIN_LINE_SPACING = 0;
        public static final int MAX_LINE_SPACING = 30;
    }

    public static class PageSegmentSpacing{
        public static final int MIN_SEGMENT_SPACING = 0;
        public static final int MAX_SEGMENT_SPACING = 30;
    }

    public static class PageLeftAndRightSpacing{
        public static final int MIN_LEFT_AND_RIGHT_SPACING = 0;
        public static final int MAX_LEFT_AND_RIGHT_SPACING = 30;
    }

    public static class PageUpAndDownSpacing{
        public static final int MIN_UP_AND_DOWN_SPACING = 0;
        public static final int MAX_UP_AND_DOWN_SPACING = 30;
    }
}
