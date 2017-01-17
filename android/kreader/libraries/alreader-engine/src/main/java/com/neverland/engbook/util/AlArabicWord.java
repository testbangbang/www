package com.neverland.engbook.util;

class AlArabicWord {
    public int     start;
    public int     end;
    public int     type;

    static final int ARABIC_WORD_TYPE_NONE = 0;
    static final int ARABIC_WORD_TYPE_ARABIC = 1;
    static final int ARABIC_WORD_TYPE_SPACE = 2;
    static final int ARABIC_WORD_TYPE_PUNCTO = 3;
    static final int ARABIC_WORD_TYPE_NORMAL = 4;
    static final int ARABIC_WORD_TYPE_DIGIT = 5;
    static final int ARABIC_WORD_TYPE_OTHER = 6;
}
