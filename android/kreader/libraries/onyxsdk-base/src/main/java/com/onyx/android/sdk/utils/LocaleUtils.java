package com.onyx.android.sdk.utils;

import java.util.Locale;

/**
 * Created by zengzhu on 2/22/16.
 */
public class LocaleUtils {

    public static boolean isChinese() {
        final String language = Locale.getDefault().getDisplayLanguage();
        return  language.equals(Locale.CHINESE.getDisplayLanguage());
    }

}
