package com.neverland.engbook.forpublic;

import com.neverland.engbook.util.AlStyles;

public class AlBookStyles {
    public static final long FONT_TEXT = AlStyles.SL_FONT_TEXT;
    public static final long FONT_TITLE = AlStyles.SL_FONT_TITLE;
    public static final long FONT_CODE = AlStyles.SL_FONT_CODE;

    public static final long FONT_ITALIC = AlStyles.SL_ITALIC;
    public static final long FONT_BOLD = AlStyles.SL_BOLD;

    public static final long FONT_SIZE_TEXT = AlStyles.SL_SIZE_0;
    public static final long FONT_SIZE_TEXT_MINUS7 = AlStyles.SL_SIZE_M7;
    public static final long FONT_SIZE_TEXT_MINUS6 = AlStyles.SL_SIZE_M6;
    public static final long FONT_SIZE_TEXT_MINUS5 = AlStyles.SL_SIZE_M5;
    public static final long FONT_SIZE_TEXT_MINUS4 = AlStyles.SL_SIZE_M4;
    public static final long FONT_SIZE_TEXT_MINUS3 = AlStyles.SL_SIZE_M3;
    public static final long FONT_SIZE_TEXT_MINUS2 = AlStyles.SL_SIZE_M2;
    public static final long FONT_SIZE_TEXT_MINUS1 = AlStyles.SL_SIZE_M1;
    public static final long FONT_SIZE_TEXT_PLUS1 = AlStyles.SL_SIZE_P1;
    public static final long FONT_SIZE_TEXT_PLUS2 = AlStyles.SL_SIZE_P2;
    public static final long FONT_SIZE_TEXT_PLUS3 = AlStyles.SL_SIZE_P3;
    public static final long FONT_SIZE_TEXT_PLUS4 = AlStyles.SL_SIZE_P4;
    public static final long FONT_SIZE_TEXT_PLUS5 = AlStyles.SL_SIZE_P5;
    public static final long FONT_SIZE_TEXT_PLUS6 = AlStyles.SL_SIZE_P6;
    public static final long FONT_SIZE_TEXT_PLUS7 = AlStyles.SL_SIZE_P7;
    public static final long FONT_SIZE_TEXT_PLUS8 = AlStyles.SL_SIZE_P8;

    public static final long SUPPORT_HYPHEN = AlStyles.SL_HYPH;

    public static final long JUST_NONE = AlStyles.SL_JUST_NONE;
    public static final long JUST_LEFT = AlStyles.SL_JUST_LEFT;
    public static final long JUST_RIGHT = AlStyles.SL_JUST_RIGHT;
    public static final long JUST_CENTER = AlStyles.SL_JUST_CENTER;
    public static final long JUST_4POEM = AlStyles.SL_JUSTIFY_POEM;

    public static final long REDLINE = AlStyles.SL_REDLINE;

    public static final long MARGINS_LEFT_NONE = AlStyles.SL_MARGL0;
    public static final long MARGINS_LEFT_1 = AlStyles.SL_MARGL1;
    public static final long MARGINS_LEFT_2 = AlStyles.SL_MARGL2;
    public static final long MARGINS_LEFT_3 = AlStyles.SL_MARGL3;
    public static final long MARGINS_RIGHT_NONE = AlStyles.SL_MARGR0;
    public static final long MARGINS_RIGHT_1 = AlStyles.SL_MARGR1;
    public static final long MARGINS_RIGHT_2 = AlStyles.SL_MARGR2;
    public static final long MARGINS_RIGHT_3 = AlStyles.SL_MARGR3;

    public static final long COLOR_TEXT = AlStyles.SL_COLOR_TEXT;
    public static final long COLOR_TITLE = AlStyles.SL_COLOR_TITLE;

    public static final long SHADOW = AlStyles.SL_SHADOW;

    /////////////////////////////////////////

    public long  styleTitle = FONT_TEXT | FONT_SIZE_TEXT_PLUS2 | COLOR_TEXT | SHADOW | JUST_CENTER | MARGINS_LEFT_1 | MARGINS_RIGHT_1;
    public long  styleSubTitle = FONT_TEXT | FONT_SIZE_TEXT_PLUS1 | COLOR_TEXT | JUST_CENTER | MARGINS_LEFT_1 | MARGINS_RIGHT_1;
    public long  styleAnnotation = FONT_TEXT | FONT_SIZE_TEXT_MINUS1 | COLOR_TEXT | FONT_ITALIC | SUPPORT_HYPHEN | JUST_RIGHT | MARGINS_LEFT_3;
    public long  styleEpigraph = FONT_TEXT | FONT_SIZE_TEXT_MINUS2 | COLOR_TEXT | FONT_ITALIC | SUPPORT_HYPHEN | JUST_LEFT | MARGINS_LEFT_2;
    public long  styleAuthor = FONT_TEXT | FONT_SIZE_TEXT_MINUS3 | COLOR_TEXT | JUST_RIGHT;
    public long  styleCite = FONT_TEXT | FONT_SIZE_TEXT | COLOR_TEXT | SUPPORT_HYPHEN;
    public long  stylePRE = FONT_TEXT | FONT_SIZE_TEXT | COLOR_TEXT;
    public long  stylePoem = FONT_TEXT | FONT_SIZE_TEXT | COLOR_TEXT | JUST_4POEM;
    public long  styleFootnote = FONT_TEXT | FONT_SIZE_TEXT_MINUS3 | COLOR_TEXT | SUPPORT_HYPHEN;
    public long  styleCode = FONT_TEXT | FONT_SIZE_TEXT | COLOR_TEXT;


}
