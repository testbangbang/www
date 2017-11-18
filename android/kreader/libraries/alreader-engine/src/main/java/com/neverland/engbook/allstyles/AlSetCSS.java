package com.neverland.engbook.allstyles;

import com.neverland.engbook.unicode.AlUnicode;
import com.neverland.engbook.util.AlStyleStack;
import com.neverland.engbook.util.AlStyles;
import com.neverland.engbook.util.InternalFunc;

import java.util.ArrayList;

public class AlSetCSS {
    public String				name = null;

    ArrayList<AlOneCSS>			setTAG = new ArrayList<>();
    ArrayList<AlOneCSS>			setCLASS = new ArrayList<>();
    ArrayList<AlOneCSS>			setTAGCLASS = new ArrayList<>();

    //AlCSSStyles				parent = null;

    ArrayList<Integer> usingSet = new ArrayList<>();

    AlSetCSS(AlCSSStyles p) {
        //parent = p;
    }

    public final void acceptValue(AlOneCSS css, AlOneCSSPair val) {
        if (val.m0 != 0) {
            (css).val.m0 |= val.m0;
            (css).val.v0 &= ~val.m0;
            (css).val.v0 |= val.v0;
        }
        if (val.m1 != 0) {
            (css).val.m1 |= val.m1;
            (css).val.v1 &= ~val.m1;
            (css).val.v1 |= val.v1;
        }
    }

    public final void addTAG(int tag, StringBuilder tags, AlOneCSSPair val) {
        for (int i = 0; i < setTAG.size(); i++) {
            if (tag == setTAG.get(i).tag) {
                acceptValue(setTAG.get(i), val);
                return;
            }
        }

        AlOneCSS a = new AlOneCSS();
        a.tag = tag;
        a.tag_str = tags.toString();
        a.val.v0 = val.v0;
        a.val.m0 = val.m0;
        a.val.v1 = val.v1;
        a.val.m1 = val.m1;
        setTAG.add(a);
    }

    public final void addTAG(int tag, String tags, AlOneCSSPair val) {
        for (int i = 0; i < setTAG.size(); i++) {
            if (tag == setTAG.get(i).tag) {
                acceptValue(setTAG.get(i), val);
                return;
            }
        }

        AlOneCSS a = new AlOneCSS();
        a.tag = tag;
        a.tag_str = tags;
        a.val.v0 = val.v0;
        a.val.m0 = val.m0;
        a.val.v1 = val.v1;
        a.val.m1 = val.m1;
        setTAG.add(a);
    }

    public final void addCLASS(StringBuilder cls, AlOneCSSPair val) {
        for (int i = 0; i < setCLASS.size(); i++) {
            if (setCLASS.get(i).cls_str.contentEquals(cls)) {
                acceptValue(setCLASS.get(i), val);
                return;
            }
        }

        AlOneCSS a = new AlOneCSS();
        a.cls_str = cls.toString();
        a.val.v0 = val.v0;
        a.val.m0 = val.m0;
        a.val.v1 = val.v1;
        a.val.m1 = val.m1;
        setCLASS.add(a);
    }

    public final void addCLASS(String cls, AlOneCSSPair val) {
        for (int i = 0; i < setCLASS.size(); i++) {
            if (setCLASS.get(i).cls_str.contentEquals(cls)) {
                acceptValue(setCLASS.get(i), val);
                return;
            }
        }

        AlOneCSS a = new AlOneCSS();
        a.cls_str = cls;
        a.val.v0 = val.v0;
        a.val.m0 = val.m0;
        a.val.v1 = val.v1;
        a.val.m1 = val.m1;
        a.clsX = AlOneCSS.calcHash(cls);
        setCLASS.add(a);
    }

    public final void addTAGCLASS(int tag, String tags, String cls, AlOneCSSPair val) {
        for (int i = 0; i < setTAGCLASS.size(); i++) {
            AlOneCSS o = setTAGCLASS.get(i);
            if (tag == o.tag && o.cls_str.contentEquals(cls)) {
                acceptValue(o, val);
                return;
            }
        }

        AlOneCSS a = new AlOneCSS();
        a.tag = tag;
        a.tag_str = tags;
        a.cls_str = cls;
        a.val.v0 = val.v0;
        a.val.m0 = val.m0;
        a.val.v1 = val.v1;
        a.val.m1 = val.m1;
        a.clsX = AlOneCSS.calcHash(cls);
        setTAGCLASS.add(a);
    }

    public final static int parseInsertSpaces(StringBuilder val) {
        //StringBuilder vv = new StringBuilder(val);
        int i;

        i = val.length() - 1;
        while (i > 0) {
            if (val.charAt(i) == ' ')
                val.deleteCharAt(i);
            i--;
        }

        i = val.indexOf("em");
        if (i != -1) {
            val.delete(i, val.length());
        } else {
            return 0;
        }

        return InternalFunc.str2int(val, 10);
    }

    public final static boolean parseAlign(StringBuilder val, AlStyleStack stack) {
        AlOneCSSPair p = new AlOneCSSPair();

        if ("inherit".contentEquals(val)) {

        } else
        if ("center".contentEquals(val)) {
            p.m1 = AlOneCSS.JUST_MASK;
            p.v1 = AlOneCSS.JUST_CENTER;
            return AlSetCSS.applyValue(p, stack);
        } else
        if ("left".contentEquals(val)) {
            p.m1 = AlOneCSS.JUST_MASK;
            p.v1 = AlOneCSS.JUST_LEFT;
            return AlSetCSS.applyValue(p, stack);
        } else
        if ("right".contentEquals(val)) {
            p.m1 = AlOneCSS.JUST_MASK;
            p.v1 = AlOneCSS.JUST_RIGHT;
            return AlSetCSS.applyValue(p, stack);
        } else {
            p.m1 = AlOneCSS.JUST_MASK;
            p.v1 = AlOneCSS.JUST_NONE;
            return AlSetCSS.applyValue(p, stack);
        }

        return false;
    }

    public final static boolean parseFontSize(String val, AlStyleStack stack) {
        long v0 = 0L;

        int news = 0;
        switch (val.charAt(0)) {
            case '7': v0 = AlOneCSS.FONTSIZE_PLUS4  | AlOneCSS.FONTSIZE_ABSOLUTE; break;
            case '6': v0 = AlOneCSS.FONTSIZE_PLUS3  | AlOneCSS.FONTSIZE_ABSOLUTE; break;
            case '5': v0 = AlOneCSS.FONTSIZE_PLUS2  | AlOneCSS.FONTSIZE_ABSOLUTE; break;
            case '4': v0 = AlOneCSS.FONTSIZE_PLUS1  | AlOneCSS.FONTSIZE_ABSOLUTE; break;
            case '3': v0 = AlOneCSS.FONTSIZE_NORMAL | AlOneCSS.FONTSIZE_ABSOLUTE; break;
            case '2': v0 = AlOneCSS.FONTSIZE_MINUS1 | AlOneCSS.FONTSIZE_ABSOLUTE; break;
            case '1': v0 = AlOneCSS.FONTSIZE_MINUS2 | AlOneCSS.FONTSIZE_ABSOLUTE; break;

            case '+':
                switch (val.charAt(1)) {
                    case '8': v0 = AlOneCSS.FONTSIZE_PLUS8; break;
                    case '7': v0 = AlOneCSS.FONTSIZE_PLUS7; break;
                    case '6': v0 = AlOneCSS.FONTSIZE_PLUS6; break;
                    case '5': v0 = AlOneCSS.FONTSIZE_PLUS5; break;
                    case '4': v0 = AlOneCSS.FONTSIZE_PLUS4; break;
                    case '3': v0 = AlOneCSS.FONTSIZE_PLUS3; break;
                    case '2': v0 = AlOneCSS.FONTSIZE_PLUS2; break;
                    case '1': v0 = AlOneCSS.FONTSIZE_PLUS1; break;
                    default: return false;
                }
                break;

            case '-':
                switch (val.charAt(1)) {
                    case '7': v0 = AlOneCSS.FONTSIZE_MINUS7; break;
                    case '6': v0 = AlOneCSS.FONTSIZE_MINUS6; break;
                    case '5': v0 = AlOneCSS.FONTSIZE_MINUS5; break;
                    case '4': v0 = AlOneCSS.FONTSIZE_MINUS4; break;
                    case '3': v0 = AlOneCSS.FONTSIZE_MINUS3; break;
                    case '2': v0 = AlOneCSS.FONTSIZE_MINUS2; break;
                    case '1': v0 = AlOneCSS.FONTSIZE_MINUS1; break;
                    default: return false;
                }
                break;

            default:
                return false;
        }

        if ((v0 &  AlOneCSS.FONTSIZE_ABSOLUTE) != 0) {
            stack.buffer[stack.position].fontSize0 = ((v0 & AlOneCSS.FONTSIZE_MASK_SIZE) >> AlOneCSS.FONTSIZE_VALUE_SHIFT) / 100.0f + 0.5f;
        } else {
            float d = ((float)((v0 & AlOneCSS.FONTSIZE_MASK_SIZE) >> AlOneCSS.FONTSIZE_VALUE_SHIFT)) / 10000;
            stack.buffer[stack.position].fontSize0 = stack.buffer[stack.position - 1].fontSize0 * d;
        }

        return true;
    }

    public final static boolean applyValue0(AlOneCSSPair val, AlOneCSSPair pair) {

        pair.m0 |= val.m0;
        pair.v0 &= ~val.m0;
        pair.v0 |= val.v0;

        pair.m1 |= val.m1;
        pair.v1 &= ~val.m1;
        pair.v1 |= val.v1;

        return true;
    }
    
    public final static boolean applyValue(AlOneCSSPair val, AlStyleStack stack) {
        if (val.m0 != 0) {
			stack.buffer[stack.position].paragraph &= ~val.m0;
            stack.buffer[stack.position].paragraph |= val.v0;
            stack.buffer[stack.position].paragraph &= ~AlStyles.SL3_NUMBER_MASK;

            if ((val.m0 &  AlOneCSS.FONTSIZE_MASK_ALL) != 0) {
                if ((val.v0 &  AlOneCSS.FONTSIZE_ABSOLUTE) != 0) {
                    stack.buffer[stack.position].fontSize0 = ((val.v0 & AlOneCSS.FONTSIZE_MASK_SIZE) >> AlOneCSS.FONTSIZE_VALUE_SHIFT) / 100.0f + 0.5f;
                } else {
                    float d = ((float)((val.v0 & AlOneCSS.FONTSIZE_MASK_SIZE) >> AlOneCSS.FONTSIZE_VALUE_SHIFT)) / 10000;
                    stack.buffer[stack.position].fontSize0 = stack.buffer[stack.position - 1].fontSize0 * d;
                }
            }
        }

        if (val.m1 != 0) {
            stack.buffer[stack.position].prop &= ~val.m1;
            stack.buffer[stack.position].prop |= val.v1;

        }

        return true;
    }

    public final int apply1(int tag, long clsX, AlOneCSSPair pair) {
        int res = 0;

        for (int i = 0; i < setTAG.size(); i++) {
            if (tag == setTAG.get(i).tag) {
                applyValue0(setTAG.get(i).val, pair);
                res = 1;
                break;
            }
        }
        if (clsX != 0) {
            for (int i = 0; i < setCLASS.size(); i++) {
                AlOneCSS o = setCLASS.get(i);
                if (o.clsX == clsX) {
                    applyValue0(o.val, pair);
                    res = 1;
                    break;
                }
            }
            for (int i = 0; i < setTAGCLASS.size(); i++) {
                AlOneCSS o = setTAGCLASS.get(i);
                if (tag == o.tag && o.clsX == clsX) {
                    applyValue0(o.val, pair);
                    res = 1;
                    break;
                }
            }
        }

        return res;
    };

}
