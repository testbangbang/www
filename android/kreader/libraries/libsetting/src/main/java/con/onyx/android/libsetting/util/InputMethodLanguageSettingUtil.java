package con.onyx.android.libsetting.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.Preference;
import android.provider.Settings;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import com.onyx.android.sdk.utils.ReflectUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import con.onyx.android.libsetting.R;
import con.onyx.android.libsetting.model.LocaleInfo;

/**
 * Created by solskjaer49 on 2016/12/05 10:31.
 */
public class InputMethodLanguageSettingUtil {
    private static final String TAG = InputMethodLanguageSettingUtil.class.getSimpleName();

    public static boolean isInputMethodPreference(List<InputMethodInfo> mImis, Preference preference) {
        for (InputMethodInfo inputMethodInfo : mImis) {
            if (inputMethodInfo.getId().equals(preference.getKey())) {
                return true;
            }
        }
        return false;
    }

    public static List<InputMethodInfo> getCurrentEnableIMEList(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.getEnabledInputMethodList();
    }

    public static boolean isSpecificIMEEnabled(Context context, String imeID) {
        for (InputMethodInfo info : getCurrentEnableIMEList(context)) {
            if (info.getId().equalsIgnoreCase(imeID)) {
                return true;
            }
        }
        return false;
    }

    public static boolean enableSpecificIME(Context context, String imeID) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        for (InputMethodInfo info : imm.getInputMethodList()) {
            if (info.getId().equalsIgnoreCase(imeID)) {
                Settings.Secure.putString(context.getContentResolver(), Settings.Secure.ENABLED_INPUT_METHODS, (Settings.Secure.getString(
                        context.getContentResolver(), Settings.Secure.ENABLED_INPUT_METHODS) + ":" + imeID));
                return true;
            }
        }
        return false;
    }

    public static void setSpecificIMEDefault(Context context, String imeID) {
        Settings.Secure.putString(
                context.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD, imeID);
    }

    public static void updateLocale(Locale locale) {
        try {
            Class<?> localePickerClass = ReflectUtil.classForName("com.android.internal.app.LocalePicker");
            Method methodLocalePicker = ReflectUtil.getMethodSafely(localePickerClass, "updateLocale", Locale.class);
            ReflectUtil.invokeMethodSafely(methodLocalePicker, null, locale);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCurrentLanguage(Context context) {
        Configuration conf = context.getResources().getConfiguration();
        Locale locale;
        if (CommonUtil.apiLevelCheck(Build.VERSION_CODES.N)) {
            locale = conf.getLocales().get(0);
        } else {
            locale = conf.locale;
        }
        String langName = locale.getDisplayName();
        if (langName != null && langName.length() > 1) {
            langName = Character.toUpperCase(langName.charAt(0)) + langName.substring(1);
        }
        return langName;
    }

    public static List<LocaleInfo> buildLanguageList(Context context) {
        String[] locales = Resources.getSystem().getAssets().getLocales();
        final int origSize = locales.length;
        final LocaleInfo[] preprocess = new LocaleInfo[origSize];
        final String[] specialLocaleCodes = context.getResources().getStringArray(R.array.special_locale_codes);
        final String[] specialLocaleNames = context.getResources().getStringArray(R.array.special_locale_names);
        int finalSize = 0;
        Arrays.sort(locales);
        for (final String s : locales) {
            final int len = s.length();
            if (len == 5) {
                String language = s.substring(0, 2);
                String country = s.substring(3, 5);
                final Locale l = new Locale(language, country);
                if (finalSize == 0) {
                    preprocess[finalSize++] = new LocaleInfo(toTitleCase(l.getDisplayLanguage(l)), l);
                } else {
                    // check previous entry:
                    //  same lang and a country -> upgrade to full name and
                    //    insert ours with full name
                    //  diff lang -> insert ours with lang-only name
                    if (preprocess[finalSize - 1].getLocale().getLanguage().equals(language)) {
                        preprocess[finalSize - 1].setLabel(toTitleCase(getDisplayName(preprocess[finalSize - 1].getLocale(),
                                specialLocaleCodes, specialLocaleNames)));
                        preprocess[finalSize++] = new LocaleInfo(toTitleCase(getDisplayName(l, specialLocaleCodes, specialLocaleNames)), l);
                    } else {
                        String displayName;
                        if (s.equals("zz_ZZ")) {
                            displayName = "Pseudo...";
                        } else {
                            displayName = toTitleCase(l.getDisplayLanguage(l));
                        }
                        preprocess[finalSize++] = new LocaleInfo(displayName, l);
                    }
                }
            }
        }
        ArrayList<LocaleInfo> dataList = new ArrayList<>(Arrays.asList(preprocess));
        dataList.removeAll(Collections.singleton(null));
        return dataList;
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

    private static String toTitleCase(String s) {
        if (s.length() == 0) {
            return s;
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

}
