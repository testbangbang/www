package com.onyx.android.sun.devicesetting;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.onyx.android.sdk.utils.ReflectUtil;
import com.onyx.android.sun.R;

import java.lang.reflect.Method;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by huxiaomao on 2016/12/15.
 */

public class SystemLanguage {
    public static Locale getCurrentLanguage(final Context context) {
        Configuration conf = context.getResources().getConfiguration();
        return conf.locale;
    }

    public static String getCurrentLanguageDisplayName(final Context context) {
        Configuration conf = context.getResources().getConfiguration();
        String locale = conf.locale.getDisplayName(conf.locale);
        if (locale != null && locale.length() > 1) {
            locale = Character.toUpperCase(locale.charAt(0)) + locale.substring(1);
        }
        return locale;
    }

    public static boolean updateLocale(Locale locale) {
        boolean bRet = true;
        try {
            Class<?> localePickerClass = ReflectUtil.classForName("com.android.internal.app.LocalePicker");
            Method methodLocalePicker = ReflectUtil.getMethodSafely(localePickerClass, "updateLocale", Locale.class);
            invokeDeviceControllerMethod(methodLocalePicker, locale);
        } catch (Exception e) {
            e.printStackTrace();
            bRet = false;
        }
        return bRet;
    }

    private static Object invokeDeviceControllerMethod(Method method, Object... args) {
        if (method == null) {
            return null;
        }
        return ReflectUtil.invokeMethodSafely(method, null, args);
    }

    public static SystemLanguageInformation getSystemLanguageList(final Context context) {
        Locale locale = getCurrentLanguage(context);

        final String[] locales = Resources.getSystem().getAssets().getLocales();
        final String[] specialLocaleCodes = context.getResources().getStringArray(R.array.special_locale_codes);
        final String[] specialLocaleNames = context.getResources().getStringArray(R.array.special_locale_names);
        Arrays.sort(locales);
        final int origSize = locales.length;
        final LocaleLanguageInfo[] localeInfo = new LocaleLanguageInfo[origSize];
        int finalSize = 0;
        for (int i = 0; i < origSize; i++) {
            final String s = locales[i];
            final int len = s.length();
            if (len == 5) {
                String language = s.substring(0, 2);
                String country = s.substring(3, 5);
                final Locale l = new Locale(language, country);
                if (finalSize == 0) {
                    localeInfo[finalSize++] = new LocaleLanguageInfo(toTitleCase(l.getDisplayLanguage(l)), l);
                } else {
                    // check previous entry:
                    //  same lang and a country -> upgrade to full name and
                    //    insert ours with full name
                    //  diff lang -> insert ours with lang-only name
                    if (localeInfo[finalSize - 1].locale.getLanguage().equals(language)) {
                        localeInfo[finalSize - 1].label = toTitleCase(getDisplayName(localeInfo[finalSize - 1].locale,
                                specialLocaleCodes, specialLocaleNames));
                        localeInfo[finalSize++] = new LocaleLanguageInfo(toTitleCase(getDisplayName(l, specialLocaleCodes, specialLocaleNames)), l);
                    } else {
                        String displayName;
                        if (s.equals("zz_ZZ")) {
                            displayName = "Pseudo...";
                        } else {
                            displayName = toTitleCase(l.getDisplayLanguage(l));
                        }
                        localeInfo[finalSize++] = new LocaleLanguageInfo(displayName, l);
                    }
                }
            }
        }
        SystemLanguageInformation systemLanguageInformation = new SystemLanguageInformation();
        systemLanguageInformation.localeLanguageInfoList = new ArrayList<>();
        for (int i = 0; i < finalSize; i++) {
            LocaleLanguageInfo localeLanguageInfo = localeInfo[i];
            if(localeLanguageInfo.getLocale().getCountry().equals(locale.getCountry())){
                systemLanguageInformation.currentLanguage = localeLanguageInfo.getLabel();
            }
            systemLanguageInformation.localeLanguageInfoList.add(localeLanguageInfo);
        }
        return systemLanguageInformation;
    }

    public static class LocaleLanguageInfo implements Comparable<LocaleLanguageInfo> {
        static final Collator sCollator = Collator.getInstance();

        String label;
        Locale locale;

        public LocaleLanguageInfo(String label, Locale locale) {
            this.label = label;
            this.locale = locale;
        }

        public String getLabel() {
            return label;
        }

        public Locale getLocale() {
            return locale;
        }

        @Override
        public String toString() {
            return this.label;
        }

        @Override
        public int compareTo(LocaleLanguageInfo another) {
            return sCollator.compare(this.label, another.label);
        }
    }

    private static String toTitleCase(String s) {
        if (s.length() == 0) {
            return s;
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static String getDisplayName(
            Locale l, String[] specialLocaleCodes, String[] specialLocaleNames) {
        String code = l.toString();
        for (int i = 0; i < specialLocaleCodes.length; i++) {
            if (specialLocaleCodes[i].equals(code)) {
                return specialLocaleNames[i];
            }
        }
        return l.getDisplayName(l);
    }
}
