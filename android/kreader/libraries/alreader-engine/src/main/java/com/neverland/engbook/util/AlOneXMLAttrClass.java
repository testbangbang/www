package com.neverland.engbook.util;

public class AlOneXMLAttrClass {
    public static final int LEVEL2_CSS_CLS_COUNT = 8;

    public int		count = 0x00;
    public long[]		hash = new long[LEVEL2_CSS_CLS_COUNT];

    public AlOneXMLAttrClass() {
        clear();
    }

    public void clear() {
        count = 0x00;
    }

    public void start() {
        hash[count] = 0x00;
    }

    public void add(char ch) {
        if (count < LEVEL2_CSS_CLS_COUNT) {
            hash[count] = hash[count] * 31 + ch;
        }
    }

    public void end() {
        if (hash[count] != 0 && count < LEVEL2_CSS_CLS_COUNT)
            count++;
    }

    public void copyTo(AlOneXMLAttrClass dst) {
        dst.count = count;
        System.arraycopy(hash, 0, dst.hash, 0, LEVEL2_CSS_CLS_COUNT);
    }

    public void copyFrom(AlOneXMLAttrClass src) {
        count = src.count;
        System.arraycopy(src.hash, 0, hash, 0, LEVEL2_CSS_CLS_COUNT);
    }
}
