package com.neverland.engbook.util;

import java.util.ArrayList;

public class AlStyleStack {
    public static final int MLEFT = 0;
    public static final int MRIGHT = 1;

    public int				    length;
    public AlOneStyleStack	    buffer[] = null;
    public int				    position;

    //public long			    activeParagraph;
    //public long			    activeProp;

    public int				    lastMarginBottom;

    public boolean				linearMode = false;

    public AlStyleStack() {
        lastMarginBottom = 0;
        position = 0;
        length = 64;
        buffer = new AlOneStyleStack[length];
        for (int i = 0; i < length; i++)
            buffer[i] = new AlOneStyleStack();
        //activeParagraph = &buffer[0].paragraph;
        //activeProp = &buffer[0].prop;
        buffer[0].paragraph = AlStyles.SL_SIZE_NORMAL;
        buffer[0].prop = AlParProperty.SL2_INDENT_DEFAULT | AlParProperty.SL2_INTER_FONT;
    }

    /*@Override
    public void finalize() throws Throwable {
        clearAllArray();
        //styleStack.clear();
        stored_par.data = null;
        super.finalize();
    }
	~AlStyleStack() {
        if (buffer)
            delete[] buffer;
        buffer = NULL;
    }*/

    public int pos() {
        return position;
    }

    public void push() {
        push(0, null);
    }

    public void push(int tag, AlOneXMLAttrClass cls) {
        if (linearMode)
            return;

        buffer[position].inCounter++;

        position++;
        if (position >= length)
            addLength();

        buffer[position].copyFrom(buffer[position - 1]);
        //System.arraycopy(buffer[position - 1], 0, buffer[position], 0, sizeOf(AlOneStyleStack));
        buffer[position].prop &= ~(AlParProperty.SL2_EMPTY_AFTER | AlParProperty.SL2_BREAK_AFTER |
                AlParProperty.SL2_MARGB_MASK | AlParProperty.SL2_MARGT_MASK | AlParProperty.SL2_MARGL_MASK | AlParProperty.SL2_MARGR_MASK);
        buffer[position].tag = tag;
        buffer[position].inCounter = 0;


        if (cls != null && cls.count != 0) {
            cls.copyTo(buffer[position].cls);
            //plf_systemCopy(cls, 0, &buffer[position].cls, 0, sizeof(AlOneXMLAttrClass));
        } else {
            buffer[position].cls.clear();
        }

        //activeParagraph = &buffer[position].paragraph;
        //activeProp = &buffer[position].prop;
    }

    public int getActualSize() {
        int sz;
        if ((buffer[position].paragraph & (AlStyles.STYLE_SUB | AlStyles.STYLE_SUP)) != 0) {
            sz = (int)(buffer[position].fontSize0 / 0.7 + 0.5f);
        } else {
            sz = (int)(buffer[position].fontSize0 + 0.5f);
        }
        if (sz > 511)
            sz = 511;
        return sz;
    }

    public long getActualParagraph() {
        long res = buffer[position].paragraph & AlStyles.SL_SIZE_IMASK;
        int sz;
        if ((buffer[position].paragraph & (AlStyles.STYLE_SUB | AlStyles.STYLE_SUP)) != 0) {
            sz = (int)(buffer[position].fontSize0 / 0.7 + 0.5f);
        } else {
            sz = (int)(buffer[position].fontSize0 + 0.5f);
        }
        if (sz > 511)
            sz = 511;
        res |= sz << AlStyles.SL_SIZE_SHIFT;
        return res;
    }

    public long getActualProp() {
        long res = buffer[position].prop;

        res &= ~(AlParProperty.SL2_MARGT_MASK | AlParProperty.SL2_MARG_MASK_WIDTH);

        int top = lastMarginBottom, ml, mr;
        float width = 300.0f, left = 0, right = 0;
        for (int i = 1; i <= position; i++) {
            top += (buffer[i].prop & AlParProperty.SL2_MARGT_MASK) >> AlParProperty.SL2_MARGT_SHIFT;

            ml = (int) ((buffer[i].prop & AlParProperty.SL2_MARGL_MASK) >> AlParProperty.SL2_MARGL_SHIFT);
            mr = (int) ((buffer[i].prop & AlParProperty.SL2_MARGR_MASK) >> AlParProperty.SL2_MARGR_SHIFT);

            if (mr != 0 || ml != 0) {

                left += (float)ml * width / 300;
                right += (float)mr * width / 300;

                width = 300 - left - right;
            }
        }
        lastMarginBottom = 0;

        if (top > AlParProperty.SL2_MARG_MAX_VALUE)
            top = (int) AlParProperty.SL2_MARG_MAX_VALUE;
        res |= ((long)top << AlParProperty.SL2_MARGT_SHIFT);

        if (left < 0)
            left = 0;
        if (right < 0)
            right = 0;

        ml = (int)(left + 0.5);
        mr = (int)(right + 0.5);
        while (ml + mr > 270) {
            ml--;
            mr--;
        }

        res |= ((long)ml << AlParProperty.SL2_MARGL_SHIFT);
        res |= ((long)mr << AlParProperty.SL2_MARGR_SHIFT);

        return res;
    }

    public void pop(int tag) {
        if (linearMode)
            return;

        for (int i = position; i > 0; i--) {
            if (buffer[i].tag == tag) {
                long has_flags = ((buffer[position].prop) & (AlParProperty.SL2_EMPTY_AFTER | AlParProperty.SL2_BREAK_AFTER/* | AlParProperty::SL2_MARGB_MASK*/))
					>> AlParProperty.SL2_SHIFT_FOR_AFTER;

                lastMarginBottom += ((buffer[position].prop) & AlParProperty.SL2_MARGB_MASK) >> AlParProperty.SL2_MARGB_SHIFT;

                position = i - 1;

                //activeParagraph = &buffer[position].paragraph;
                //activeProp = &buffer[position].prop;

                //(*activeProp) |= has_flags;
                buffer[position].prop |= has_flags;
                break;
            }
        }
    }

    public void pop() {
        pop(0);
    }

    public boolean haveParentTAG(int tag) {
        for (int i = position - 1; i > 0; i--) {
            if (buffer[i].tag == tag)
                return true;
        }
        return false;
    }

    public void clearFlagInAllParentsParagraph(long flag) {
        for (int i = position; i > 0; i--) {
            //if ((buffer[i].paragraph & flag) == 0)
            //	return;
            buffer[i].paragraph &= ~flag;
        }
    }

    public void clearFlagInAllParentsProp(long flag) {
        for (int i = position; i > 0; i--) {
            //if ((buffer[i].paragraph & flag) == 0)
            //	return;
            buffer[i].prop &= ~flag;
        }
    }

    public void addLength() {
        AlOneStyleStack	tmp[] = new AlOneStyleStack[length + 64];
        for (int i = 0; i < length; i++)
            tmp[i] = buffer[i];
        for (int i = length; i < length + 64; i++)
            tmp[i] = new AlOneStyleStack();
        //plf_systemCopy(&buffer[0], 0, &tmp[0], 0, sizeof(TAL_OneStyleStack) * length);
        //delete[] buffer;
        buffer = tmp;
        System.gc();
        length += 64;
    }

    public void clear() {
        position = 0;
    }

}
