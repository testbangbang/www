package com.neverland.engbook.allstyles;

import com.neverland.engbook.unicode.AlUnicode;

public class AlOneCSSNumberValue {
    public static final int  CSS_NUM_UNKNOWN = 0;
    public static final int  CSS_NUM_PX = 1;
    public static final int  CSS_NUM_PERCENT = 2;
    public static final int  CSS_NUM_EM = 3;
    public static final int  CSS_NUM_SIMPLE = 4;
    public static final int  CSS_NUM_REM = 5;

    public int 		tp;
    public double	dval;
    public int		ival;

    protected StringBuilder tmpval = new StringBuilder();

    AlOneCSSNumberValue() {
        tp = CSS_NUM_UNKNOWN;
    }


    static final int scan(AlOneCSSNumberValue a, StringBuilder vv) {
        int i = 0, j, len = vv.length();
        boolean pointPresent = false;

        a.tmpval.setLength(0);
        a.tp = CSS_NUM_UNKNOWN;

        scanning: for (i = 0; i < len; i++) {
            switch (vv.charAt(i)) {
                case '0':case '1':case '2':case '3':case '4':case '5':case '6':case '7':case '8':case '9':
                    a.tmpval.append(vv.charAt(i));
                    break;
                case '-':case '+':
                    if (a.tmpval.length() > 0)
                        return 0;
                    a.tmpval.append(vv.charAt(i));
                    break;
                case '.':
                    if (pointPresent)
                       return 0;
                    a.tmpval.append(vv.charAt(i));
                    pointPresent = true;
                    break;
                default:
                    if (a.tmpval.length() == 0)
                        break;
                    break scanning;
            }
        }

        if (a.tmpval.length() == 0)
            return 0;

        try {
            a.dval = Float.parseFloat(a.tmpval.toString());
        } catch (Exception e) {
            e.printStackTrace();
            a.dval = 0;
        }
        a.ival = (int)(a.dval + 0.5);

        while (true) {
            if (i >= len) {
                a.tp = CSS_NUM_SIMPLE;
                return vv.length();
            } else
            if (AlUnicode.isDecDigit(vv.charAt(i)) || vv.charAt(i) == '.') {
                a.tp = CSS_NUM_SIMPLE;
                break;
            } else
            if (AlUnicode.isSpaceSeparator(vv.charAt(i)) || vv.charAt(i) <= 0x20) {
                i++;
            } else
                break;
        }

        if (a.tp == CSS_NUM_UNKNOWN) {
            if (vv.charAt(i) == 'p' && i + 1 < len) {
                if (vv.charAt(i + 1) == 't') {
                    a.tp = CSS_NUM_PX;
                    a.dval *= 4.0 / 3;
                    a.ival = (int)(a.dval + 0.5);
                    i++;
                    i++;
                } else
                if (vv.charAt(i + 1) == 'x') {
                    a.tp = CSS_NUM_PX;
                    i++;
                    i++;
                } else {

                }
            } else
            if (vv.charAt(i) == 'e' && i + 1 < len) {
                if (vv.charAt(i + 1) == 'm') {
                    a.tp = CSS_NUM_EM;
                    i++;
                    i++;
                } else {

                }
            } else
            if (vv.charAt(i) == '%') {
                a.tp = CSS_NUM_PERCENT;
                i++;
            } else
            if (vv.charAt(i) == 'r' && i + 2 < len) {
                if (vv.charAt(i + 1) == 'e' && vv.charAt(i + 2) == 'm') {
                    a.tp = CSS_NUM_REM;
                    i++;
                    i++;
                    i++;
                } else {

                }
            }
        }

        while (i < len && vv.charAt(i) <= 0x20)
            i++;

        return i;
    }
}
