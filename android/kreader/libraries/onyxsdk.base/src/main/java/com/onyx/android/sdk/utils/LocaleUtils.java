package com.onyx.android.sdk.utils;

import java.util.Locale;

/**
 * Created by zengzhu on 12/29/15.
 */
public class LocaleUtils {

    public static boolean isChinese() {
        final String language = Locale.getDefault().getDisplayLanguage();
        return  language.equals(Locale.CHINESE.getDisplayLanguage());
    }

}
