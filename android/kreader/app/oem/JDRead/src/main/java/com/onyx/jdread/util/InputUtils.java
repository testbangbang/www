package com.onyx.jdread.util;

import com.onyx.android.sdk.utils.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hehai on 18-1-30.
 */

public class InputUtils {

    public static boolean haveSpecialCharacters(String str) {
        String limitEx = "[^a-zA-Z0-9\\u4e00-\\u9fa5]";
        Pattern pattern = Pattern.compile(limitEx);
        Matcher m = pattern.matcher(str);
        return m.find();
    }

    public static int getByteCount(String s) {
        if (StringUtils.isNullOrEmpty(s)) {
            return 0;
        }
        int length = 0;
        for (int i = 0; i < s.length(); i++) {
            int ascii = Character.codePointAt(s, i);
            if (ascii >= 0 && ascii <= 255) {
                length++;
            } else {
                length += 2;
            }
        }
        return length;
    }

    public static String getEffectiveString(String s, int maxByteCount) {
        if (StringUtils.isNullOrEmpty(s)) {
            return s;
        }
        int length = 0;
        int endIndex = s.length() - 1;
        for (int i = 0; i < s.length(); i++) {
            int ascii = Character.codePointAt(s, i);
            if (ascii >= 0 && ascii <= 255) {
                length++;
            } else {
                length += 2;
            }
            if (length > maxByteCount) {
                endIndex = i;
                break;
            }
        }
        return s.substring(0, endIndex);
    }

    public static String filterSpecialCharacters(String string) {
        if (!isHaveAvailableCharacters(string)) {
            return null;
        }
        String regEx = "[^a-zA-Z0-9\\u4e00-\\u9fa5]";
        return string.replaceAll(regEx, "%");
    }

    private static boolean isHaveAvailableCharacters(String string) {
        String limitEx = "[a-zA-Z0-9\\u4e00-\\u9fa5]";
        Pattern pattern = Pattern.compile(limitEx);
        Matcher m = pattern.matcher(string);
        return m.find();
    }
}
