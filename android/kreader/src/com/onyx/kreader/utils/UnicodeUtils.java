package com.onyx.kreader.utils;

/**
 * Created by zengzhu on 3/4/16.
 */
public class UnicodeUtils {

    static public boolean isCJKCharacter(final Character ch) {
        return (Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS);
    }
}
