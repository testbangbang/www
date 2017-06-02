package com.onyx.android.sdk.utils;

/**
 * Created by zengzhu on 3/4/16.
 */
public class UnicodeUtils {

    static public boolean isWhitespace(final Character ch) {
        return Character.isWhitespace(ch);
    }

    static public boolean isCJKCharacter(final Character ch) {
        return (Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS);
    }

    static public boolean isPunctuation(final Character ch) {
         return isEnglishPunctuation(ch) || isChinesePunctuation(ch);
    }

    static public boolean isEnglishPunctuation(final Character ch) {
        return ch == ',' || ch == '.' || ch == '!' || ch == '?' || ch == ':' || ch == ';' || ch == '’';
    }

    static public boolean isChinesePunctuation(final Character ch) {
        return ch == '，' || ch == '。' || ch == '？' || ch == '：' || ch == '、' || ch == '；' || ch == '！' || ch == '“' || ch == '”' || ch == '‘' || ch ==  '’';
    }

}
