package com.onyx.jdread.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hehai on 18-1-30.
 */

public class InputUtils {

    public static boolean haveSpecialCharacters(String str) {
        String limitEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@①#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern pattern = Pattern.compile(limitEx);
        Matcher m = pattern.matcher(str);
        return m.find();
    }

    public static int getByteCount(String s) {
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

    public static String filterSpecialCharacters(String string) {
        String regEx = "[a-zA-Z0-9\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(string);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            sb.append(m.group());
        }
        return sb.toString();
    }
}
