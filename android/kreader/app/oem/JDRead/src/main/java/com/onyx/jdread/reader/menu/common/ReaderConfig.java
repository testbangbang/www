package com.onyx.jdread.reader.menu.common;

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
}
