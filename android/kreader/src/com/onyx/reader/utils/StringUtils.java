package com.onyx.reader.utils;

/**
 * Created by zhuzeng on 10/16/15.
 */
public class StringUtils {


    static public boolean isNullOrEmpty(final String string) {
        return (string == null || string.length() <= 0);
    }

    static public boolean isNonBlank(final String string) {
        return (string != null && string.length() > 0);
    }


}
