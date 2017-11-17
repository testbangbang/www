package com.neverland.engbook.allstyles;

import com.neverland.engbook.util.AlParProperty;
import com.neverland.engbook.util.AlStyles;

public class AlOneCSS {
    // set 0
    public static final long		FONTSIZE_MASK_ALL =			0x1ffff00000000000L;
    public static final long		FONTSIZE_MASK_FLAG =		0x1000000000000000L;
    public static final long		FONTSIZE_MASK_SIZE =		0x0ffff00000000000L;
    public static final long		FONTSIZE_ABSOLUTE =			0x1000000000000000L;
    public static final long		FONTSIZE_VALUE_SHIFT =		44L;

    public static final long		FONTSIZE_MINUS7 =			0x00ae600000000000L;
    public static final long		FONTSIZE_MINUS6 =			0x00d1400000000000L;
    public static final long		FONTSIZE_MINUS5 =			0x00fb200000000000L;
    public static final long		FONTSIZE_MINUS4 =			0x012d600000000000L;
    public static final long		FONTSIZE_MINUS3 =			0x0169b00000000000L;
    public static final long		FONTSIZE_MINUS2 =			0x01b2000000000000L;
    public static final long		FONTSIZE_MINUS1 =			0x0208d00000000000L;
    public static final long		FONTSIZE_NORMAL =			0x0271000000000000L;
    public static final long		FONTSIZE_PLUS1 =			0x02ee000000000000L;
    public static final long		FONTSIZE_PLUS2 =			0x0384000000000000L;
    public static final long		FONTSIZE_PLUS3 =			0x0438000000000000L;
    public static final long		FONTSIZE_PLUS4 =			0x0510000000000000L;
    public static final long		FONTSIZE_PLUS5 =			0x0613300000000000L;
    public static final long		FONTSIZE_PLUS6 =			0x074a300000000000L;
    public static final long		FONTSIZE_PLUS7 =			0x08bf700000000000L;
    public static final long		FONTSIZE_PLUS8 =			0x0a7f600000000000L;

    public static final long		BOLD_MASK =					AlStyles.STYLE_BOLD;
    public static final long		ITALIC_MASK =				AlStyles.STYLE_ITALIC;
    public static final long		SUP_MASK =					AlStyles.STYLE_SUP;
    public static final long		SUB_MASK =					AlStyles.STYLE_SUB;
    public static final long		STRIKE_MASK =				AlStyles.STYLE_STRIKE;
    public static final long		UNDER_MASK =				AlStyles.STYLE_UNDER;
    public static final long		CODE_MASK =					AlStyles.STYLE_CODE;
    public static final long		HIDDEN_MASK =				AlStyles.STYLE_HIDDEN;
    public static final long		RAZR_MASK =					AlStyles.STYLE_RAZR;

    public static final long		SHADOW_MASK =				AlStyles.SL_SHADOW;
    public static final long		NOHYPH_MASK =				AlStyles.SL_NOHYPH;
    public static final long		FONTTYPE_MASK =				AlStyles.SL_FONT_MASK;
    public static final long		FONTTYPE_TEXT =				AlStyles.SL_FONT_TEXT;
    public static final long		FONTTYPE_CODE =				AlStyles.SL_FONT_CODE;
    public static final long		FONTTYPE_NOTE =				AlStyles.SL_FONT_NOTE;
    public static final long		FONTTYPE_FLET =				AlStyles.SL_FONT_FLET;

    public static final long		PRESERVE_SPACE =			AlStyles.SL_PRESERVE_SPACE;

    // set 1
    public static final long		JUST_MASK =                 AlParProperty.SL2_JUST_MASK;
    public static final long		JUST_NONE =					AlParProperty.SL2_JUST_NONE;
    public static final long		JUST_LEFT =					AlParProperty.SL2_JUST_LEFT;
    public static final long		JUST_RIGHT =				AlParProperty.SL2_JUST_RIGHT;
    public static final long		JUST_CENTER =				AlParProperty.SL2_JUST_CENTER;

    public static final long 		MARG_MAX_VALUE =			AlParProperty.SL2_MARG_MAX_VALUE;

    public static final long		MARGLEFT_MASK =				AlParProperty.SL2_MARGL_MASK;
    public static final long		MARGLEFT_SHIFT =			AlParProperty.SL2_MARGL_SHIFT;

    public static final long		MARGRIGHT_MASK =			AlParProperty.SL2_MARGR_MASK;
    public static final long		MARGRIGHT_SHIFT =			AlParProperty.SL2_MARGR_SHIFT;

    public static final long		MARGTOP_MASK =				AlParProperty.SL2_MARGT_MASK;
    public static final long		MARGTOP_SHIFT =				AlParProperty.SL2_MARGT_SHIFT;

    public static final long		MARGBOTTOM_MASK =			AlParProperty.SL2_MARGB_MASK;
    public static final long		MARGBOTTOM_SHIFT =			AlParProperty.SL2_MARGB_SHIFT;

    public static final long		INDENT_MASK =				AlParProperty.SL2_INDENT_MASK;
    public static final long		INDENT_SHIFT =				AlParProperty.SL2_INDENT_SHIFT;
    public static final long 		INDENT_MAX_VALUE =			AlParProperty.SL2_INDENT_MAX_VALUE;

    public static final long		PAGEBREAKBEFORE_MASK =		AlParProperty.SL2_BREAK_BEFORE;
    public static final long		PAGEBREAKAFTER_MASK =		AlParProperty.SL2_BREAK_AFTER;

    public static final long		JUSTPOEM_MASK =				AlParProperty.SL2_JUSTIFY_POEM;
    //public static final long		REDLINE_MASK =				AlParProperty.SL2_REDLINE;	
    public static final long		EMPTYLINEBEFORE_MASK =		AlParProperty.SL2_EMPTY_BEFORE;
    public static final long		EMPTYLINEAFTER_MASK =		AlParProperty.SL2_EMPTY_AFTER;
    //

    public static final long		ENABLE_MASK1_ALL = 0xffffffffffffffffL;
    public static final long		ENABLE_MASK1_RESTRICTION = ENABLE_MASK1_ALL -
                                    JUST_MASK -
                                    MARGLEFT_MASK - MARGRIGHT_MASK - MARGTOP_MASK - MARGBOTTOM_MASK -
                                    INDENT_MASK;

    public int				    tag = 0x00;
    public long 				clsX = 0x00;
    public AlOneCSSPair			val = new AlOneCSSPair();
    public String               tag_str = null;
    public String               cls_str = null;

    public static long	calcHash(String cls) {
        long res = 0x00;
        for (int i = 0; i < cls.length(); i++) {
            res = (res * 31) + cls.charAt(i);
        }
        return res;
    }

    public String outString() {
        StringBuilder s = new StringBuilder();
        if (tag != 0) {
            s.append(tag_str);
        }

        if (cls_str != null && !cls_str.isEmpty()) {
            s.append('.');
            s.append(cls_str);
        }
        s.append(String.format("=%016x.%016x %016x.%016x", val.m0, val.v0, val.m1, val.v1));
        /*s.append('=').append(Long.toHexString(val.m0)).append('.').append(Long.toHexString(val.v0)).
                append(' ').append(Long.toHexString(val.m1)).append('.').append(Long.toHexString(val.v1));*/

        return s.toString();
    }
}
