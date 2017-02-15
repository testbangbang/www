package com.onyx.android.sdk.utils;

/**
 * Created by joy on 2/15/17.
 */

public class ChineseTextUtils {

    public static String removeWhiteSpacesBetweenChineseText(String text) {
        if (StringUtils.isBlank(text)) {
            return "";
        }
        text = StringUtils.deleteNewlineSymbol(text);
        final String pattern = ".*[\\u4E00-\\u9FA5][\\s]+[\\u4E00-\\u9FA5].*";
        while (text.matches(pattern)) {
            text = text.replaceAll("([\\u4E00-\\u9FA5])[\\s]+([\\u4E00-\\u9FA5])", "$1$2");
        }
        return text;
    }

}
