package com.onyx.android.sdk.utils;

import java.util.Locale;

/**
 * Created by zengzhu on 2/22/16.
 */
public class LocaleUtils {

    public static final int AUTO			= -1;

    public static final int CP437			= 437;
    public static final int CP850			= 850;
    public static final int CP855			= 855;
    public static final int CP860			= 860;
    public static final int CP861			= 861;
    public static final int CP863			= 863;
    public static final int CP865			= 865;
    public static final int CP866			= 866;
    public static final int CP874			= 874;

    public static final int CP932			= 932;
    public static final int CP936			= 936;
    public static final int CP949			= 949;
    public static final int CP950			= 950;

    public static final int CP1200			= 1200;
    public static final int CP1201			= 1201;
    public static final int CP1250			= 1250;
    public static final int CP1251			= 1251;
    public static final int CP1252			= 1252;
    public static final int CP1253			= 1253;
    public static final int CP1254			= 1254;
    public static final int CP1255			= 1255;
    public static final int CP1256			= 1256;
    public static final int CP1257			= 1257;
    public static final int CP1258			= 1258;

    public static final int CP10000			= 10000;
    public static final int CP10007			= 10007;
    public static final int CP10017			= 10017;
    public static final int CP10079			= 10079;
    public static final int CP20127			= 20127;
    public static final int CP20866			= 20866;
    public static final int CP21866			= 21866;
    public static final int CP28591			= 28591;
    public static final int CP28592			= 28592;
    public static final int CP28595			= 28595;
    public static final int CP28605			= 28605;

    public static final int CP65001			= 65001;

    public static boolean isChinese() {
        final String language = Locale.getDefault().getDisplayLanguage();
        return  language.equals(Locale.CHINESE.getDisplayLanguage());
    }

    public static int getLocaleDefaultCodePage() {
        switch (Locale.getDefault().getLanguage()) {
            case "zh":
                return CP936;
            case "ja":
                return CP932;
            case "ko":
                return CP949;
            case "ru":
                return CP1251;
            default:
                return CP65001;
        }
    }

}
